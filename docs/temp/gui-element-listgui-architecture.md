# GUIElement & ListGUI アーキテクチャ解析

## 概要

Little Maid Model LoaderのGUIシステムにおける、`GUIElement`と`ListGUI`の仕組みを描画・操作・データの観点で整理したドキュメントです。

## GUIElement クラス

### 基本構造
- **継承**: `Drawable`, `Element` インターフェースを実装した抽象基底クラス
- **位置**: `common/src/main/java/net/sistr/littlemaidmodelloader/client/screen/GUIElement.java`
- **役割**: 全てのGUIコンポーネントの共通機能を提供

### 1. 描画（Rendering）システム

#### データフィールド
```java
protected int x, y;           // 画面上の座標
protected final int width, height;  // 固定サイズ
```

#### 描画メソッド
```java
public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);
```
- **抽象メソッド**: 各サブクラスで具体的な描画処理を実装
- **パラメータ**:
  - `DrawContext`: Minecraft のレンダリングコンテキスト
  - `mouseX, mouseY`: マウス座標（ホバー効果用）
  - `delta`: フレーム間時間差（アニメーション用）

#### 座標管理
```java
public void setPos(int x, int y)  // 位置設定
public int getX(), getY()         // 座標取得
public int getWidth(), getHeight()  // サイズ取得
```

### 2. 操作（Interaction）システム

#### マウスイベント
- `mouseClicked()`: クリック処理
- `mouseReleased()`: マウスリリース処理  
- `mouseDragged()`: ドラッグ処理
- `mouseScrolled()`: スクロール処理
- `mouseMoved()`: マウス移動処理
- `isMouseOver()`: マウスホバー判定

#### キーボードイベント
- `keyPressed()`, `keyReleased()`: キー入力処理
- `charTyped()`: 文字入力処理

### 3. データ（Data Management）システム

#### 状態管理
```java
private boolean focused;  // フォーカス状態
```

#### フォーカス制御
```java
public void setFocused(boolean focused)
public boolean isFocused()
```

---

## ListGUI クラス

### 基本構造
- **継承**: `GUIElement` を継承
- **型パラメータ**: `<T extends GUIElement>` - 表示する要素の型
- **役割**: 要素をグリッド状に配置・表示するリストコンテナ

### 1. 描画（Rendering）システム

#### レイアウト計算
```java
protected final int widthStack;   // 横方向の要素数
protected final int heightStack;  // 縦方向の要素数  
protected final int elementW;     // 各要素の幅
protected final int elementH;     // 各要素の高さ
```

#### 描画ロジック
```java
public void render(DrawContext context, int mouseX, int mouseY, float delta)
```

**描画プロセス**:
1. **グリッド計算**: `widthStack × heightStack` のグリッドを構成
2. **スクロール考慮**: `scroll` オフセットを適用
3. **要素配置**: 各要素の座標を計算して `setPos()` で設定
4. **要素描画**: 各要素の `render()` メソッドを呼び出し

#### 座標変換システム
```java
// インデックス → 座標
protected int getElementXIndex(int index)  // X座標インデックス
protected int getElementYIndex(int index)  // Y座標インデックス

// マウス座標 → 要素座標  
protected double getElementX(double mouseX)
protected double getElementY(double mouseY)

// マウス座標 → 要素インデックス
protected int getIndex(double mouseX, double mouseY)
```

### 2. 操作（Interaction）システム

#### マウス操作の委譲システム
全てのマウスイベントが以下のパターンで処理されます:

1. **要素特定**: `getElement(mouseX, mouseY)` で対象要素を取得
2. **座標変換**: グローバル座標を要素内座標に変換
3. **委譲**: 要素のイベントメソッドに処理を委譲

```java
public boolean mouseClicked(double mouseX, double mouseY, int button) {
    Optional<T> e = getElement(mouseX, mouseY);
    if (e.isPresent()) {
        T element = e.get();
        return element.mouseClicked(getElementX(mouseX), getElementY(mouseY), button);
    }
    return false;
}
```

