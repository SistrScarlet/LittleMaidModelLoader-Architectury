package net.sistr.littlemaidmodelloader.client.resource;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MinecraftVersion;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//外部から読み込んだリソースをマイクラに送るラッパー
@Environment(EnvType.CLIENT)
public class ResourceWrapper implements ResourcePack {
    public static final ResourceWrapper INSTANCE = new ResourceWrapper();
    public static final PackResourceMetadata PACK_INFO =
            new PackResourceMetadata(Text.literal("LittleMaid ModelLoader!!!"),
                    MinecraftVersion.CURRENT.getResourceVersion(ResourceType.CLIENT_RESOURCES));
    protected static final HashMap<Identifier, Resource> PATHS = Maps.newHashMap();

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        return null;
    }

    //引数のResourceLocationはlittlemaidmodelloader:textures/...の形式
    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        Resource resource = PATHS.get(id);
        if (resource == null) {
            return null;
        }
        return resource::getInputStream;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        PATHS.entrySet().stream()
                .filter(entry -> entry.getKey().getNamespace().equals(namespace))
                .filter(entry -> entry.getKey().getPath().startsWith(prefix))
                .forEach(e -> consumer.accept(e.getKey(), () -> e.getValue().getInputStream()));
    }

    @Override
    public boolean isAlwaysStable() {
        return true;
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

    private record Resource(String path, Path homePath, boolean isArchive) {

        public InputStream getInputStream() throws IOException {
            if (isArchive) {
                String resourcePath = homePath.toString();
                //try with resourcesしてはいけない
                //取った結果を返すとき、closeしてしまう
                ZipFile zipfile = new ZipFile(resourcePath);
                ZipEntry zipentry = zipfile.getEntry(path);
                if (zipentry == null) {
                    zipfile.close();
                    throw new NoSuchFileException(path);
                } else {
                    return zipfile.getInputStream(zipentry);
                }
            } else {
                return Files.newInputStream(Paths.get(homePath.toString(), path));
            }
        }

    }

}
