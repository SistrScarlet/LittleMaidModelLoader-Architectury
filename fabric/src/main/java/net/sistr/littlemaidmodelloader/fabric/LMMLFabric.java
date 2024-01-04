package net.sistr.littlemaidmodelloader.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;
import net.sistr.littlemaidmodelloader.setup.ClientSetup;
import net.sistr.littlemaidmodelloader.setup.ModSetup;
import net.sistr.littlemaidmodelloader.setup.Registration;

public class LMMLFabric implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        LMMLMod.init();
        ModSetup.init();
    }

    @Override
    public void onInitializeClient() {
        ClientSetup.init();
        EntityRendererRegistry.INSTANCE.register(Registration.MULTI_MODEL_ENTITY.get(), (manager, context) -> new MultiModelRenderer<>(manager));
        EntityRendererRegistry.INSTANCE.register(Registration.DUMMY_MODEL_ENTITY.get(), (manager, context) -> new MultiModelRenderer<>(manager));
    }
}