#### スクロール機能
```java
public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    // 1. 要素レベルでの処理を試行
    // 2. 処理されない場合、リストレベルでスクロール
    scroll = scroll + (0 < amount ? -1 : 1);
    this.scroll = MathHelper.clamp(this.scroll, 0, size() / widthStack - 1);
}
```

#### 要素選択システム
`MarginedClickable` を使用した選択処理:

```java
public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (selectBox.release(mouseX, mouseY)) {  // 有効なクリックか判定
        int index = getIndex(mouseX, mouseY);
        if (checkElementsBounds(index)) {
            // 前の選択を解除
            // 新しい選択を設定
            // ListGUIElement インターフェースで状態更新
        }
    }
}
```

### 3. データ（Data Management）システム

#### 要素管理
```java
protected final ImmutableList<T> elements;  // 不変リスト
protected int selectElem = -1;               // 選択要素インデックス
```

#### スクロール状態管理  
```java
protected int scroll = 0;  // スクロールオフセット

public void setScroll(int scroll) {
    this.scroll = MathHelper.clamp(scroll, 0, size() / widthStack - 1);
}
```

#### 境界チェック
```java
protected boolean checkElementsBounds(int index) {
    return 0 <= index && index < this.elements.size();
}

protected boolean isRenderingElement(int index) {
    return scroll * widthStack <= index && 
           index < scroll * widthStack + widthStack * heightStack;
}
```

---

## 設計パターン分析

### 1. Template Method パターン
- `GUIElement.render()` は抽象メソッド
- サブクラスで具体的な描画処理を実装

### 2. Composite パターン  
- `ListGUI` は `GUIElement` のコンテナ
- 階層的なUI構造を実現

### 3. Observer パターン
- `ListGUIElement` インターフェースで選択状態を通知
- UIの状態変更を効率的に管理

### 4. Strategy パターン
- `MarginedClickable` でクリック判定の戦略を分離
- 異なるインタラクション方式に対応可能

---

## 使用例

### MultiModelGUI での実装
```java
public class MultiModelGUI extends GUIElement implements ListGUIElement {
    // 1. 描画: テクスチャと3Dモデルを描画
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // モデル描画処理
    }
    
    // 2. 操作: マウスクリックでカラー選択
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.selectColor = TextureColors.getColor(MathHelper.floor(mouseX / scale));
    }
    
    // 3. データ: 選択状態管理
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
```

### ModelSelectScreen での使用
```java
this.modelListGUI = new ListGUI<>(
    (width - scale * allColor) / 2,           // x座標
    (height - scale * heightRatio * heightStack) / 2,  // y座標  
    1, heightStack,                           // 1列、4行のグリッド
    scale * allColor, scale * heightRatio,    // 各要素のサイズ
    textureHolders.stream()                   // 表示要素のストリーム
        .map(t -> new MultiModelGUI(...))     // GUIElement に変換
        .collect(Collectors.toList())
);
```

---

## ScrollBar クラス - 統合が面倒な実装

### 基本構造
- **継承**: `GUIElement` を継承  
- **位置**: `common/src/main/java/net/sistr/littlemaidmodelloader/client/screen/ScrollBar.java`
- **役割**: 垂直スクロールバーの表示と操作

### ⚠️ 統合上の問題点

この `ScrollBar` クラス自体は150行程度のシンプルな実装ですが、**ListGUIとの統合が非常に面倒**で大量のボイラープレートコードが発生します。

#### 1. 複雑な初期化

**ModelSelectScreen での初期化例**:
```java
this.modelScrollBar = new ScrollBar(
    (width + GUI_WIDTH) / 2 + 4, (height - GUI_HEIGHT) / 2,  // 座標計算
    8, GUI_HEIGHT, this.modelListGUI.size(),                 // サイズ・要素数
    new TextureAddress(0, 200, 8, 8, 256, 256),             // 上部テクスチャ
    new TextureAddress(0, 208, 8, 8, 256, 256),             // 中間テクスチャ
    new TextureAddress(0, 216, 8, 8, 256, 256),             // 下部テクスチャ
    new TextureAddress(0, 224, 10, 6, 256, 256),            // ポインターテクスチャ
    MODEL_SELECT_GUI_TEXTURE);

// 同じパターンをarmorScrollBarでも書く必要がある
this.armorScrollBar = new ScrollBar(
    (width + GUI_WIDTH) / 2 + 4, (height - GUI_HEIGHT) / 2,  // 同じ座標計算
    8, GUI_HEIGHT, this.armorListGUI.size(),                 // 同じパターン
    new TextureAddress(0, 200, 8, 8, 256, 256),             // 同じテクスチャ設定
    new TextureAddress(0, 208, 8, 8, 256, 256),             // ...
    new TextureAddress(0, 216, 8, 8, 256, 256),
    new TextureAddress(0, 224, 10, 6, 256, 256),
    MODEL_SELECT_GUI_TEXTURE);
```

