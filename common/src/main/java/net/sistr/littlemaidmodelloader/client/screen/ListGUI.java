package net.sistr.littlemaidmodelloader.client.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ListGUI<T extends GUIElement> extends GUIElement {
    protected final MarginedClickable selectBox = new MarginedClickable(4);
    protected final int widthStack;
    protected final int heightStack;
    protected final int elementW;
    protected final int elementH;
    protected final ImmutableList<T> elements;
    protected int scroll = 0;
    protected int selectElem = -1;

    public ListGUI(int x, int y, int widthStack, int heightStack, int elementW, int elementH, Collection<T> elements) {
        super(widthStack * elementW, heightStack * elementH);
        this.x = x;
        this.y = y;
        this.widthStack = widthStack;
        this.heightStack = heightStack;
        this.elementW = elementW;
        this.elementH = elementH;
        this.elements = ImmutableList.copyOf(elements);
    }

    public int size() {
        return elements.size();
    }

    public void setScroll(int scroll) {
        this.scroll = scroll;
        this.scroll = MathHelper.clamp(this.scroll, 0, size() / widthStack - 1);
    }

    public int getScroll() {
        return this.scroll;
    }

    public List<T> getAllElements() {
        return Lists.newArrayList(elements);
    }

    public Optional<T> getSelectElement() {
        if (checkElementsBounds(selectElem)) {
            return Optional.of(elements.get(selectElem));
        }
        return Optional.empty();
    }

    protected Optional<T> getElement(double mouseX, double mouseY) {
        int index = getIndex(mouseX, mouseY);
        if (checkElementsBounds(index)) {
            return Optional.of(this.elements.get(index));
        }
        return Optional.empty();
    }

    protected double getElementX(double mouseX) {
        return (mouseX - this.x) % this.elementW;
    }

    protected double getElementY(double mouseY) {
        return (mouseY - this.y) % this.elementH;
    }

    protected int getIndex(double mouseX, double mouseY) {
        if (this.x <= mouseX && mouseX < this.x + this.elementW * this.widthStack
                && this.y <= mouseY && mouseY < this.y + this.elementH * this.heightStack) {
            int xIndex = MathHelper.floor((float) (mouseX - this.x) / (float) this.elementW);
            int yIndex = MathHelper.floor((float) (mouseY - this.y) / (float) this.elementH);
            int index = scroll * this.widthStack + yIndex * this.widthStack + xIndex;
            if (checkElementsBounds(index)) {
                return index;
            }
        }
        return -1;
    }

    protected boolean checkElementsBounds(int index) {
        return 0 <= index && index < this.elements.size();
    }

    protected boolean isRenderingElement(int index) {
        return scroll * widthStack <= index && index < scroll * widthStack + widthStack * heightStack;
    }

    protected int getElementXIndex(int index) {
        return index % widthStack;
    }

    protected int getElementYIndex(int index) {
        return index / widthStack - scroll;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (int i = 0; i < widthStack * heightStack; i++) {
            int xIndex = i % widthStack;
            int yIndex = i / widthStack;
            int x = this.x + elementW * xIndex;
            int y = this.y + elementH * yIndex;
            matrices.push();
            matrices.translate(x, y, 0);
            int index = scroll * this.widthStack + yIndex * this.widthStack + xIndex;
            if (checkElementsBounds(index)) {
                T elem = elements.get(index);
                elem.setPos(x, y);
                elem.render(matrices,
                        mouseX - x,
                        mouseY - y, delta
                );
            }
            matrices.pop();
        }
        /*if (checkElementsBounds(selectElem) && isRenderingElement(selectElem)) {
            fill(matrices,
                    this.x + getElementXIndex(selectElem) * elementW,
                    this.y + getElementYIndex(selectElem) * elementH,
                    this.x + getElementXIndex(selectElem) * elementW + elementW,
                    this.y + getElementYIndex(selectElem) * elementH + elementH,
                    0x80FFFFFF
            );
        }
        fill(matrices,
                this.x,
                this.y,
                this.x + elementW * widthStack,
                this.y + elementH * heightStack,
                0x80FFFFFF
        );*/
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        Optional<T> e = getElement(mouseX, mouseY);
        if (e.isPresent()) {
            T element = e.get();
            element.mouseMoved(getElementX(mouseX), getElementY(mouseY));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            selectBox.click(mouseX, mouseY);
        }
        Optional<T> e = getElement(mouseX, mouseY);
        if (e.isPresent()) {
            T element = e.get();
            return element.mouseClicked(getElementX(mouseX), getElementY(mouseY), button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Optional<T> e = getElement(mouseX, mouseY);
        if (e.isPresent()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (selectBox.release(mouseX, mouseY)) {
                    int index = getIndex(mouseX, mouseY);
                    if (checkElementsBounds(index)) {
                        if (this.selectElem != index && checkElementsBounds(this.selectElem)) {
                            GUIElement prev = this.elements.get(this.selectElem);
                            if (prev instanceof ListGUIElement) {
                                ((ListGUIElement) prev).setSelected(false);
                            }
                        }
                        this.selectElem = index;
                        GUIElement now = this.elements.get(this.selectElem);
                        if (now instanceof ListGUIElement) {
                            ((ListGUIElement) now).setSelected(true);
                        }
                    }
                }
            }

            T element = e.get();
            return element.mouseReleased(getElementX(mouseX), getElementY(mouseY), button);
        }
        return false;
    }

    //todo 画面の入れ替えか、スクロールとか
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Optional<T> e = getElement(mouseX, mouseY);
        if (e.isPresent()) {
            T element = e.get();
            return element.mouseDragged(getElementX(mouseX), getElementY(mouseY), button, deltaX, deltaY);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Optional<T> e = getElement(mouseX, mouseY);
        if (e.isPresent()) {
            T element = e.get();
            if (element.mouseScrolled(getElementX(mouseX), getElementY(mouseY), amount)) {
                return true;
            }
        }
        scroll = scroll + (0 < amount ? -1 : 1);
        this.scroll = MathHelper.clamp(this.scroll, 0, size() / widthStack - 1);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return super.changeFocus(lookForwards);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        Optional<T> e = getElement(mouseX, mouseY);
        if (e.isPresent()) {
            T element = e.get();
            return element.isMouseOver(getElementX(mouseX), getElementY(mouseY));
        }
        return false;
    }
}
