package net.sistr.littlemaidmodelloader.resource.classloader;

import me.shedaniel.architectury.platform.Platform;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MultiModelClassLoader extends URLClassLoader {
    private final MultiModelClassTransformer transformer = new MultiModelClassTransformer();

    public MultiModelClassLoader(List<Path> folderPaths) {
        super(getClassLoaderURL(folderPaths), MultiModelClassLoader.class.getClassLoader());
    }

    /**
     * クラスローダー参照先を設定
     */
    private static URL[] getClassLoaderURL(List<Path> folderPaths) {
        //フォルダに含まれるファイルのURLをリストにする
        List<URL> urlList = new ArrayList<>();
        folderPaths.forEach(folderPath -> {
            try {
                try (Stream<Path> stream = Files.walk(folderPath)) {
                    stream.filter(resourcePath -> !Files.isDirectory(resourcePath))
                            .map(resourcePath -> {
                                try {
                                    return resourcePath.toUri().toURL();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            })
                            .forEach(urlList::add);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return urlList.toArray(new URL[urlList.size()]);
    }

    /**
     * class検索と登録
     */
    @Override
    protected Class<?> findClass(final String className) throws ClassNotFoundException {
        InputStream inputstream = this.getResourceAsStream(className.replace(".", "/") + ".class");
        if (inputstream == null) {
            throw new ClassNotFoundException(className + ":inputstream:" + className.replace(".", "/") + ".class");
        }
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputstream);
        } catch (Exception e) {
            throw new ClassNotFoundException(className + ":toByteArray[" + e + "]");
        }
        if (bytes == null) {
            throw new ClassNotFoundException(className + ":bytes");
        }
        //クラス変換
        byte[] transBytes = transformer.transform(bytes);
        try {
            return defineClass(className, transBytes, 0, transBytes.length);
        } catch (Exception e) {
            throw new ClassNotFoundException(className + ":defineClass_Exception:[" + e + "]");
        } catch (Error e) {
            throw new ClassNotFoundException(className + ":defineClass_Error:[" + e + "]");
        }
    }

    //todo 今も必要？
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (Platform.isForge()) {
            try {
                return super.loadClass(name, resolve);
            } catch (IndexOutOfBoundsException e) {//ForgeのModuleClassLoaderがsubstringで落ちた場合
                if (name.lastIndexOf('.') == -1) {
                    Class<?> c = findClass(name);
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                }
            }
            throw new ClassNotFoundException(name);
        } else {
            return super.loadClass(name, resolve);
        }
    }

}