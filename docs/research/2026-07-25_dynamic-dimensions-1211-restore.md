# 1.21.1 移植で失われた動的サイズ・視点・騎乗オフセットの復元方針

調査日: 2026-07-25 / 対象: LMML・LMRB・TILM 共通

## 背景

1.21 で `getDimensions(EntityPose)` / `getEyeHeight(EntityPose)` が final 化され、
移植時にこれらの override を落としたことで以下の機能が失われた。

- モデルサイズに追従した hitbox (動的 dimensions)
- モデルサイズに追従した視点高さ (動的 EyeHeight)
- 騎乗まわりのオフセット (旧 `getHeightOffset` / `getMountedHeightOffset`)
- `EntityCaps` の `YOffset` (固定値 0 化)

当初は `EntityAttachments` 経由での代替を想定していたが、**この想定は誤り**だった。
`EntityAttachments` は `EntityType` 登録時に確定する静的データで、インスタンス単位で
差し替える公式 API を持たない。正解は後述の `getBaseDimensions` である。

## バニラ API の実態 (1.21.1 / Yarn 1.21.1+build.3)

### final 化されたのは `LivingEntity` 側だけ

```java
// LivingEntity
public final EntityDimensions getDimensions(EntityPose pose);   // final
protected EntityDimensions getBaseDimensions(EntityPose pose);  // ← final ではない
// Entity
public EntityDimensions getDimensions(EntityPose pose);         // final ではない
public final float getEyeHeight(EntityPose pose);               // final
```

`LivingEntity#getDimensions` は「SLEEPING なら固定値、それ以外は
`getBaseDimensions(pose).scaled(getScale())`」という実装で、
**サブクラスの拡張点は `getBaseDimensions` に移された**。
バニラ自身も SlimeEntity・ArmorStandEntity はここを override している。

`Entity#getEyeHeight` は final だが中身は `getDimensions(pose).eyeHeight()` だけなので、
`EntityDimensions` 側に埋め込めば追従する。専用の拡張点は不要。

### `EntityDimensions` は record

```java
public record EntityDimensions(float width, float height, float eyeHeight,
                               EntityAttachments attachments, boolean fixed) {
    public static EntityDimensions changing(float width, float height);
    public EntityDimensions withEyeHeight(float eyeHeight);
    public EntityDimensions withAttachments(EntityAttachments.Builder attachments);
    public EntityDimensions scaled(float ratio);
    public EntityDimensions scaled(float widthRatio, float heightRatio);
}
```

- `changing(w, h)` の既定 eyeHeight は `h * 0.85F`、attachments は `EntityAttachments.of(w, h)`
- `scaled` は width・height・eyeHeight・attachments を**まとめて一貫スケール**する
  (attachments は Y に heightRatio、X/Z に widthRatio)。ゆえに `scaled` は必ず最後に呼ぶ
- `withAttachments` に渡す `EntityAttachments.Builder` は、`build(width, height)` の時点で
  **未指定の type に既定値を自動で埋める**。PASSENGER だけ指定しても VEHICLE・NAME_TAG は壊れない
- `fixed` が立っていると `scaled` は no-op

### 騎乗オフセットの後継

| 1.20.1 | 1.21.1 | final |
|---|---|---|
| `getMountedHeightOffset()` (自分に乗せる位置) | `EntityAttachmentType.PASSENGER` の点 / `getPassengerRidingPos` | 非 final |
| `getHeightOffset()` (自分が乗るときの位置) | `getVehicleAttachmentPos(Entity)` | 非 final |

1.20.1 は `vehicleY + vehicle.getMountedHeightOffset() + passenger.getHeightOffset()` の
**加算式**、1.21.1 は `vehicle.getPassengerRidingPos(p) - p.getVehicleAttachmentPos(v)` の
**減算式**。移植時は符号の向きが逆になる点に注意。

## 実装時の罠

1. **`getScaleFactor()` の再適用漏れ** — `getBaseDimensions` の既定実装
   (`getType().getDimensions().scaled(getScaleFactor())`) が内包しているため、
   override すると子どもスケールが消える。自前で `.scaled(getScaleFactor())` を掛け直す
2. **`calculateDimensions()` を呼ばないと視点が変わらない** — `getEyeY()` / `getEyePos()` が
   参照するのは `standingEyeHeight` フィールドで、これは `calculateDimensions()` /
   `reinitDimensions()` の実行時にしか `dimensions.eyeHeight()` から再同期されない
3. **リサイズ時の位置ジッター** — `calculateDimensions()` は `refreshPosition()` を呼ぶ。
   Slime・ArmorStand は `calculateDimensions()` 自体を override し、前後で x/y/z を
   保存・復元してこれを打ち消している。モデル変更時のみ呼ぶ運用なら影響は限定的
4. **PASSENGER の既定値では旧仕様を再現できない** — 既定は `(0, height, 0)` だが、
   旧モデルの `mountedYOffset` は `height * 0.75F` や固定 `0.35F` などモデルごとにバラバラ。
   `withAttachments` での明示指定が必要
5. **scale の二重適用** — `LivingEntity#getPassengerRidingPos` は
   `getScale() * getScaleFactor()` を掛ける。独自スケールと衝突させない

## 各 mod の現状と方針

### LMML — `MultiModelEntity` (テスト用エンティティ)

