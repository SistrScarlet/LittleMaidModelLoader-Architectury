package net.sistr.littlemaidmodelloader.setup;

import me.shedaniel.architectury.registry.entity.EntityRenderers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderer;

@Environment(EnvType.CLIENT)
public class ClientSetup {

    public static void init() {
        EntityRenderers.register(Registration.MULTI_MODEL_ENTITY_BEFORE, MultiModelRenderer::new);
        EntityRenderers.register(Registration.DUMMY_MODEL_ENTITY_BEFORE, MultiModelRenderer::new);
    }

}
