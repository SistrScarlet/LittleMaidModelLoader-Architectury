# GUI改善実装計画書

## 🎯 概要

Little Maid Model LoaderのGUIシステムにおいて、統合の複雑さを軽減し、フィルタリング機能を追加するための実装計画。

**計画策定日**: 2025年8月13日  
**対象システム**: `common/src/main/java/net/sistr/littlemaidmodelloader/client/screen/`

---

## 📊 現状分析

### 優れた設計
- ✅ **GUIElement**: 抽象基底クラスとして適切な責任分離
- ✅ **ListGUI**: ジェネリック型による高い再利用性とイベント委譲システム

### 問題のある設計
- ❌ **ScrollBar統合**: ListGUIとの連携で大量のボイラープレートコード
- ❌ **ImmutableList**: 動的な要素変更（フィルタリング）が不可能
- ❌ **手動同期**: 複数コンポーネント間の状態同期が手動で複雑

### 影響範囲
- **直接影響**: `ModelSelectScreen.java`, `SoundPackSelectScreen.java`
- **間接影響**: 全てのGUIElementサブクラス

---

## 🎯 実装目標

### Primary Goals (Must Have)
1. **統合の簡潔化**: ScrollBarとListGUIの連携を自動化
2. **フィルタリング機能**: テキスト検索による動的リスト絞り込み
3. **後方互換性**: 既存コードへの破壊的変更を最小化

### Secondary Goals (Should Have)
4. **コード削減**: ボイラープレートコードを80%以上削減
5. **再利用性向上**: 他画面でも簡単に利用可能
6. **保守性向上**: 関連処理の一箇所集約

### Future Goals (Could Have)
7. **水平スクロール**: 将来的な機能拡張への対応
8. **アニメーション**: UI遷移の滑らかさ向上

---

## 🚀 段階的実装計画

### Phase 1: 基盤整備（3-5日）
**目標**: 破壊的変更を避けた新コンポーネントの実装

#### 1.1 MutableListGUI の実装
```java
public class MutableListGUI<T extends GUIElement> extends GUIElement {
    protected final List<T> elements;  // ArrayList使用
    
    // 動的要素変更メソッド
    public void setElements(Collection<T> newElements);
    public void addElement(T element);
    public void removeElement(T element);
}
```

**作業項目**:
- [ ] MutableListGUI クラス作成
- [ ] 既存ListGUIとの互換性テスト
- [ ] 単体テスト実装

#### 1.2 ScrollableListGUI の実装
```java
public class ScrollableListGUI<T extends GUIElement> extends MutableListGUI<T> {
    private final Optional<ScrollBar> scrollBar;
    
    // 自動連携機能
    private void syncScrollBar();
    private boolean handleScrollBarOrList(MouseEvent event);
}
```

**作業項目**:
- [ ] ScrollableListGUI クラス作成  
- [ ] ScrollBarとの自動連携実装
- [ ] イベント処理の統合テスト

### Phase 2: フィルタリング機能（5-7日）
**目標**: テキスト検索による動的フィルタリング機能の実装

#### 2.1 FilterableListGUI の実装
```java
public class FilterableListGUI<T extends GUIElement> extends ScrollableListGUI<T> {
    private final TextInputGUI searchInput;
    private final FilterPredicate<T> filterPredicate;
    
    // 統合された機能
    private void onFilterTextChanged(String filterText);
    private void updateFilteredItems(String filterText);
}
```

**作業項目**:
- [ ] TextInputGUI クラス作成
- [ ] FilterPredicate インターフェース定義
- [ ] フィルタリングロジック実装
- [ ] UI統合テスト

#### 2.2 Builder/Factory パターン実装
```java
FilterableListGUI<MultiModelGUI> modelList = FilterableListGUI.builder()
    .position(x, y)
    .size(width, height)
    .items(allModelGUIs)
    .filterBy(gui -> gui.getTexture().getTextureName().toLowerCase().contains(filter))
    .withScrollBar(ScrollBarStyle.DEFAULT)
    .build();
```

**作業項目**:
- [ ] Builder クラス実装
- [ ] ScrollBarStyle 列挙型作成
- [ ] 設定オプションの整理

### Phase 3: 実用化・統合（3-4日）
**目標**: 既存画面への適用と品質向上

