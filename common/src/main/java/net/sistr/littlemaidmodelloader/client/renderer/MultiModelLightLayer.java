package net.sistr.littlemaidmodelloader.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.profiler.Profiler;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.maidmodel.IModelCaps;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMRenderContext;

//スキンの発光レイヤー、防具の発光レイヤーは防具でやってる
@Environment(EnvType.CLIENT)
public class MultiModelLightLayer<T extends LivingEntity & IHasMultiModel> extends FeatureRenderer<T, MultiModel<T>> {

    public MultiModelLightLayer(FeatureRendererContext<T, MultiModel<T>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                       float headYaw, float headPitch) {
        Profiler profiler = MinecraftClient.getInstance().getProfiler();
        profiler.push("littlemaidmodelloader:mm_eye_layer");
        renderLightLayer(matrices, vertexConsumers, entity, limbAngle, limbDistance, tickDelta, animationProgress,
                headYaw, headPitch, entity.getCaps());
        profiler.pop();
    }

    //クモの目と同じRenderTypeを使いたいがなんか真っ白になるのでやめた
    private void renderLightLayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity,
                                  float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                  float headYaw, float headPitch, IModelCaps caps) {
        entity.getTexture(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD, true).ifPresent(resourceLocation ->
                entity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD).ifPresent(model -> {
                    VertexConsumer builder = vertexConsumers.getBuffer(MultiModelRenderLayer.getDefault(resourceLocation));
                    model.animateModel(caps, limbAngle, limbDistance, tickDelta);
                    model.setAngles(caps, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                    model.render(new MMRenderContext(matrices, builder, 0xF00000, OverlayTexture.DEFAULT_UV,
                            1F, 1F, 1F, 1F));
                }));
    }

}
