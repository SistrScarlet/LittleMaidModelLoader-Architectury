package net.sistr.littlemaidmodelloader.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackProvider;

@Environment(EnvType.CLIENT)
public interface AddableResourcePackProvider {

    void addResourcePackProvider(ResourcePackProvider packProvider);

}
