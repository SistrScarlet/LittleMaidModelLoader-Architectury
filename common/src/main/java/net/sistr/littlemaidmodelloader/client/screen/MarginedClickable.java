package net.sistr.littlemaidmodelloader.client.screen;

public class MarginedClickable {
    private final int marginSq;
    private float clickAtX;
    private float clickAtY;

    public MarginedClickable(int margin) {
        this.marginSq = margin * margin;
    }

    public void click(double mouseX, double mouseY) {
        this.clickAtX = (float) mouseX;
        this.clickAtY = (float) mouseY;
    }

    public boolean release(double mouseX, double mouseY) {
        return (this.clickAtX - mouseX) * (this.clickAtX - mouseX)
                + (this.clickAtY - mouseY) * (this.clickAtY - mouseY) < marginSq;
    }

}
