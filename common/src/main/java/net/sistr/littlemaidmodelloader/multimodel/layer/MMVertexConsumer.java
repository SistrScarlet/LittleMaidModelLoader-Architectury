package net.sistr.littlemaidmodelloader.multimodel.layer;

import net.minecraft.client.render.VertexConsumer;

public class MMVertexConsumer {
    private final VertexConsumer vertexConsumer;

    public MMVertexConsumer(VertexConsumer vertexConsumer) {
        this.vertexConsumer = vertexConsumer;
    }

    public void vertex(float x, float y, float z, MMColor color, float u, float v,
                       int overlay, int light, float normalX, float normalY, float normalZ) {
        this.vertexConsumer.vertex(x, y, z, color.argb(), u, v, overlay, light, normalX, normalY, normalZ);
    }

    public VertexConsumer getVanillaVertexConsumer() {
        return this.vertexConsumer;
    }

}
