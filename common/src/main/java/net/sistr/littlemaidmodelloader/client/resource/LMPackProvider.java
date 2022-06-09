package net.sistr.littlemaidmodelloader.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

//PackFinderはリソースパックを探すクラス
//これをResourcePackListに突っ込むことで、ゲーム内リソースパックから選ぶことができる
//Forgeでも同じことをやってModのリソースを読み込んでいる
@Environment(EnvType.CLIENT)
public class LMPackProvider implements ResourcePackProvider {

    @Override
    public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
        String name = "littlemaidmodelloader";
        boolean isAlwaysEnabled = true;
        ResourcePack resourcePack = ResourceWrapper.INSTANCE;
        Supplier<ResourcePack> supplier = () -> resourcePack;
        PackResourceMetadata resourcePackMeta = ResourceWrapper.PACK_INFO;
        ResourcePackProfile.InsertionPosition priority = ResourcePackProfile.InsertionPosition.TOP;
        ResourcePackSource decorator = ResourcePackSource.PACK_SOURCE_BUILTIN;
        ResourcePackProfile info = factory.create(
                name,
                Text.literal(resourcePack.getName()),
                isAlwaysEnabled,
                supplier,
                resourcePackMeta,
                priority,
                decorator
        );
        consumer.accept(info);
    }
}
