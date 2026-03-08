package net.sistr.littlemaidmodelloader.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.sistr.littlemaidmodelloader.LMMLMod;
import org.jetbrains.annotations.Nullable;

public class MultiModelRenderLayer extends RenderLayer {

  // RenderPhase の protected 定数にアクセスするために RenderLayer を継承
  private MultiModelRenderLayer() {
    super(null, null, null, 0, false, false, null, null);
  }

  @Nullable private static net.minecraft.client.gl.ShaderProgram emissiveShader;

  public static void setEmissiveShader(net.minecraft.client.gl.ShaderProgram shader) {
    emissiveShader = shader;
  }

  public static RenderLayer getDefault(Identifier identifier) {
    if (LMMLMod.getConfig().isEnableAlpha()) {
      return RenderLayer.getEntityTranslucent(identifier);
    }
    return RenderLayer.getEntityCutoutNoCull(identifier);
  }

  public static RenderLayer getEmissive(Identifier identifier) {
    return RenderLayer.of(
        "lmml_emissive",
        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
        VertexFormat.DrawMode.QUADS,
        256,
        false,
        true,
        RenderLayer.MultiPhaseParameters.builder()
            .program(new RenderPhase.ShaderProgram(() -> emissiveShader))
            .texture(new RenderPhase.Texture(identifier, false, false))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .lightmap(ENABLE_LIGHTMAP)
            .writeMaskState(COLOR_MASK)
            .cull(DISABLE_CULLING)
            .build(false));
  }
}
