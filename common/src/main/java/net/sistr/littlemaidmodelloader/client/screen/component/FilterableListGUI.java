package net.sistr.littlemaidmodelloader.client.screen.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.gui.DrawContext;

/** フィルタリング機能付きリストGUI テキスト入力・フィルタリング・リスト・スクロールバーを統合したコンポーネント */
public class FilterableListGUI<T extends GUIElement> extends GUIElement {

  private final TextInputGUI searchInput;
  private final ScrollableListGUI<T> listGUI;
  private final FilterPredicate<T> filterPredicate;

  // データ管理
  private final List<T> allItems;
  private final List<T> filteredItems;

  // レイアウト設定
  private final int searchInputHeight;
  private final int elementW;
  private final int elementH;

  public FilterableListGUI(
      int x,
      int y,
      int width,
      int height,
      int elementW,
      int elementH,
      Collection<T> items,
      FilterPredicate<T> filterPredicate,
      ScrollableListGUI.ScrollBarConfig scrollBarConfig,
      int searchInputHeight) {
    super(width, height);
    this.x = x;
    this.y = y;
    this.filterPredicate = filterPredicate;
    this.allItems = new ArrayList<>(items);
    this.filteredItems = new ArrayList<>(items);
    this.searchInputHeight = searchInputHeight;
    this.elementW = elementW;
    this.elementH = elementH;

    // グリッドを自動計算
    int widthStack = Math.max(1, width / elementW);
    int listHeight = height - searchInputHeight;
    int heightStack = Math.max(1, listHeight / elementH);

    // レイアウト計算（検索フィールドは常に最下部）
    int listX = x;
    int listY = y;
    int searchX = x;
    int searchY = y + listHeight;

    // テキスト入力欄の作成
    this.searchInput = new TextInputGUI(searchX, searchY, width, searchInputHeight);
    this.searchInput.addTextChangeListener(this::onFilterTextChanged);

    // リストGUIの作成
    if (scrollBarConfig != null) {
      this.listGUI =
          new ScrollableListGUI<>(
              listX,
              listY,
              widthStack,
              heightStack,
              elementW,
              elementH,
              filteredItems,
              scrollBarConfig);
    } else {
      this.listGUI =
          new ScrollableListGUI<>(
              listX, listY, widthStack, heightStack, elementW, elementH, filteredItems, false);
    }
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    // 検索入力欄の描画
    searchInput.render(context, mouseX, mouseY, delta);

    // リスト部分の描画
    listGUI.render(context, mouseX, mouseY, delta);
  }

