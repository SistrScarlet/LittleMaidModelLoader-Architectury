package net.sistr.littlemaidmodelloader.client.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;

public abstract class GUIElement implements Drawable, Element {
    protected int x;
    protected int y;
    protected final int width;
    protected final int height;
    private boolean focused;

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

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }
}
