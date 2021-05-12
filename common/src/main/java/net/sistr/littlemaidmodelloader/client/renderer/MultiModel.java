package net.sistr.littlemaidmodelloader.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMRenderContext;

@Environment(EnvType.CLIENT)
public class MultiModel<T extends LivingEntity & IHasMultiModel> extends EntityModel<T> {
    private T entity;

    public MultiModel() {
        super(RenderLayer::getEntityTranslucent);
    }

    @Override
    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
        entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .ifPresent(model -> model.animateModel(entity.getCaps(), limbAngle, limbDistance, tickDelta));
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .ifPresent(model -> model.setAngles(entity.getCaps(), limbAngle, limbDistance, animationProgress, headYaw, headPitch));
        this.entity = entity;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.entity == null) {
            return;
        }
        this.entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .ifPresent(model -> model.render(new MMRenderContext(matrices, vertices, light, overlay, red, green, blue, alpha)));
        this.entity = null;
    }

}