**問題点**:
1. **重複した設定**: 同じテクスチャ設定を何度も書く
2. **座標計算の手動実装**: レイアウト計算を毎回手動で行う
3. **5つのTextureAddress**: スクロールバー1つに5つの設定が必要

#### 2. 大量のボイラープレートコード

**イベント処理での重複例**:
```java
@Override
public boolean mouseClicked(double x, double y, int button) {
    if (guiSwitch) {
        // モデル用の処理
        if (modelScrollBar.mouseClicked(x, y, button)) {
            modelListGUI.setScroll(modelScrollBar.getPoint());  // 手動同期
            return true;
        } else {
            return modelListGUI.mouseClicked(x, y, button);
        }
    } else {
        // アーマー用の処理（全く同じパターン）
        if (armorScrollBar.mouseClicked(x, y, button)) {
            armorListGUI.setScroll(armorScrollBar.getPoint());  // 手動同期
            return true;
        } else {
            return armorListGUI.mouseClicked(x, y, button);
        }
    }
}

@Override
public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (guiSwitch) {
        // モデル用の処理（mouseClickedと同じパターン）
        if (modelScrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            modelListGUI.setScroll(modelScrollBar.getPoint());  // 手動同期
            return true;
        }
    } else {
        // アーマー用の処理（全く同じパターン）
        if (armorScrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            armorListGUI.setScroll(armorScrollBar.getPoint());  // 手動同期
            return true;
        }
    }
    return false;
}

@Override
public boolean mouseScrolled(double x, double y, double scrollAmount) {
    // また同じパターンの処理を書く必要がある...
}
```

**問題点**:
1. **重複したイベント処理**: 同じパターンを複数のメソッドで書く
2. **手動同期**: ScrollBarとListGUIの状態を手動で同期する必要
3. **分岐の複雑化**: 複数のScrollBarがあると条件分岐が複雑になる

#### 3. 状態管理の手動実装

**初期値設定での手動同期例**:
```java
// モデル用の初期スクロール位置設定
TextureHolder ownerSkinTex = entity.getTextureHolder(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD);
int index = 0;
for (MultiModelGUI g : this.modelListGUI.getAllElements()) {
    if (g.getTexture() == ownerSkinTex) {
        modelScrollBar.setPoint(index);                    // ScrollBar側を更新
        modelListGUI.setScroll(index);                     // ListGUI側を更新
    }
    index++;
}

// アーマー用も同じパターンで書く必要がある...
```

**問題点**:
1. **二重管理**: ScrollBarとListGUIで同じスクロール状態を別々に管理
2. **同期忘れのリスク**: 片方だけ更新してしまうバグが発生しやすい
3. **初期化の複雑さ**: 初期値設定で両方を同期する処理が必要

### 🛠️ より良い統合方法の提案

#### 1. スクロールバー内蔵型ListGUI
```java
public class ScrollableListGUI<T extends GUIElement> extends GUIElement {
    private final ListGUI<T> listGUI;
    private final ScrollBar scrollBar;
    
    // 自動で連携するコンストラクタ
    public ScrollableListGUI(int x, int y, int width, int height, 
                           Collection<T> elements, ScrollBarStyle style) {
        // ListGUIとScrollBarを自動連携で初期化
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // ScrollBarとListGUIの連携を自動処理
        return handleScrollBarOrList(mouseX, mouseY, 
            (bar, x, y, b) -> bar.mouseClicked(x, y, b),
            (list, x, y, b) -> list.mouseClicked(x, y, b));
    }
    
    // mouseDragged, mouseScrolledも同じパターンで自動処理
}
```

