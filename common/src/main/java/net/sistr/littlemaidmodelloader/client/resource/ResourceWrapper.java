package net.sistr.littlemaidmodelloader.client.resource;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//外部から読み込んだリソースをマイクラに送るラッパー
@Environment(EnvType.CLIENT)
public class ResourceWrapper implements ResourcePack {
    public static final ResourceWrapper INSTANCE = new ResourceWrapper();
    private static final String PACK_ID = "lmmlresources";
    private static final PackResourceMetadata METADATA =
            new PackResourceMetadata(Text.translatable("pack.description." + PACK_ID),
                    SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES),
                    Optional.empty());
    private static final HashMap<Identifier, Resource> PATHS = Maps.newHashMap();
    private static final ResourcePackSource RESOURCE_PACK_SOURCE = new ResourcePackSource() {
        @Override
        public Text decorate(Text packName) {
            return Text.translatable(
                    "pack.nameAndSource",
                    packName,
                    Text.translatable("pack.source." + PACK_ID)
            );
        }

        @Override
        public boolean canBeEnabledLater() {
            return true;
        }
    };
    private static final ResourcePackInfo PACK_INFO = new ResourcePackInfo(
            PACK_ID,
            Text.translatable("pack.name." + PACK_ID),
            RESOURCE_PACK_SOURCE,
            Optional.empty()
    );

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

    //初期化時に読み込まれる
    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return PATHS.keySet().stream()
                .map(Identifier::getNamespace)
                .collect(Collectors.toSet());
    }

    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        return ResourceMetadataMap.of(PackResourceMetadata.SERIALIZER, METADATA).get(metaReader);
    }

    @Override
    public ResourcePackInfo getInfo() {
        return PACK_INFO;
    }

    @Override
    public String getId() {
        return PACK_ID;
    }

    @Override
    public void close() {

    }

    public static void addResourcePath(Identifier resourcePath, String path, Path homePath, boolean isArchive) {
        PATHS.put(resourcePath, new Resource(path, homePath, isArchive));
    }

    public record Resource(String path, Path homePath, boolean isArchive) {

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
