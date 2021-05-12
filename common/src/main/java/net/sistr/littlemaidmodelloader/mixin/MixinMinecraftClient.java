package net.sistr.littlemaidmodelloader.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.resource.manager.LMTextureManager;
import net.sistr.littlemaidmodelloader.resource.util.ResourceHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(at = @At("RETURN"), method = "<init>")
    public void onInit(RunArgs args, CallbackInfo ci) {
        loadTexture();
    }

    private static void loadTexture() {
        //このパスにあるテクスチャすべてを受け取る(リソパ及びModリソースからも抜ける)
        Collection<Identifier> resourceLocations = MinecraftClient.getInstance().getResourceManager()
                .findResources("textures/entity/littlemaid", s -> true);
        //テクスチャを読み込む
        resourceLocations.forEach(resourcePath -> {
            String path = resourcePath.getPath();
            ResourceHelper.getParentFolderName(path, false).ifPresent(textureName -> {
                String modelName = ResourceHelper.getModelName(textureName);
                int index = ResourceHelper.getIndex(path);
                if (index != -1) {
                    LMTextureManager.INSTANCE
                            .addTexture(ResourceHelper.getFileName(path, false), textureName, modelName, index, resourcePath);
                }
            });
        });
    }

}
