package net.sistr.littlemaidmodelloader.setup;

import dev.architectury.registry.level.entity.EntityRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;

@Environment(EnvType.CLIENT)
public class ClientSetup {

    public static void init() {
        EntityRendererRegistry.register(() -> Registration.MULTI_MODEL_ENTITY_BEFORE, MultiModelRenderer::new);
        EntityRendererRegistry.register(() -> Registration.DUMMY_MODEL_ENTITY_BEFORE, MultiModelRenderer::new);
    }

}