#### 2. ビルダーパターンによる簡潔な初期化
```java
// 使用例
this.modelListGUI = ListGUI.builder()
    .position(x, y)
    .size(width, height)
    .elements(modelElements)
    .withScrollBar(ScrollBarStyle.DEFAULT)  // スクロールバー自動追加
    .build();

this.armorListGUI = ListGUI.builder()
    .position(x, y) 
    .size(width, height)
    .elements(armorElements)
    .withScrollBar(ScrollBarStyle.DEFAULT)  // 同じ設定で簡単に追加
    .build();
```

#### 3. ListGUIへのスクロールバー機能統合
```java
public class ListGUI<T extends GUIElement> extends GUIElement {
    private Optional<ScrollBar> scrollBar = Optional.empty();
    
    // スクロールバー有効化メソッド
    public ListGUI<T> enableScrollBar(ScrollBarConfig config) {
        this.scrollBar = Optional.of(new ScrollBar(this, config));
        return this;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // スクロールバーがある場合は自動で連携処理
        if (scrollBar.isPresent() && scrollBar.get().handleEvent(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
```

### 現在の実装の根本的問題

1. **統合の複雑さ**: ScrollBarとListGUIが独立しており、手動で連携が必要
2. **ボイラープレートコード**: 同じパターンの処理を何度も書く必要
3. **保守の困難さ**: 新しいScrollBarを追加する度に大量のコードが必要
4. **設定の重複**: 同じテクスチャ設定を複数箇所で定義

---

## まとめ

### 優れた実装 (GUIElement, ListGUI)

**GUIElement・ListGUI** の設計は以下の点で優秀です:

1. **責任分離**: 描画・操作・データが明確に分離
2. **再利用性**: ジェネリック型による高い再利用性
3. **拡張性**: 新しいGUIコンポーネントを容易に追加可能
4. **座標変換**: グローバル座標と要素座標の自動変換
5. **イベント委譲**: 親→子への効率的なイベント伝播

### 統合が面倒な実装 (ScrollBar)

一方で **ScrollBar** は以下の問題により**他コンポーネントとの統合コストが非常に高い**実装となっています:

1. **統合の複雑さ**: ScrollBarとListGUIの連携に大量のボイラープレートコードが必要
2. **手動同期**: 状態管理を手動で行うため、同期忘れのバグが発生しやすい
3. **設定の重複**: 同じテクスチャ設定を複数箇所で定義する必要
4. **保守の困難さ**: 新しいスクロールバーを追加する度に大量のコードが必要

### 教訓

同じプロジェクト内でも、**コンポーネント間の統合設計**に大きな差が生まれることがあります。

**ScrollBar** は「個々の実装は問題ないが、組み合わせが面倒な設計」の典型例です。150行程度のシンプルなクラスでも、他との統合を考慮していないと開発効率が大幅に低下します。

**統合の簡潔さ**を意識した設計（内蔵型、ビルダーパターン、自動連携など）により、このような問題を避けることができます。

---

## より複雑な統合例: フィルタリング機能付きリスト

### 要件の追加

さらに複雑な例として、以下の機能を追加することを考えてみましょう:

- **テキスト入力欄**: GUIElementを継承した文字列入力機能
- **フィルタリング**: 入力文字列に一致する項目のみ表示
- **既存機能**: リスト表示 + スクロールバー + 選択機能

この組み合わせは**複雑性が指数関数的に増加**する典型例です。

### 🔥 普通に実装した場合の複雑性爆発

#### 1. 状態管理の複雑化

```java
public class ModelSelectScreen {
    // 元々のデータ
    private Collection<TextureHolder> allTextureHolders;
    
    // フィルタリング関連
    private TextInputGUI searchInput;
    private String currentFilter = "";
    private List<MultiModelGUI> filteredModelGUIs;
    private List<ArmorModelGUI> filteredArmorGUIs;
    
    // 既存のコンポーネント
    private ListGUI<MultiModelGUI> modelListGUI;
    private ListGUI<ArmorModelGUI> armorListGUI;
    private ScrollBar modelScrollBar;
    private ScrollBar armorScrollBar;
    
    // 選択状態（フィルタで見えなくなる可能性）
    private Optional<MultiModelGUI> selectedModel;
    private Optional<ArmorModelGUI> selectedArmor;
}
```

