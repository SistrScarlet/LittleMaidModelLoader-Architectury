package net.sistr.littlemaidmodelloader.multimodel.layer;

public class MMRenderContext {
    private final MMMatrixStack matrices;
    private final MMVertexConsumer vertices;
    private final int light;
    private final int overlay;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public MMRenderContext(net.minecraft.client.util.math.MatrixStack matrices,
                           net.minecraft.client.render.VertexConsumer vertices,
                           int light, int overlay, float red, float green, float blue, float alpha) {
        this(new MMMatrixStack(matrices), new MMVertexConsumer(vertices), light, overlay, red, green, blue, alpha);
    }

    public MMRenderContext(MMMatrixStack matrices, MMVertexConsumer vertices, int light, int overlay,
                           float red, float green, float blue, float alpha) {
        this.matrices = matrices;
        this.vertices = vertices;
        this.light = light;
        this.overlay = overlay;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
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

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public void render(Renderer renderer) {
        renderer.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public interface Renderer {
        void render(MMMatrixStack matrices, MMVertexConsumer vertices, int light, int overlay,
                    float red, float green, float blue, float alpha);
    }

}
