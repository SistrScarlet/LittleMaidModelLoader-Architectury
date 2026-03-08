package net.sistr.littlemaidmodelloader.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.profiler.Profiler;
import net.sistr.littlemaidmodelloader.entity.compound.IHasMultiModel;
import net.sistr.littlemaidmodelloader.maidmodel.IModelCaps;
import net.sistr.littlemaidmodelloader.maidmodel.ModelCapsHelper;
import net.sistr.littlemaidmodelloader.multimodel.layer.MMRenderContext;

// スキンの発光レイヤー、防具の発光レイヤーは防具でやってる
@Environment(EnvType.CLIENT)
public class MultiModelLightLayer<T extends LivingEntity & IHasMultiModel, M extends MultiModel<T>>
    extends FeatureRenderer<T, M> {

  public MultiModelLightLayer(FeatureRendererContext<T, M> context) {
    super(context);
  }

  @Override
  public void render(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      T entity,
      float limbAngle,
      float limbDistance,
      float tickDelta,
      float animationProgress,
      float headYaw,
      float headPitch) {
    Profiler profiler = MinecraftClient.getInstance().getProfiler();
    profiler.push("littlemaidmodelloader:mm_eye_layer");
    renderLightLayer(
        matrices,
        vertexConsumers,
        entity,
        limbAngle,
        limbDistance,
        tickDelta,
        animationProgress,
        headYaw,
        headPitch,
        entity.getCaps());
    profiler.pop();
  }

  private void renderLightLayer(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      T entity,
      float limbAngle,
      float limbDistance,
      float tickDelta,
      float animationProgress,
      float headYaw,
      float headPitch,
      IModelCaps caps) {
    entity
        .getTexture(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD, true)
        .ifPresent(
            resourceLocation ->
                entity
                    .getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                    .ifPresent(
                        model -> {
                          VertexConsumer builder =
                              vertexConsumers.getBuffer(
                                  MultiModelRenderLayer.getEmissive(resourceLocation));
                          model.animateModel(caps, limbAngle, limbDistance, tickDelta);
                          model.setAngles(
                              caps, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                          float r = 1F, g = 1F, b = 1F, a = 1F;
                          if (model instanceof IModelCaps modelCaps) {
                            float[] lightColor =
                                (float[])
                                    ModelCapsHelper.getCapsValue(
                                        modelCaps, IModelCaps.caps_textureLightColor, caps);
                            if (lightColor != null && lightColor.length >= 4) {
                              r = lightColor[0];
                              g = lightColor[1];
                              b = lightColor[2];
                              a = lightColor[3];
                            }
                          }
                          model.render(
                              new MMRenderContext(
                                  matrices,
                                  builder,
                                  15728880,
                                  OverlayTexture.DEFAULT_UV,
                                  r,
                                  g,
                                  b,
                                  a));
                        }));
  }
}
