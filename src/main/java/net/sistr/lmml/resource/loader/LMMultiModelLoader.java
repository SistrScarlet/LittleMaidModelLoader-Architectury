package net.sistr.lmml.resource.loader;

import net.sistr.lmml.LittleMaidModelLoader;
import net.sistr.lmml.config.LMMLConfig;
import net.sistr.lmml.maidmodel.ModelMultiBase;
import net.sistr.lmml.resource.manager.LMModelManager;

import java.io.InputStream;
import java.nio.file.Path;

public class LMMultiModelLoader implements LMLoader {
    private final LMModelManager modelManager;
    private final ClassLoader classLoader;

    public LMMultiModelLoader(LMModelManager modelManager, ClassLoader classLoader) {
        this.modelManager = modelManager;
        this.classLoader = classLoader;
    }

    @Override
    public boolean canLoad(String path, Path homePath, InputStream inputStream, boolean isArchive) {
        return path.endsWith(".class") && (path.contains("ModelMulti_") || path.contains("ModelLittleMaid_"));
    }

    @Override
    public void load(String path, Path homePath, InputStream inputStream, boolean isArchive) {
        //ClassLoader用パスへ変換
        String classpath = path.replace("/", ".");
        classpath = classpath.substring(0, path.lastIndexOf(".class"));

        try {
            tryAddModel(classpath, classForName(classpath));
        } catch (Exception e) {
            LittleMaidModelLoader.LOGGER.error("読み込めませんでした。古いモデルの可能性があります : " + path);
            if (LMMLConfig.isDebugMode()) e.printStackTrace();
        }
    }

    //クラスがModelMultiBaseであるかチェック
    @SuppressWarnings("unchecked")
    private void tryAddModel(String classpath, Class<?> modelClass) {
        if (modelClass != null && ModelMultiBase.class.isAssignableFrom(modelClass)) {
            int lastSplitter = classpath.lastIndexOf("_");
            //アンダーバー無しモデルはモデルとして読み込まない
            if (lastSplitter == -1) {
                return;
            }
            //最後の_から前を削除
            String className = classpath.toLowerCase().substring(lastSplitter + 1);
            //クラスを登録
            modelManager.addModel(className, (Class<? extends ModelMultiBase>) modelClass);
        }
    }

    public Class<?> classForName(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className, true, classLoader);
        } catch (Error e) {
            throw new ClassNotFoundException(className + ":classForName_Error:[" + e.toString() + "]");
        }
    }


}
