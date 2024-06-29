package net.sistr.littlemaidmodelloader.multimodel.layer;

public record MMColor(int red, int green, int blue, int alpha) {

    public static MMColor from4I(int red, int green, int blue, int alpha) {
        return new MMColor(red, green, blue, alpha);
    }

    public static MMColor from4F(float red, float green, float blue, float alpha) {
        return new MMColor((int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
    }

    public static MMColor fromRGBA(int rgba) {
        return new MMColor(rgba >>> 24, (rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF);
    }

    public static MMColor fromARGB(int argb) {
        return new MMColor((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, argb >>> 24);
    }

    public int rgba() {
        return (red << 24) | (green << 16) | (blue << 8) | alpha;
    }

    public int argb() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public int redI() {
        return red;
    }

    public int greenI() {
        return green;
    }

    public int blueI() {
        return blue;
    }

    public int alphaI() {
        return alpha;
    }

    public float redF() {
        return (red / 255f);
    }

    public float greenF() {
        return (green / 255f);
    }

    public float blueF() {
        return (blue / 255f);
    }

    public float alphaF() {
        return (alpha / 255f);
    }

}
