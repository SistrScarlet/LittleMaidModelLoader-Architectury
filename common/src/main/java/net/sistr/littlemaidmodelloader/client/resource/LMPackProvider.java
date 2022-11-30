package net.sistr.littlemaidmodelloader.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;

import java.util.function.Consumer;

//PackFinderはリソースパックを探すクラス
//これをResourcePackListに突っ込むことで、ゲーム内リソースパックから選ぶことができる
//Fabric/Forgeでも似たようなことをやってModのリソースを読み込んでいる
@Environment(EnvType.CLIENT)
public class LMPackProvider implements ResourcePackProvider {
    public static final ResourcePackSource RESOURCE_PACK_SOURCE =
            text -> Text.translatable("pack.nameAndSource", text, Text.translatable("pack.source.littlemaidmodelloader"));

    @Override
    public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
        ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(
                "LittleMaid ModelLoader",
                true,
                () -> ResourceWrapper.INSTANCE,
                factory,
                ResourcePackProfile.InsertionPosition.TOP,
                RESOURCE_PACK_SOURCE
        );
        consumer.accept(resourcePackProfile);
    }
}
