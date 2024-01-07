package net.sistr.littlemaidmodelloader.client.screen;

public class TextureAddress {
    private final int u;
    private final int v;
    private final int width;
    private final int height;
    private final int texSizeW;
    private final int texSizeH;

    public TextureAddress(int u, int v, int width, int height, int texSizeW, int texSizeH) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.texSizeW = texSizeW;
        this.texSizeH = texSizeH;
    }

    public int u() {
        return u;
    }

    public int v() {
        return v;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int texSizeW() {
        return texSizeW;
    }

    public int texSizeH() {
        return texSizeH;
    }
}
