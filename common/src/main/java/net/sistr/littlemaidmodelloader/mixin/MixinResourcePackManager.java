package net.sistr.littlemaidmodelloader.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.sistr.littlemaidmodelloader.client.resource.LMPackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(ResourcePackManager.class)
public class MixinResourcePackManager {

    @Shadow
    @Final
    @Mutable
    private Set<ResourcePackProvider> providers;

    @Inject(method = "<init>(Lnet/minecraft/resource/ResourcePackProfile$Factory;[Lnet/minecraft/resource/ResourcePackProvider;)V",
            at = @At("RETURN"))
    public void addResourcePackProvider(ResourcePackProfile.Factory profileFactory, ResourcePackProvider[] providers, CallbackInfo ci) {
        this.providers = new HashSet<>(this.providers);
        //外部リソース読み込み用偽装リソパ追加
        this.providers.add(new LMPackProvider());
    }
}
