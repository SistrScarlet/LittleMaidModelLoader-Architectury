package net.sistr.lmml.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


//外部から読み込んだリソースをマイクラに送るラッパー
@Environment(EnvType.CLIENT)
public class ResourceWrapper implements ResourcePack {
    public static final ResourceWrapper INSTANCE = new ResourceWrapper();
    public static final PackResourceMetadata PACK_INFO =
            new PackResourceMetadata(new LiteralText("LittleMaidModelLoader!!!"), 6);
    protected static final HashMap<Identifier, Resource> PATHS = Maps.newHashMap();

    //いつ呼ばれるのか不明
    //少なくとも起動時とリロード時は動かず
    @Override
    public InputStream openRoot(String fileName) {
        return null;
    }

    //引数のResourceLocationはlittlemaidmodelloader:textures/...の形式
    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException {
        Resource resource = PATHS.get(id);
        if (resource == null) {
            throw new FileNotFoundException(id.toString());
        }
        return resource.getInputStream();
    }

    //リロード時に呼ばれる
    //pathInが重要で、これと一致するリソースのみ渡すこと
    //そうでないと画像リソースがフォントに飛んでってクソ時間を食う
    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        return PATHS.keySet().stream()
                .filter(location -> location.getPath().startsWith(namespace))
                .filter(location -> pathFilter.test(location.getPath()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        return PATHS.containsKey(id);
    }

    //初期化時に読み込まれる
    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return Sets.newHashSet("littlemaidmodelloader");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        if (metaReader.getKey().equals("pack")) {
            return (T) PACK_INFO;
        }
        return null;
    }

    @Override
    public String getName() {
        return "LMModelLoader";
    }

    @Override
    public void close() {

    }

    public static void addResourcePath(Identifier resourcePath, String path, Path homePath, boolean isArchive) {
        PATHS.put(resourcePath, new Resource(path, homePath, isArchive));
    }

    private static class Resource {
        private final String path;
        private final Path homePath;
        private final boolean isArchive;

        private Resource(String path, Path homePath, boolean isArchive) {
            this.path = path;
            this.homePath = homePath;
            this.isArchive = isArchive;
        }

        public InputStream getInputStream() throws IOException {
            if (isArchive) {
                String resourcePath = homePath.toString();
                ZipFile zipfile = new ZipFile(resourcePath);
                ZipEntry zipentry = zipfile.getEntry(path);
                if (zipentry != null) {
                    return zipfile.getInputStream(zipentry);
                }
                //上記より遅い
                /*ZipInputStream zipStream = new ZipInputStream(Files.newInputStream(homePath));
                ZipEntry entry;
                while ((entry = zipStream.getNextEntry()) != null) {
                    if (entry.getName().equals(path)) {
                        return zipStream;
                    }
                }*/
                throw new NoSuchFileException(path);
            } else {
                return Files.newInputStream(Paths.get(homePath.toString(), path));
            }
        }

    }

}
