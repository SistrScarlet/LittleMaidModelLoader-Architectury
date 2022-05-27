package net.sistr.littlemaidmodelloader.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.sistr.littlemaidmodelloader.LMMLMod;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.maidmodel.ModelMultiBase;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMMatrixStack;

import static net.sistr.littlemaidmodelloader.maidmodel.IModelCaps.*;

//そのまま使ってもいいし継承して使ってもいい
//別な奴を継承しながら使いたいなら移譲でどうにかするか自作してね
@Environment(EnvType.CLIENT)
public class MultiModelRenderer<T extends LivingEntity & IHasMultiModel> extends LivingEntityRenderer<T, MultiModel<T>> {
    private static final Identifier NULL_TEXTURE = new Identifier(LMMLMod.MODID, "null");

    public MultiModelRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MultiModel<>(), 0.5F);
        this.addFeature(new MultiModelArmorLayer<>(this));
        this.addFeature(new MultiModelHeldItemLayer<>(this));
        this.addFeature(new MultiModelLightLayer<>(this));
    }

    //MobRendererから引っ張ってきた
    protected boolean hasLabel(T mobEntity) {
        return super.hasLabel(mobEntity) && (mobEntity.shouldRenderName() || mobEntity.hasCustomName()
                && mobEntity == this.dispatcher.targetedEntity);
    }

    @Override
    protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .ifPresent(model -> model.setupTransform(entity.getCaps(),
                        new MMMatrixStack(matrices), animationProgress, bodyYaw, tickDelta));
    }

    @Override
    protected void scale(T entity, MatrixStack matrices, float amount) {
        entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .filter(model -> model instanceof ModelMultiBase)
                .map(model -> (float) ((ModelMultiBase) model).getCapsValue(caps_ScaleFactor))
                .ifPresent(scale -> matrices.scale(scale, scale, scale));
    }

    @Override
    public void render(T livingEntity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        Profiler profiler = MinecraftClient.getInstance().getProfiler();
        profiler.push("littlemaidmodelloader:mm");
        livingEntity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .filter(model -> model instanceof ModelMultiBase)
                .ifPresent(model -> syncCaps(livingEntity, (ModelMultiBase) model, partialTicks));
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            livingEntity.getModel(IHasMultiModel.Layer.INNER, part)
                    .filter(model -> model instanceof ModelMultiBase)
                    .ifPresent(model -> syncCaps(livingEntity, (ModelMultiBase) model, partialTicks));
            livingEntity.getModel(IHasMultiModel.Layer.OUTER, part)
                    .filter(model -> model instanceof ModelMultiBase)
                    .ifPresent(model -> syncCaps(livingEntity, (ModelMultiBase) model, partialTicks));
        }
        super.render(livingEntity, entityYaw, partialTicks, matrixStack, vertexConsumerProvider, light);
        profiler.pop();
    }

    public void syncCaps(T entity, ModelMultiBase model, float partialTicks) {
        float swingProgress = entity.getHandSwingProgress(partialTicks);
        float right = 0;
        float left = 0;
        if (entity.preferredHand == Hand.MAIN_HAND) {
            if (entity.getMainArm() == Arm.RIGHT) {
                right = swingProgress;
            } else {
                left = swingProgress;
            }
        } else {
            if (entity.getMainArm() != Arm.RIGHT) {
                right = swingProgress;
            } else {
                left = swingProgress;
            }
        }
        model.setCapsValue(caps_onGround, right, left);
        model.setCapsValue(caps_isRiding, entity.hasVehicle());
        model.setCapsValue(caps_isSneak, entity.isSneaking());
        model.setCapsValue(caps_isChild, entity.isBaby());
        model.setCapsValue(caps_heldItemLeft, 0F);
        model.setCapsValue(caps_heldItemRight, 0F);
        model.setCapsValue(caps_aimedBow, false);
        model.setCapsValue(caps_entityIdFactor, 0F);
        model.setCapsValue(caps_ticksExisted, entity.age);
    }

    @Override
    public Identifier getTexture(T entity) {
        return entity.getTexture(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD, false)
                .orElse(NULL_TEXTURE);
    }
}