#### 3.1 ModelSelectScreen への適用
```java
// Before: 100+ lines of boilerplate code
// After: ~20 lines of clean code

private FilterableListGUI<MultiModelGUI> modelFilterableList;
private FilterableListGUI<ArmorModelGUI> armorFilterableList;
```

**作業項目**:
- [ ] ModelSelectScreen リファクタリング
- [ ] 既存機能の動作確認
- [ ] UI/UX テスト

#### 3.2 SoundPackSelectScreen への適用
**作業項目**:
- [ ] SoundPackSelectScreen リファクタリング
- [ ] 音声パック検索機能の実装

#### 3.3 品質向上・最適化
**作業項目**:
- [ ] パフォーマンステスト
- [ ] メモリ使用量確認
- [ ] エラーハンドリング改善

---

## ⚠️ リスク評価と対策

### High Risk
| リスク | 影響度 | 対策 |
|--------|--------|------|
| **既存機能の破綻** | 高 | 段階的導入、既存コードは温存 |
| **パフォーマンス劣化** | 中 | 事前ベンチマーク、プロファイリング |
| **UI一貫性の破綻** | 中 | 既存UIとの整合性確認 |

### Medium Risk  
| リスク | 影響度 | 対策 |
|--------|--------|------|
| **メモリリーク** | 中 | イベントリスナーの適切な解放 |
| **スレッドセーフティ** | 低 | 単一スレッド前提での実装 |

### Low Risk
| リスク | 影響度 | 対策 |
|--------|--------|------|
| **学習コスト** | 低 | 詳細なドキュメント作成 |
| **テスト工数増加** | 低 | 自動化テストの充実 |

---

## ⏱️ 工数見積もり

### Phase別見積もり
| Phase | 開発 | テスト | ドキュメント | 合計 |
|-------|------|--------|-------------|------|
| Phase 1 | 3日 | 1日 | 1日 | 5日 |
| Phase 2 | 5日 | 1日 | 1日 | 7日 |
| Phase 3 | 2日 | 1日 | 1日 | 4日 |
| **合計** | **10日** | **3日** | **3日** | **16日** |

### 要員構成（推奨）
- **メイン開発者**: 1名（全フェーズ）
- **レビュワー**: 1名（コードレビュー）
- **テスター**: 0.5名（UI/UXテスト）

---

## 🎯 成功基準

### 機能要件
- [ ] フィルタリング機能が動作する（検索文字列による絞り込み）
- [ ] 既存のモデル・アーマー選択機能が正常動作する
- [ ] スクロールバーが正常に連携する

### 非機能要件
- [ ] **パフォーマンス**: 既存比較で±5%以内の応答時間
- [ ] **メモリ**: 既存比較で+10%以内のメモリ使用量
- [ ] **安定性**: 1時間連続操作でクラッシュしない

### 保守性要件
- [ ] **コード削減**: ModelSelectScreenのコード量を50%以上削減
- [ ] **再利用性**: 新しい画面で<20行でフィルタリングリストを実装可能
- [ ] **ドキュメント**: API仕様書と使用例の作成

---

## 📋 実装チェックリスト

### Phase 1 完了条件
- [ ] MutableListGUI実装完了
- [ ] ScrollableListGUI実装完了
- [ ] 既存機能との互換性確認
- [ ] 単体テスト通過

### Phase 2 完了条件
- [ ] FilterableListGUI実装完了
- [ ] Builder パターン実装完了
- [ ] フィルタリング機能動作確認
- [ ] 統合テスト通過

### Phase 3 完了条件
- [ ] ModelSelectScreen適用完了
- [ ] SoundPackSelectScreen適用完了
- [ ] 全機能の動作確認
- [ ] ドキュメント作成完了

---

## 📚 参考資料

- [GUI Element & ListGUI アーキテクチャ解析](../temp/gui-element-listgui-architecture.md)
- 既存コードベース: `common/src/main/java/net/sistr/littlemaidmodelloader/client/screen/`

---

## 📝 実装ログ

**計画策定**: 2025/08/13 - 初版作成

**Phase開始予定**: TBD  
**Phase完了予定**: TBD

---

*この計画書は実装の進行に合わせて随時更新されます。*