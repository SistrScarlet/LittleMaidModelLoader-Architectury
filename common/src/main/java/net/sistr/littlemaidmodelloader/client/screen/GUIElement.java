package net.sistr.littlemaidmodelloader.client.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;

public abstract class GUIElement extends DrawableHelper implements Drawable, Element {
    protected int x;
    protected int y;
    protected final int width;
    protected final int height;

    protected GUIElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

}
