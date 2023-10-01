package net.sistr.littlemaidmodelloader.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3f;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMMatrixStack;

@Environment(EnvType.CLIENT)
public class MultiModelHeldItemLayer<T extends LivingEntity & IHasMultiModel, M extends MultiModel<T>> extends FeatureRenderer<T, M> {

    public MultiModelHeldItemLayer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                       float headYaw, float headPitch) {
        boolean isMainRight = entity.getMainArm() == Arm.RIGHT;
        ItemStack rightStack = isMainRight ? entity.getMainHandStack() : entity.getOffHandStack();
        ItemStack leftStack = isMainRight ? entity.getOffHandStack() : entity.getMainHandStack();
        if (!leftStack.isEmpty() || !rightStack.isEmpty()) {
            matrices.push();
            if (this.getContextModel().child) {
                matrices.translate(0.0D, 0.75D, 0.0D);
                matrices.scale(0.5F, 0.5F, 0.5F);
            }

            this.handRender(entity, rightStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrices, vertexConsumers, light);
            this.handRender(entity, leftStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrices, vertexConsumers, light);
            matrices.pop();
        }
    }

    //todo 位置調整
    private void handRender(T entity, ItemStack stack, ModelTransformation.Mode mode, Arm hand, MatrixStack matrixStack, VertexConsumerProvider buffer, int light) {
        if (!stack.isEmpty()) {
            matrixStack.push();
            boolean isLeft = hand == Arm.LEFT;
            entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.BODY)
                    .ifPresent(model -> model.adjustHandItem(new MMMatrixStack(matrixStack), isLeft));

            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            /* 初期モデル構成で
             * x: 手の甲に垂直な方向(-で向かって右に移動)
             * y: 体の面に垂直な方向(-で向かって背面方向に移動)
             * z: 腕に平行な方向(-で向かって手の先方向に移動)
             */
            matrixStack.translate(isLeft ? -0.0125F : 0.0125F, 0.05f, -0.15f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(entity, stack, mode, isLeft,
                    matrixStack, buffer, entity.world, light, OverlayTexture.DEFAULT_UV, entity.getId() + mode.ordinal());
            matrixStack.pop();
        }
    }

}