  @Override
  public void setPos(int x, int y) {
    super.setPos(x, y);

    // 子コンポーネントの位置を更新（検索フィールドは常に最下部）
    int listHeight = height - searchInputHeight;
    int listX = x;
    int listY = y;
    int searchX = x;
    int searchY = y + listHeight;

    searchInput.setPos(searchX, searchY);
    listGUI.setPos(listX, listY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    // テキスト入力欄が優先
    if (searchInput.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }

    // リスト部分の処理
    return listGUI.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    boolean searchResult = searchInput.mouseReleased(mouseX, mouseY, button);
    boolean listResult = listGUI.mouseReleased(mouseX, mouseY, button);
    return searchResult || listResult;
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    // テキスト入力欄の処理
    if (searchInput.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
      return true;
    }

    // リスト部分の処理
    return listGUI.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    // リスト部分のスクロール処理のみ
    return listGUI.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    // フォーカスがある場合はテキスト入力欄に送る
    if (searchInput.isFocused()) {
      return searchInput.keyPressed(keyCode, scanCode, modifiers);
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    // フォーカスがある場合はテキスト入力欄に送る
    if (searchInput.isFocused()) {
      return searchInput.charTyped(chr, modifiers);
    }

    return super.charTyped(chr, modifiers);
  }

  @Override
  public void setFocused(boolean focused) {
    super.setFocused(focused);
    if (!focused) {
      searchInput.setFocused(false);
    }
  }

  /** フィルタテキストが変更された時の処理 */
  private void onFilterTextChanged(String filterText) {
    updateFilteredItems(filterText);
    updateListGUI();
  }

  /** フィルタリング済みアイテムリストを更新 */
  private void updateFilteredItems(String filterText) {
    filteredItems.clear();

    if (filterText == null || filterText.trim().isEmpty()) {
      // 空の場合は全て表示
      filteredItems.addAll(allItems);
    } else {
      // フィルタリング実行
      filteredItems.addAll(
          allItems.stream().filter(item -> filterPredicate.test(item, filterText)).toList());
    }
  }

  /** リストGUIを更新 */
  private void updateListGUI() {
    listGUI.setElements(filteredItems);
    // 検索時にスクロール位置を最上部にリセット
    listGUI.setScroll(0);
  }

  // 外部インターフェース

  /** 選択された要素を取得 */
  public Optional<T> getSelectedItem() {
    return listGUI.getSelectElement();
  }

  /** 全アイテムを設定（フィルタリングも再実行される） */
  public void setItems(Collection<T> newItems) {
    allItems.clear();
    allItems.addAll(newItems);
    onFilterTextChanged(searchInput.getText());
  }

  /** アイテムを追加 */
  public void addItem(T item) {
    allItems.add(item);
    onFilterTextChanged(searchInput.getText());
  }

  /** アイテムを削除 */
  public void removeItem(T item) {
    allItems.remove(item);
    onFilterTextChanged(searchInput.getText());
  }

  /** 全アイテムをクリア */
  public void clearItems() {
    allItems.clear();
    onFilterTextChanged(searchInput.getText());
  }

  /** 検索テキストを取得 */
  public String getFilterText() {
    return searchInput.getText();
  }

  /** 検索テキストを設定 */
  public void setFilterText(String text) {
    searchInput.setText(text);
  }

  /** プレースホルダーテキストを設定 */
  public void setPlaceholder(String placeholderText) {
    searchInput.setPlaceholder(placeholderText);
  }

  /** フィルタリング後のアイテム数を取得 */
  public int getFilteredItemCount() {
    return filteredItems.size();
  }

  /** 全アイテム数を取得 */
  public int getTotalItemCount() {
    return allItems.size();
  }

  /** スクロールバーの有無を取得 */
  public boolean hasScrollBar() {
    return listGUI.hasScrollBar();
  }

  /** 内部のListGUIを取得（高度な操作用） */
  public ScrollableListGUI<T> getListGUI() {
    return listGUI;
  }

  /** 内部のTextInputGUIを取得（高度な操作用） */
  public TextInputGUI getSearchInput() {
    return searchInput;
  }

  /** 特定のアイテムを選択状態にする */
  public boolean setSelectedItem(T item) {
    int index = filteredItems.indexOf(item);
    if (index >= 0) {
      return setSelectedIndex(index);
    }
    return false;
  }

  /** 条件にマッチする最初のアイテムを選択状態にする */
  public boolean setSelectedItemBy(Predicate<T> predicate) {
    for (int i = 0; i < filteredItems.size(); i++) {
      if (predicate.test(filteredItems.get(i))) {
        return setSelectedIndex(i);
      }
    }
    return false;
  }

  public boolean setSelectedItemBy(Predicate<T> predicate, Consumer<T> consumer) {
    for (int i = 0; i < filteredItems.size(); i++) {
      if (predicate.test(filteredItems.get(i))) {
        consumer.accept(filteredItems.get(i));
        return setSelectedIndex(i);
      }
    }
    return false;
  }

  /** インデックスで選択状態を設定（内部メソッド） */
  private boolean setSelectedIndex(int index) {
    if (index < 0 || index >= filteredItems.size()) {
      return false;
    }

    // 現在の選択を解除
    listGUI
        .getAllElements()
        .forEach(
            element -> {
              if (element instanceof ListGUIElement) {
                ((ListGUIElement) element).setSelected(false);
              }
            });

    // 新しい選択を設定
    T selectedItem = filteredItems.get(index);
    if (selectedItem instanceof ListGUIElement) {
      ((ListGUIElement) selectedItem).setSelected(true);
    }

    // スクロール位置を調整（選択されたアイテムが見えるように）
    // グリッド情報を内部で保持している値から計算
    int widthStack = Math.max(1, width / elementW);

    int scrollRow = index / widthStack;
    if (scrollRow < listGUI.size()) {
      listGUI.setScroll(scrollRow);
    } else if (scrollRow >= listGUI.size()) {
      listGUI.setScroll(listGUI.size());
    }

    return true;
  }

  // Builder パターン

  /** FilterableListGUIのBuilderを取得 */
  public static <T extends GUIElement> Builder<T> builder() {
    return new Builder<>();
  }

  /** FilterableListGUI構築用のシンプルなBuilderクラス */
  public static class Builder<T extends GUIElement> {
    private int x = 0, y = 0;
    private int width = 200, height = 150;
    private int elementW = 200, elementH = 30;
    private Collection<T> items = new ArrayList<>();
    private FilterPredicate<T> filterPredicate = FilterPredicate.containsIgnoreCase();
    private ScrollableListGUI.ScrollBarConfig scrollBarConfig = null;
    private int searchInputHeight = 20;
    private String placeholderText = "";

    /** 位置を設定 */
    public Builder<T> position(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    /** サイズを設定 */
    public Builder<T> size(int width, int height) {
      this.width = width;
      this.height = height;
      return this;
    }

    /** 要素サイズを設定 */
    public Builder<T> elementSize(int elementW, int elementH) {
      this.elementW = elementW;
      this.elementH = elementH;
      return this;
    }

    /** 表示するアイテムを設定 */
    public Builder<T> items(Collection<T> items) {
      this.items = items;
      return this;
    }

    /** フィルタリング条件を設定 */
    public Builder<T> filterBy(FilterPredicate<T> filterPredicate) {
      this.filterPredicate = filterPredicate;
      return this;
    }

    /** デフォルトのスクロールバーを有効化 */
    public Builder<T> withScrollBar() {
      this.scrollBarConfig = ScrollBarStyle.DEFAULT.getConfig(this.height);
      return this;
    }

    /** カスタムスクロールバー設定を使用 */
    public Builder<T> withScrollBar(ScrollableListGUI.ScrollBarConfig config) {
      this.scrollBarConfig = config;
      return this;
    }

    /** スクロールバーを無効化 */
    public Builder<T> withoutScrollBar() {
      this.scrollBarConfig = null;
      return this;
    }

    /** プレースホルダーテキストを設定 */
    public Builder<T> withPlaceholder(String placeholderText) {
      this.placeholderText = placeholderText;
      return this;
    }

    /** 検索入力欄の高さを設定 */
    public Builder<T> searchInputHeight(int height) {
      this.searchInputHeight = height;
      return this;
    }

    /** FilterableListGUIを構築 */
    public FilterableListGUI<T> build() {
      FilterableListGUI<T> gui =
          new FilterableListGUI<>(
              x,
              y,
              width,
              height,
              elementW,
              elementH,
              items,
              filterPredicate,
              scrollBarConfig,
              searchInputHeight);

      // プレースホルダーテキストの設定
      if (!placeholderText.isEmpty()) {
        gui.setPlaceholder(placeholderText);
      }

      return gui;
    }
  }
}
