package net.sistr.littlemaidmodelloader.fabric.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;
import net.sistr.littlemaidmodelloader.setup.ClientSetup;
import net.sistr.littlemaidmodelloader.setup.Registration;

public final class LMMLFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSetup.init();
        EntityRendererRegistry.register(Registration.MULTI_MODEL_ENTITY, MultiModelRenderer::new);
        EntityRendererRegistry.register(Registration.DUMMY_MODEL_ENTITY, MultiModelRenderer::new);
    }
}
