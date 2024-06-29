package net.sistr.littlemaidmodelloader.multimodel.layer;

public class MMRenderContext {
    private final MMMatrixStack matrices;
    private final MMVertexConsumer vertices;
    private final int light;
    private final int overlay;
    private final MMColor color;

    public MMRenderContext(net.minecraft.client.util.math.MatrixStack matrices,
                           net.minecraft.client.render.VertexConsumer vertices,
                           int light, int overlay, MMColor color) {
        this(new MMMatrixStack(matrices), new MMVertexConsumer(vertices), light, overlay, color);
    }

    public MMRenderContext(MMMatrixStack matrices, MMVertexConsumer vertices, int light, int overlay,
                           MMColor color) {
        this.matrices = matrices;
        this.vertices = vertices;
        this.light = light;
        this.overlay = overlay;
        this.color = color;
    }

    public MMMatrixStack getMatrices() {
        return matrices;
    }

    public MMVertexConsumer getVertices() {
        return vertices;
    }

    public int getLight() {
        return light;
    }

    public int getOverlay() {
        return overlay;
    }

    public MMColor getColor() {
        return color;
    }

    public void render(Renderer renderer) {
        renderer.render(matrices, vertices, light, overlay, color);
    }

    public interface Renderer {
        void render(MMMatrixStack matrices, MMVertexConsumer vertices, int light, int overlay, MMColor color);
    }

}