#### 2. イベントの連鎖反応

```java
// テキスト入力の変更処理
private void onSearchTextChanged(String newText) {
    this.currentFilter = newText;
    
    // 1. モデルリストをフィルタリング
    updateFilteredModels();
    // 2. アーマーリストをフィルタリング  
    updateFilteredArmors();
    // 3. ListGUIの要素を差し替え
    recreateModelListGUI();
    recreateArmorListGUI();
    // 4. スクロールバーのサイズを調整
    updateModelScrollBar();
    updateArmorScrollBar();
    // 5. スクロール位置をリセット
    resetScrollPositions();
    // 6. 選択状態を確認（見えなくなった場合は解除）
    validateSelections();
    // 7. 描画の更新
    markForRedraw();
}

// このような処理を複数のイベント（フィルタ変更、リスト変更、スクロール等）で書く必要がある
```

#### 3. ボイラープレートコードの大量発生

```java
@Override
public boolean mouseClicked(double x, double y, int button) {
    // テキスト入力の処理
    if (searchInput.mouseClicked(x, y, button)) {
        return true;
    }
    
    // 既存のリスト・スクロールバー処理（さらに複雑化）
    if (guiSwitch) {
        if (modelScrollBar.mouseClicked(x, y, button)) {
            modelListGUI.setScroll(modelScrollBar.getPoint());
            return true;
        } else {
            boolean result = modelListGUI.mouseClicked(x, y, button);
            // フィルタリングで選択が変わった場合の処理も必要
            if (result) {
                handleModelSelectionChange();
            }
            return result;
        }
    } else {
        // アーマー用も同じパターンで更に複雑化...
    }
}

// mouseReleased, mouseDragged, keyPressed, charTyped すべてで同様の複雑化
```

### 💡 解決策の提案

#### 解決策1: 統合型コンポーネント（推奨）

```java
/**
 * フィルタリング機能付きリストGUI
 * テキスト入力・フィルタリング・リスト・スクロールバーを統合
 */
public class FilterableListGUI<T extends GUIElement> extends GUIElement {
    // 内部コンポーネント（外部からは隠蔽）
    private final TextInputGUI searchInput;
    private final ListGUI<T> listGUI;
    private final Optional<ScrollBar> scrollBar;
    
    // データ管理
    private final List<T> allItems;           // 元データ
    private final List<T> filteredItems;     // フィルタ後データ
    private final FilterPredicate<T> filterPredicate;
    
    public FilterableListGUI(int x, int y, int width, int height,
                           Collection<T> items, 
                           FilterPredicate<T> filterPredicate,
                           boolean enableScrollBar) {
        super(width, height);
        this.allItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
        this.filterPredicate = filterPredicate;
        
        // 内部コンポーネントの自動配置
        this.searchInput = new TextInputGUI(x, y, width, 20);
        this.searchInput.addTextChangeListener(this::onFilterTextChanged);
        
        this.listGUI = new ListGUI<>(x, y + 25, width, height - 25, filteredItems);
        
        if (enableScrollBar) {
            this.scrollBar = Optional.of(createScrollBar());
        } else {
            this.scrollBar = Optional.empty();
        }
    }
    
    // フィルタ変更の処理（内部で完結）
    private void onFilterTextChanged(String filterText) {
        updateFilteredItems(filterText);
        updateListGUI();
        updateScrollBar();
    }
    
    // 外部インターフェース（シンプル）
    public Optional<T> getSelectedItem() {
        return listGUI.getSelectElement();
    }
    
    public void setItems(Collection<T> newItems) {
        allItems.clear();
        allItems.addAll(newItems);
        onFilterTextChanged(searchInput.getText());
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 内部で全て処理、外部は気にしなくて良い
        return searchInput.mouseClicked(mouseX, mouseY, button) ||
               scrollBar.map(sb -> sb.mouseClicked(mouseX, mouseY, button)).orElse(false) ||
               listGUI.mouseClicked(mouseX, mouseY, button);
    }
}
```

