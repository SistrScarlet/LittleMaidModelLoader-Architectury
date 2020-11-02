package net.sistr.lmml.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.sistr.lmml.client.AddableResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(ResourcePackManager.class)
public class MixinResourcePackManager implements AddableResourcePackProvider {

    @Shadow @Final private Set<ResourcePackProvider> providers;

    @Override
    public void addResourcePackProvider(ResourcePackProvider packProvider) {
        this.providers.add(packProvider);
    }
}
