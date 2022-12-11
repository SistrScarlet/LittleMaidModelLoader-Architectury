package net.sistr.littlemaidmodelloader.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.function.Consumer;

//PackFinderはリソースパックを探すクラス
//これをResourcePackListに突っ込むことで、ゲーム内リソースパックから選ぶことができる
//Fabric/Forgeでも似たようなことをやってModのリソースを読み込んでいる
@Environment(EnvType.CLIENT)
public class LMPackProvider implements ResourcePackProvider {
    public static final ResourcePackSource RESOURCE_PACK_SOURCE = new ResourcePackSource() {
        @Override
        public Text decorate(Text packName) {
            return Text.translatable("pack.nameAndSource", packName, Text.translatable("pack.source.littlemaidmodelloader"));
        }

        @Override
        public boolean canBeEnabledLater() {
            return true;
        }
    };

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        MutableText title = Text.translatable("pack.name.littlemaidmodelloader");
        var profile = ResourcePackProfile.create(
                "LittleMaid ModelLoader",
                title,
                true,
                factory -> ResourceWrapper.INSTANCE,
                ResourceType.CLIENT_RESOURCES,
                ResourcePackProfile.InsertionPosition.TOP,
                RESOURCE_PACK_SOURCE);
        profileAdder.accept(profile);
    }
}
