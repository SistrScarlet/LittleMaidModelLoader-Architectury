package net.sistr.littlemaidmodelloader.client.screen.component;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

/** 要素数を動的に変更可能なScrollBar */
public class MutableScrollBar extends GUIElement {
  private int elemSize;
  private final TextureAddress sliderT;
  private final TextureAddress sliderM;
  private final TextureAddress sliderB;
  private final TextureAddress pointer;
  private final Identifier texture;
  private boolean clicked;
  private int point;

  public MutableScrollBar(
      int x,
      int y,
      int width,
      int height,
      int elemSize,
      TextureAddress sliderT,
      TextureAddress sliderM,
      TextureAddress sliderB,
      TextureAddress pointer,
      Identifier texture) {
    super(width, height);
    this.x = x;
    this.y = y;
    this.elemSize = elemSize;
    this.sliderT = sliderT;
    this.sliderM = sliderM;
    this.sliderB = sliderB;
    this.pointer = pointer;
    this.texture = texture;
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    draw(context, this.x, this.y, sliderT);
    int midRange = height - (sliderT.height() + sliderB.height());
    int i = 0;
    while (i < midRange) {
      i += sliderM.height();
      draw(context, this.x, this.y + i, sliderM);
    }
    draw(context, this.x, this.y + height - sliderB.height(), sliderB);
    draw(
        context,
        this.x + (sliderT.width() - pointer.width()) / 2,
        (int) ((this.y + (width - pointer.height()) / 2f) + (getPercent() * (height - width))),
        pointer);
  }

  private void draw(DrawContext context, int x, int y, TextureAddress texture) {
    context.drawTexture(
        this.texture,
        x,
        y,
        texture.u(),
        texture.v(),
        texture.width(),
        texture.height(),
        texture.texSizeW(),
        texture.texSizeH());
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
      return false;
    }
    if (!RangeChecker.checkFromWidth(mouseX, mouseY, this.x, this.y, this.width, this.height)) {
      return false;
    }
    clicked = true;
    pointAt(mouseY);
    return true;
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
      return false;
    }
    if (!clicked
        && !RangeChecker.checkFromWidth(mouseX, mouseY, this.x, this.y, this.width, this.height)) {
      return false;
    }
    clicked = true;
    pointAt(mouseY);
    return true;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
      clicked = false;
      return false;
    }
    return false;
  }

  public void pointAt(double y) {
    if (elemSize <= 0) {
      point = 0;
      return;
    }

    float percent = ((float) y - this.y - width / 2f) / (height - width);
    this.point = MathHelper.floor(percent * elemSize);
    this.point = MathHelper.clamp(this.point, 0, elemSize - 1);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (!RangeChecker.checkFromWidth(mouseX, mouseY, this.x, this.y, this.width, this.height)) {
      return false;
    }
    this.point += 0 < amount ? -1 : 1;
    this.point = MathHelper.clamp(this.point, 0, Math.max(0, elemSize - 1));
    return true;
  }

  public float getPercent() {
    if (elemSize <= 0) return 0.0f;
    return ((float) getPoint() / Math.max(1, elemSize - 1));
  }

  public void setPercent(float percent) {
    if (elemSize <= 0) {
      this.point = 0;
      return;
    }
    this.point = MathHelper.floor(percent * (elemSize - 1));
    this.point = MathHelper.clamp(this.point, 0, elemSize - 1);
  }

  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
    this.point = MathHelper.clamp(this.point, 0, Math.max(0, elemSize - 1));
  }

  public int getElemSize() {
    return elemSize;
  }

  /** 要素数を動的に設定する（MutableScrollBar の主要機能） */
  public void setElemSize(int newElemSize) {
    if (newElemSize < 0) {
      newElemSize = 0;
    }

    this.elemSize = newElemSize;

    // ポイント位置の再調整
    if (elemSize <= 0) {
      this.point = 0;
    } else {
      this.point = MathHelper.clamp(this.point, 0, elemSize - 1);
    }
  }
}