#### 解決策2: State管理の分離

```java
/**
 * フィルタリング状態の一元管理
 */
public class FilterableListState<T> {
    private final List<T> allItems;
    private final FilterPredicate<T> filterPredicate;
    private final List<StateChangeListener<T>> listeners = new ArrayList<>();
    
    private String filterText = "";
    private List<T> filteredItems;
    private int scrollPosition = 0;
    private Optional<T> selectedItem = Optional.empty();
    
    public void updateFilter(String newFilter) {
        if (!Objects.equals(this.filterText, newFilter)) {
            this.filterText = newFilter;
            this.filteredItems = applyFilter();
            this.scrollPosition = 0;  // フィルタ変更でスクロールリセット
            
            // 選択中の項目が見えなくなった場合は選択解除
            if (selectedItem.isPresent() && !filteredItems.contains(selectedItem.get())) {
                selectedItem = Optional.empty();
            }
            
            notifyStateChanged();
        }
    }
    
    private void notifyStateChanged() {
        StateChangeEvent<T> event = new StateChangeEvent<>(this);
        listeners.forEach(listener -> listener.onStateChanged(event));
    }
    
    // イミューダブルなアクセサ
    public List<T> getFilteredItems() { return List.copyOf(filteredItems); }
    public String getFilterText() { return filterText; }
    public int getScrollPosition() { return scrollPosition; }
    public Optional<T> getSelectedItem() { return selectedItem; }
}
```

#### 解決策3: Builder/Factory による簡潔な初期化

```java
// 使用例
FilterableListGUI<MultiModelGUI> modelList = FilterableListGUI.builder()
    .position(x, y)
    .size(width, height)
    .items(allModelGUIs)
    .filterBy(gui -> gui.getTexture().getTextureName().toLowerCase().contains(filter))
    .withScrollBar(ScrollBarStyle.DEFAULT)
    .withPlaceholder("モデルを検索...")
    .onSelectionChange(this::onModelSelected)
    .build();

FilterableListGUI<ArmorModelGUI> armorList = FilterableListGUI.builder()
    .position(x, y)
    .size(width, height) 
    .items(allArmorGUIs)
    .filterBy(gui -> gui.getTexture().getTextureName().toLowerCase().contains(filter))
    .withScrollBar(ScrollBarStyle.DEFAULT)
    .withPlaceholder("防具を検索...")
    .onSelectionChange(this::onArmorSelected)
    .build();
```

### 🎯 推奨アプローチ

**解決策1（統合型コンポーネント）**が最適です。理由：

1. **単一責任**: 1つのコンポーネントで完結するため、外部の複雑性が激減
2. **カプセル化**: 内部の状態管理・同期処理を隠蔽
3. **再利用性**: 他の画面でも同じパターンで使える
4. **保守性**: 関連する処理が1箇所に集約される
5. **GUIElementとの整合性**: 既存のアーキテクチャとの親和性が高い

### ModelSelectScreen での使用例

```java
public class ModelSelectScreen<T extends Entity & IHasMultiModel> extends Screen {
    private FilterableListGUI<MultiModelGUI> modelFilterableList;
    private FilterableListGUI<ArmorModelGUI> armorFilterableList;
    
    @Override
    protected void init() {
        // 従来の複雑な初期化がシンプルに
        this.modelFilterableList = FilterableListGUI.builder()
            .position((width - GUI_WIDTH) / 2, (height - GUI_HEIGHT) / 2)
            .size(GUI_WIDTH - 40, GUI_HEIGHT - 40)
            .items(createModelGUIs())
            .filterBy(this::matchesModelFilter)
            .withScrollBar(ScrollBarStyle.DEFAULT)
            .build();
            
        this.armorFilterableList = FilterableListGUI.builder()
            .position((width - GUI_WIDTH) / 2, (height - GUI_HEIGHT) / 2)
            .size(GUI_WIDTH - 40, GUI_HEIGHT - 40)
            .items(createArmorGUIs())
            .filterBy(this::matchesArmorFilter)
            .withScrollBar(ScrollBarStyle.DEFAULT)
            .build();
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        // 従来の複雑な処理が大幅にシンプル化
        return (guiSwitch ? modelFilterableList : armorFilterableList)
            .mouseClicked(x, y, button);
    }
    
    private boolean matchesModelFilter(MultiModelGUI gui, String filter) {
        return gui.getTexture().getTextureName().toLowerCase().contains(filter.toLowerCase());
    }
}
```

