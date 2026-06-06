package net.sistr.littlemaidmodelloader.client.resource;

import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackPosition;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

// PackFinderはリソースパックを探すクラス
// これをResourcePackListに突っ込むことで、ゲーム内リソースパックから選ぶことができる
// Fabric/Forgeでも似たようなことをやってModのリソースを読み込んでいる
@Environment(EnvType.CLIENT)
public class LMPackProvider implements ResourcePackProvider {
    public static final ResourcePackSource RESOURCE_PACK_SOURCE =
            new ResourcePackSource() {
                @Override
                public Text decorate(Text packName) {
                    return Text.translatable(
                            "pack.nameAndSource",
                            packName,
                            Text.translatable("pack.source.littlemaidmodelloader"));
                }

                @Override
                public boolean canBeEnabledLater() {
                    return true;
                }
            };

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        MutableText title = Text.translatable("pack.name.littlemaidmodelloader");
        ResourcePackInfo info =
                new ResourcePackInfo(
                        "LittleMaid ModelLoader",
                        title,
                        RESOURCE_PACK_SOURCE,
                        Optional.empty());
        ResourcePackPosition position =
                new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, false);
        ResourcePackProfile.PackFactory packFactory =
                new ResourcePackProfile.PackFactory() {
                    @Override
                    public ResourcePack open(ResourcePackInfo packInfo) {
                        return ResourceWrapper.INSTANCE;
                    }

                    @Override
                    public ResourcePack openWithOverlays(
                            ResourcePackInfo packInfo, ResourcePackProfile.Metadata metadata) {
                        return ResourceWrapper.INSTANCE;
                    }
                };
        var profile =
                ResourcePackProfile.create(
                        info, packFactory, ResourceType.CLIENT_RESOURCES, position);
        profileAdder.accept(profile);
    }
}
