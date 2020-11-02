package net.sistr.lmml.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.sistr.lmml.LittleMaidModelLoader;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.maidmodel.ModelMultiBase;

import static net.sistr.lmml.maidmodel.IModelCaps.*;

//そのまま使ってもいいし継承して使ってもいい
//別な奴を継承しながら使いたいなら移譲でどうにかするか自作してね
@Environment(EnvType.CLIENT)
public class MultiModelRenderer<T extends LivingEntity & IHasMultiModel> extends LivingEntityRenderer<T, MultiModel<T>> {
    private static final Identifier NULL_TEXTURE = new Identifier(LittleMaidModelLoader.MODID, "null");

    public MultiModelRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, new MultiModel<>(), 0.5F);
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
    public void render(T livingEntity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        Profiler profiler = MinecraftClient.getInstance().getProfiler();
        profiler.push("lmml:mm");
        livingEntity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .ifPresent(model -> syncCaps(livingEntity, model, partialTicks));
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            livingEntity.getModel(IHasMultiModel.Layer.INNER, part)
                    .ifPresent(model -> syncCaps(livingEntity, model, partialTicks));
            livingEntity.getModel(IHasMultiModel.Layer.OUTER, part)
                    .ifPresent(model -> syncCaps(livingEntity, model, partialTicks));
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
