package net.sistr.littlemaidmodelloader.setup;

import dev.architectury.event.events.client.ClientReloadShadersEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.sistr.littlemaidmodelloader.client.renderer.MultiModelRenderLayer;

@Environment(EnvType.CLIENT)
public class ClientSetup {

  public static void init() {
    ClientReloadShadersEvent.EVENT.register(
        (provider, sink) -> {
          try {
            sink.registerShader(
                new ShaderProgram(
                    provider,
                    "lmml_emissive",
                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
                MultiModelRenderLayer::setEmissiveShader);
          } catch (Exception e) {
            throw new RuntimeException("Failed to load LMML emissive shader", e);
          }
        });
  }
}
