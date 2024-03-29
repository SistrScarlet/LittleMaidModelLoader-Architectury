package net.sistr.littlemaidmodelloader.mixin.fabric;

import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
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

//Fabricにリソパ追加するやつはあるが、主目的が代替リソパの追加なのでLMMLには適さない。てかめんどくさい
@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @Shadow
    @Final
    @Mutable
    private Set<ResourcePackProvider> providers;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void construct(ResourcePackProvider[] providers, CallbackInfo ci) {
        boolean client = false;
        this.providers = new HashSet<>(this.providers);
        for (ResourcePackProvider provider : this.providers) {
            if (provider instanceof DefaultClientResourcePackProvider) {
                client = true;
                break;
            }
        }
        if (client) {
            this.providers.add(new LMPackProvider());
        }
    }

}
