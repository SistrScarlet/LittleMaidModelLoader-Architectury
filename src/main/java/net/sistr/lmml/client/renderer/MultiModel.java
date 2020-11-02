package net.sistr.lmml.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.maidmodel.ModelRenderer;

@Environment(EnvType.CLIENT)
public class MultiModel<T extends LivingEntity & IHasMultiModel> extends EntityModel<T> {
    private T entity;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float netHeadYaw;
    private float headPitch;

    public MultiModel() {
        super(RenderLayer::getEntityTranslucent);
    }

    @Override
    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
        entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD).ifPresent(model ->
                model.setLivingAnimations(entity.getCaps(), limbAngle, limbDistance, tickDelta));
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.entity = entity;
        this.limbSwing = limbAngle;
        this.limbSwingAmount = limbDistance;
        this.ageInTicks = animationProgress;
        this.netHeadYaw = headYaw;
        this.headPitch = headPitch;
        entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD).ifPresent(model ->
                model.setRotationAngles(limbAngle, limbDistance,
                        animationProgress, headYaw, headPitch, 0.0625F, entity.getCaps()));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.entity == null) {
            return;
        }
        this.entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD).ifPresent(model -> {
            ModelRenderer.setParam(matrices, vertices, light, overlay, red, green, blue, alpha);
            model.render(entity.getCaps(), limbSwing, limbSwingAmount,
                    ageInTicks, netHeadYaw, headPitch, 0.0625F, true);
        });
        this.entity = null;
    }

}