これにより、**数百行のボイラープレートコードが数十行に削減**され、保守性が劇的に向上します。

---

## 🚧 フィルタリング機能実装のための既存コード変更

### 現実的な課題: ImmutableListの制約

フィルタリング機能を実装する際、**既存のListGUIアーキテクチャに根本的な変更**が必要になります。

#### 現在の実装の制約

**ListGUI.java:159**:
```java
protected final ImmutableList<T> elements;  // 不変リスト = フィルタリング不可能
```

この設計では**要素を動的に変更できない**ため、フィルタリング機能が実装できません。

### 🔧 必要な既存コード変更

#### 1. ListGUI クラスの大幅な変更

**変更前**:
```java
public class ListGUI<T extends GUIElement> extends GUIElement {
    protected final ImmutableList<T> elements;  // 不変
    
    public ListGUI(int x, int y, int widthStack, int heightStack, 
                   int elementW, int elementH, Collection<T> elements) {
        // ...
        this.elements = ImmutableList.copyOf(elements);  // 固定化
    }
    
    public int size() {
        return elements.size();  // 固定サイズ
    }
}
```

**変更後**:
```java
public class ListGUI<T extends GUIElement> extends GUIElement {
    protected final List<T> elements;  // ミュータブルに変更
    
    public ListGUI(int x, int y, int widthStack, int heightStack, 
                   int elementW, int elementH, Collection<T> elements) {
        // ...
        this.elements = new ArrayList<>(elements);  // ミュータブル化
    }
    
    // 新しいメソッドが必要
    public void setElements(Collection<T> newElements) {
        elements.clear();
        elements.addAll(newElements);
        
        // スクロール位置の再計算
        if (scroll >= size() / widthStack) {
            scroll = Math.max(0, size() / widthStack - 1);
        }
        
        // 選択状態のリセット
        if (selectElem >= elements.size()) {
            selectElem = -1;
        }
    }
    
    public void addElement(T element) {
        elements.add(element);
    }
    
    public void removeElement(T element) {
        int index = elements.indexOf(element);
        if (index != -1) {
            elements.remove(index);
            // 選択状態の調整
            if (selectElem == index) {
                selectElem = -1;
            } else if (selectElem > index) {
                selectElem--;
            }
        }
    }
    
    public int size() {
        return elements.size();  // 動的サイズ
    }
}
```

#### 2. ScrollBar クラスの変更

**変更前**:
```java
public class ScrollBar extends GUIElement {
    private final int elemSize;  // 固定サイズ
    
    public ScrollBar(int x, int y, int width, int height, int elemSize, ...) {
        this.elemSize = elemSize;  // コンストラクタで固定
    }
}
```

**変更後**:
```java
public class ScrollBar extends GUIElement {
    private int elemSize;  // ミュータブルに変更
    
    // 新しいメソッドが必要
    public void setElemSize(int newElemSize) {
        this.elemSize = newElemSize;
        
        // ポイント位置の再調整
        this.point = MathHelper.clamp(this.point, 0, elemSize - 1);
    }
    
    public float getPercent() {
        if (elemSize == 0) return 0.0f;  // ゼロ除算対策も必要
        return ((float) getPoint() / elemSize);
    }
}
```

#### 3. ModelSelectScreen の大幅な変更

**変更前**:
```java
@Override
protected void init() {
    // 要素を一度だけ作成
    this.modelListGUI = new ListGUI<>(..., 
        textureHolders.stream()
            .map(t -> new MultiModelGUI(...))
            .collect(Collectors.toList())
    );
}
```