override は完全に削除され、`Registration.java:27,37` の `dimensions(0.5F, 1.35F)` 固定値のみ。
`MultiModelEntity.java:164` の `calculateDimensions()` は override がないため空回りしている。

入力値の取得経路は健在 (`IMultiModel#getWidth/getHeight/getEyeHeight/getyOffset/getMountedYOffset`)。
→ `getBaseDimensions` を override し、`withEyeHeight` + `withAttachments` + `scaled` で再構築する。

### LMML — モデル選択 GUI (実装後に判明した波及)

`MultiModelGUIUtil.DummyModelEntity` は `MultiModelEntity` を継承せず `LivingEntity` を
直接継承する別クラスなので、`getBaseDimensions` の override が届かず hitbox が
`EntityType` の固定値のままだった。1.21 の `InventoryScreen.drawEntity` は矩形指定 +
自動センタリング型で、**エンティティの dimensions を基準に配置する**ため、
GUI プレビュー側も実寸を返さないとモデルを切り替えても表示が変わらない。

さらに hitbox を可変にすると、drawEntity が足元を

```
feet_screen_y = 矩形中心 + (entity_height / 2 + yOffset) * size
```

に置く仕様上、身長の高いモデルほど足元が下がる (標準 1.35 / Chloe2 1.8 / Beverly7 1.99)。
`yOffset` を固定値にせず `FEET_ORIGIN_OFFSET - height/2` として `height/2` を打ち消すことで
モデル間で足元位置を揃えた。`FEET_ORIGIN_OFFSET` は矩形中心のオフセット 1.5 と一致させる
(移植当初は実質 1.675 で、超過分の 0.175 = 16px テクスチャの約 3 ドットだけ
足元が矩形外にはみ出し scissor でクリップされていた)。

**教訓**: エンティティの寸法を可変にする変更は、寸法を前提に位置決めしている描画側にも
波及する。同じインターフェースを実装する別クラス (ここでは `IHasMultiModel` を実装する
GUI 用ダミー) を取りこぼしていないか確認すること。

### LMRB — `LittleMaidEntity` (本体)

計画書に項目が無かったが、**部分的にしか復元されていない**ことが判明した。

- `getBaseDimensions` (`LittleMaidEntity.java:707`) — 寸法自体は復元済みだが、
  1.20.1 にあった `.scaled(getScaleFactor())` が欠落していた
- `getVehicleAttachmentPos` (`:691`) — 2026-07-20 のボート修正で復元済み
- 自分が乗り物になる場合の PASSENGER オフセット (旧 `getMountedHeightOffset`) — 未対応だった

なお **eyeHeight は 1.21.1 の機能ロスではない**。1.20.1 の `LittleMaidEntity` も
`getActiveEyeHeight` を override しておらず、元からバニラ既定 (`height * 0.85F`) である。
`getActiveEyeHeight` を override していたのは LMML の `MultiModelEntity` の方で、
両者は 1.20.1 の時点から非対称だった。LMRB 側を揃えるかは機能追加の判断として TODO に残す。

### LMRB — `EntityCaps`

- `caps_YOffset` (`EntityCaps.java:303`) — 0 固定。外部モデルパックが参照し得る公開 caps
- `caps_mountedYOffset` (`:305-313`) — 固定 0 ではなく `getPassengerRidingPos` ベースの
  代替計算に置換済み。ただしモデル定義値は反映されない
- `caps_roll` (`:323`) — `getRoll()` → `getFallFlyingTicks()` の名称変更に追従済みで、
  **機能ロスではなかった**。計画書のフェーズ 2.5 からは除外してよい

### TILM — `MixinPlayerEntity`

TODO.md の「`getDimensions`/`getEyeHeight` は Mixin 不能」「`getHeightOffset` は代替 API なし」は
いずれも**誤り**と判明した。

- `PlayerEntity` は `getBaseDimensions` を自前で override 済み (水泳・睡眠ポーズ用) なので、
  `@Mixin(PlayerEntity.class)` から `@Inject(method = "getBaseDimensions", ...)` が remap を通る
- eyeHeight は `withEyeHeight` で同時に解決するため専用の注入ポイントは不要
- 騎乗 Y オフセットは `getVehicleAttachmentPos` の `@Override` で復元可能
- 変身フラグは `PlayerEntity` の DataTracker にあり、サーバー側でも保持されている

`Entity` / `LivingEntity` へ Mixin 対象を広げる必要はない。

## 同期とサーバーサイドの前提

寸法の数値そのものを同期する必要はない。`MultiModelCompound#writeToPacket/readFromPacket` は
テクスチャ名等のみを送り、両側が同じモデル名から同一 `IMultiModel` を解決する設計のため、
寸法計算はサーバー・クライアントで独立に同じ値になる。

ただし `ModelMultiBase` の基底 `ModelBase` がクライアント専用の `EntityRenderer` を
フィールド型に持つ (`maidmodel/ModelBase.java:6,14`)。フィールドに触れない限り
遅延解決で問題は出ない見込みだが、**dedicated server での実機確認が必要**。

## 関連

- [littlemaid 1.21.1 正式版リリース計画](../../../2026-07-11_littlemaid-1.21.1-plan.md) フェーズ 2.5
- [LMML 1.21.1 移植の振り返り](2026-06-06_lmml-1.21.1-migration-lessons.md) 9 章 (機能ロス一覧)
