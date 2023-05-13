package net.sistr.littlemaidmodelloader.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;

public class MultiModelRenderLayer {

    public static RenderLayer getDefault(Identifier identifier) {
        if (LMMLMod.getConfig().isEnableAlpha()) {
            return RenderLayer.getEntityTranslucent(identifier);
        }
        return RenderLayer.getEntityCutoutNoCull(identifier);
    }

}
