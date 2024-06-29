package net.sistr.littlemaidmodelloader.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.*;

import java.util.function.Consumer;

//PackFinderはリソースパックを探すクラス
//これをResourcePackListに突っ込むことで、ゲーム内リソースパックから選ぶことができる
//Fabric/Forgeでも似たようなことをやってModのリソースを読み込んでいる
@Environment(EnvType.CLIENT)
public class LMPackProvider implements ResourcePackProvider {

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        var profile = ResourcePackProfile.create(
                ResourceWrapper.INSTANCE.getInfo(),
                new ResourcePackProfile.PackFactory() {
                    @Override
                    public ResourcePack open(ResourcePackInfo resourcePackInfo) {
                        return ResourceWrapper.INSTANCE;
                    }

                    @Override
                    public ResourcePack openWithOverlays(ResourcePackInfo resourcePackInfo, ResourcePackProfile.Metadata metadata) {
                        return open(resourcePackInfo);
                    }
                },
                ResourceType.CLIENT_RESOURCES,
                new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, false));
        profileAdder.accept(profile);
    }
}