**変更後**:
```java
@Override
protected void init() {
    // 全要素をフィールドで保持
    this.allModelGUIs = textureHolders.stream()
        .map(t -> new MultiModelGUI(...))
        .collect(Collectors.toList());
    
    // 初期状態では全要素を表示
    this.modelListGUI = new ListGUI<>(..., allModelGUIs);
    
    // ScrollBarも要素数の変更に対応
    this.modelScrollBar = new ScrollBar(...);
    updateScrollBarForCurrentFilter();
}

// フィルタリング処理（新規実装）
private void applyModelFilter(String filterText) {
    List<MultiModelGUI> filtered = allModelGUIs.stream()
        .filter(gui -> matchesFilter(gui, filterText))
        .collect(Collectors.toList());
    
    modelListGUI.setElements(filtered);           // ListGUI更新
    modelScrollBar.setElemSize(filtered.size());  // ScrollBar更新
}
```

#### 4. 他の画面への影響

**SoundPackSelectScreen.java** でもListGUIを使用しているため、同様の変更が必要：

```java
// 現在の実装
this.soundPackListGUI = new ListGUI<>(...,
    LMConfigManager.INSTANCE.getAllConfig().stream()
        .map(c -> new SoundPackGUI(...))
        .collect(Collectors.toList()));

// フィルタリング対応のため、全要素の保持が必要
private List<SoundPackGUI> allSoundPackGUIs;
private void initializeSoundPackGUIs() {
    allSoundPackGUIs = LMConfigManager.INSTANCE.getAllConfig().stream()
        .map(c -> new SoundPackGUI(...))
        .collect(Collectors.toList());
    
    soundPackListGUI = new ListGUI<>(..., allSoundPackGUIs);
}
```

### ⚠️ 互換性への影響

#### 破壊的変更のリスク

1. **コンストラクタシグネチャ**: 変更の可能性
2. **内部状態の変更**: `elements` がミュータブルになることによる副作用
3. **スレッドセーフティ**: 並行アクセス時の問題
4. **既存の利用箇所**: 全てのListGUI使用箇所で確認が必要

#### 段階的移行戦略

```java
// Phase 1: 後方互換性を維持した拡張
public class ListGUI<T extends GUIElement> extends GUIElement {
    protected final List<T> elements;
    
    @Deprecated
    public ListGUI(int x, int y, int widthStack, int heightStack, 
                   int elementW, int elementH, Collection<T> elements) {
        // 既存コンストラクタは維持
        this(x, y, widthStack, heightStack, elementW, elementH, elements, false);
    }
    
    public ListGUI(int x, int y, int widthStack, int heightStack, 
                   int elementW, int elementH, Collection<T> elements, 
                   boolean mutable) {
        // mutableフラグで動作を制御
        if (mutable) {
            this.elements = new ArrayList<>(elements);
        } else {
            this.elements = List.copyOf(elements);  // Java 10+ の不変リスト
        }
    }
    
    // 新機能は mutable=true の場合のみ有効
    public void setElements(Collection<T> newElements) {
        if (!(elements instanceof ArrayList)) {
            throw new UnsupportedOperationException("This ListGUI is immutable");
        }
        // ...実装
    }
}
```

### 🎯 推奨実装アプローチ

#### Option A: 新クラス作成（安全）

```java
public class MutableListGUI<T extends GUIElement> extends ListGUI<T> {
    // フィルタリング対応の新しい実装
}

public class FilterableListGUI<T extends GUIElement> extends MutableListGUI<T> {
    // 統合型フィルタリング実装
}
```

#### Option B: 既存クラス拡張（効率的）

```java
public class ListGUI<T extends GUIElement> extends GUIElement {
    // 段階的移行戦略を採用
    // 既存コードの互換性を維持しながら新機能を追加
}
```

### 📝 変更の優先順位

1. **High**: ListGUI の elements ミュータブル化
2. **High**: ScrollBar の elemSize 動的変更対応
3. **Medium**: フィルタリング機能の統合
4. **Low**: 他画面への適用

**結論**: フィルタリング機能は魅力的ですが、**既存アーキテクチャへの影響が大きい**ため、慎重な設計と段階的な実装が必要です。