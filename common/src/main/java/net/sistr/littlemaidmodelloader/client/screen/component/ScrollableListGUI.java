package net.sistr.littlemaidmodelloader.client.screen.component;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.sistr.littlemaidmodelloader.client.screen.ModelSelectScreen;
import org.jetbrains.annotations.Nullable;

public class ScrollableListGUI<T extends GUIElement> extends MutableListGUI<T> {
  @Nullable private final MutableScrollBar scrollBar;

  public ScrollableListGUI(
      int x,
      int y,
      int widthStack,
      int heightStack,
      int elementW,
      int elementH,
      Collection<T> elements,
      boolean enableScrollBar) {
    super(x, y, widthStack, heightStack, elementW, elementH, elements);

    if (enableScrollBar) {
      // デフォルトのスクロールバー設定
      this.scrollBar = createDefaultScrollBar();
    } else {
      this.scrollBar = null;
    }
  }

  public ScrollableListGUI(
      int x,
      int y,
      int widthStack,
      int heightStack,
      int elementW,
      int elementH,
      Collection<T> elements,
      ScrollBarConfig scrollBarConfig) {
    super(x, y, widthStack, heightStack, elementW, elementH, elements);
    this.scrollBar = createScrollBar(scrollBarConfig);
  }

  private MutableScrollBar createDefaultScrollBar() {
    // デフォルト設定のスクロールバー（リスト右端から+10ピクセル右に配置）
    return new MutableScrollBar(
        this.x + this.width + 10,
        this.y,
        8,
        this.height,
        calculateScrollBarSize(),
        new TextureAddress(0, 200, 8, 8, 256, 256),
        new TextureAddress(0, 208, 8, 8, 256, 256),
        new TextureAddress(0, 216, 8, 8, 256, 256),
        new TextureAddress(0, 224, 10, 6, 256, 256),
        ModelSelectScreen.MODEL_SELECT_GUI_TEXTURE);
  }

  private MutableScrollBar createScrollBar(ScrollBarConfig config) {
    return new MutableScrollBar(
        this.x + this.width + config.offsetX(),
        this.y + config.offsetY(),
        config.width(),
        config.height(),
        calculateScrollBarSize(),
        config.sliderT(),
        config.sliderM(),
        config.sliderB(),
        config.pointer(),
        config.texture());
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    // リスト部分の描画
    super.render(context, mouseX, mouseY, delta);

    // スクロールバーの描画
    if (scrollBar != null) {
      scrollBar.render(context, mouseX, mouseY, delta);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    // スクロールバーが優先
    if (scrollBar != null) {
      if (scrollBar.mouseClicked(mouseX, mouseY, button)) {
        syncScrollFromScrollBar();
        return true;
      }
    }

    // リスト部分の処理
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    boolean result = false;

    // スクロールバーの処理
    if (scrollBar != null) {
      if (scrollBar.mouseReleased(mouseX, mouseY, button)) {
        syncScrollFromScrollBar();
        result = true;
      }
    }

    // リスト部分の処理
    boolean listResult = super.mouseReleased(mouseX, mouseY, button);

    return result || listResult;
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    // スクロールバーが優先
    if (scrollBar != null) {
      if (scrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
        syncScrollFromScrollBar();
        return true;
      }
    }

    // リスト部分の処理
    return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    // スクロールバーが優先
    if (scrollBar != null) {
      if (scrollBar.mouseScrolled(mouseX, mouseY, amount)) {
        syncScrollFromScrollBar();
        return true;
      }
    }

    // リスト部分の処理
    boolean result = super.mouseScrolled(mouseX, mouseY, amount);
    if (result) {
      syncScrollToScrollBar();
    }
    return result;
  }

  @Override
  public void setScroll(int scroll) {
    super.setScroll(scroll);
    syncScrollToScrollBar();
  }

  @Override
  public void setElements(Collection<T> newElements) {
    super.setElements(newElements);
    updateScrollBarSize();
  }

  @Override
  public void addElement(T element) {
    super.addElement(element);
    updateScrollBarSize();
  }

  @Override
  public void removeElement(T element) {
    super.removeElement(element);
    updateScrollBarSize();
  }

  @Override
  public void removeElementAt(int index) {
    super.removeElementAt(index);
    updateScrollBarSize();
  }

  @Override
  public void clearElements() {
    super.clearElements();
    updateScrollBarSize();
  }

  /** スクロールバーからリストへスクロール位置を同期 */
  private void syncScrollFromScrollBar() {
    if (scrollBar != null) {
      int newScroll = scrollBar.getPoint();
      if (newScroll != this.scroll) {
        int totalRows = (size() + widthStack - 1) / widthStack;
        int maxScroll = Math.max(0, totalRows - heightStack);
        this.scroll = MathHelper.clamp(newScroll, 0, maxScroll);
      }
    }
  }

  /** リストからスクロールバーへスクロール位置を同期 */
  private void syncScrollToScrollBar() {
    if (scrollBar != null) {
      if (scrollBar.getPoint() != this.scroll) {
        scrollBar.setPoint(this.scroll);
      }
    }
  }

  /** スクロールバーの要素数を更新 */
  private void updateScrollBarSize() {
    if (scrollBar != null) {
      scrollBar.setElemSize(calculateScrollBarSize());
      syncScrollToScrollBar();
    }
  }

  /** スクロールバーの有無を取得 */
  public boolean hasScrollBar() {
    return scrollBar != null;
  }

  /** スクロールバーを取得（デバッグ/テスト用） */
  public Optional<MutableScrollBar> getScrollBar() {
    return Optional.ofNullable(scrollBar);
  }

  /** スクロールバーのサイズを計算 */
  private int calculateScrollBarSize() {
    if (size() == 0) {
      return 1;
    }
    int totalRows = (size() + widthStack - 1) / widthStack;
    int maxScrollableRows = Math.max(0, totalRows - heightStack);
    return Math.max(1, maxScrollableRows + 1);
  }

  /** スクロールバー設定用のレコード */
  public record ScrollBarConfig(
      int offsetX,
      int offsetY,
      int width,
      int height,
      TextureAddress sliderT,
      TextureAddress sliderM,
      TextureAddress sliderB,
      TextureAddress pointer,
      Identifier texture) {
    public static ScrollBarConfig defaultConfig() {
      return new ScrollBarConfig(
          4,
          0,
          8,
          200,
          new TextureAddress(0, 200, 8, 8, 256, 256),
          new TextureAddress(0, 208, 8, 8, 256, 256),
          new TextureAddress(0, 216, 8, 8, 256, 256),
          new TextureAddress(0, 224, 10, 6, 256, 256),
          ModelSelectScreen.MODEL_SELECT_GUI_TEXTURE);
    }
  }
}
