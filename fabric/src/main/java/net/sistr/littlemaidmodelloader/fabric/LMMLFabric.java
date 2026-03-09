package net.sistr.littlemaidmodelloader.fabric;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
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
        EntityRendererRegistry.register(Registration.MULTI_MODEL_ENTITY, MultiModelRenderer::new);
        EntityRendererRegistry.register(Registration.DUMMY_MODEL_ENTITY, MultiModelRenderer::new);
    }
}
