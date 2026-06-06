package net.sistr.littlemaidmodelloader.multimodel.layer;

import net.minecraft.client.render.VertexConsumer;

public class MMVertexConsumer {
    private final VertexConsumer vertexConsumer;

    public MMVertexConsumer(VertexConsumer vertexConsumer) {
        this.vertexConsumer = vertexConsumer;
    }

    public void vertex(
            float x,
            float y,
            float z,
            float red,
            float green,
            float blue,
            float alpha,
            float u,
            float v,
            int overlay,
            int light,
            float normalX,
            float normalY,
            float normalZ) {
        int color =
                ((int) (alpha * 255) & 0xFF) << 24
                        | ((int) (red * 255) & 0xFF) << 16
                        | ((int) (green * 255) & 0xFF) << 8
                        | ((int) (blue * 255) & 0xFF);
        this.vertexConsumer.vertex(x, y, z, color, u, v, overlay, light, normalX, normalY, normalZ);
    }

    public VertexConsumer getVanillaVertexConsumer() {
        return this.vertexConsumer;
    }
}
