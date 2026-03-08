# IModelCaps と EntityCaps の対応状況調査

## 背景

TODO「caps対応が不完全なため、その対応」のために、IModelCaps で定義されている全 caps 定数と EntityCaps で実際に register() されているものを突き合わせた。

## 対象ファイル

- `common/.../maidmodel/IModelCaps.java` — LMML の caps 定数定義
- `common/.../maidmodel/EntityCaps.java` — LMML の EntityCaps 実装
- `.gradle/MMMLib/.../mc162/IModelCaps.java` — オリジナル MMMLib の caps 定数定義
- `.gradle/MMMLib/.../mc162/ModelMultiBase.java` — オリジナルの getCapsValue/setCapsValue 実装
- `.gradle/MMMLib/.../mc162/ModelMultiMMMBase.java` — オリジナルの実験コード（renderFace/Body, textureLightColor 等）

## MMMLib オリジナルの設計

### caps の2つの実装レイヤー

MMMLib では caps は **2箇所** で実装されていた:

1. **EntityCaps（Entity側）**: Entity のデータを読み取る。LittleMaid mod 側で実装（MMMLib にはソースなし）
2. **ModelMultiBase（Model側）**: モデルの状態（onGround, isRiding 等）を保持し get/set する

Model 側の caps はレンダリング前に EntityCaps から値を読み取り、`setCapsValue()` でモデルに設定するフローだった。

### ModelMultiBase の getCapsValue（オリジナル）

| caps | 返り値型 | 内容 |
|------|---------|------|
| `caps_onGround` | float[] | `onGrounds` 配列（左右の腕振り） |
| `caps_isRiding` | boolean | 騎乗中フラグ |
| `caps_isSneak` | boolean | スニーク中フラグ |
| `caps_isWait` | boolean | 待機中フラグ |
| `caps_isChild` | boolean | 子どもフラグ |
| `caps_heldItemLeft` | float | 左手アイテム（`heldItem[1]`） |
| `caps_heldItemRight` | float | 右手アイテム（`heldItem[0]`） |
| `caps_aimedBow` | boolean | 弓構えフラグ |
| `caps_entityIdFactor` | float | Entity ID 係数（アニメーション同期用） |
| `caps_ticksExisted` | int | 存在 Tick 数 |
| `caps_ScaleFactor` | float | スケール係数（デフォルト 0.9375F） |
| `caps_dominantArm` | int | 利き腕（0=右, 1=左） |

### ModelMultiBase の setCapsValue（オリジナル）

上記の全12個が set 可能だった。レンダリング前に Entity のデータをモデルに流し込むために使用。

### ModelMultiMMMBase の追加 caps（オリジナル）

**setCapsValue:**
| caps | 引数型 | 内容 |
|------|--------|------|
| `caps_changeModel` | (IModelCaps) | モデル変更時コールバック。`changeModel()` を呼ぶ |
| `caps_renderFace` | (IModelCaps, float x7, boolean) | Face テクスチャの描画。Actors 用分離描画 |
| `caps_renderBody` | (IModelCaps, float x7, boolean) | Body テクスチャの描画。Actors 用分離描画 |

**getCapsValue:**
| caps | 返り値型 | 内容 |
|------|---------|------|
| `caps_setFaceTexture` | int | 表情テクスチャの UV オフセット切替。引数 (int index) で顔テクスチャを選択 |
| `caps_textureLightColor` | float[] | 発光テクスチャの色補正。`{r, g, b, a}` の float 配列。null なら白 (1,1,1,1) |

### ModelLittleMaidBase での使用例（オリジナル）

```java
// setLivingAnimations 内
float angle = ModelCapsHelper.getCapsValueFloat(pEntityCaps, caps_interestedAngle, (Float)pRenderPartialTicks);
bipedHead.setRotateAngleZ(angle);

// renderItems 内
boolean lplanter = ModelCapsHelper.getCapsValueBoolean(pEntityCaps, caps_isPlanter);
if (ModelCapsHelper.getCapsValueBoolean(pEntityCaps, caps_isCamouflage) || lplanter) {
    // 頭部装飾品の描画
}
```

### ModelBaseDuo での textureLightColor 使用（オリジナル）

```java
// 発光テクスチャ描画時
if (textureLightColor == null) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
} else {
    GL11.glColor4f(textureLightColor[0], textureLightColor[1],
                   textureLightColor[2], textureLightColor[3]);
}
```

`textureLightColor` は `ModelBaseDuo` のフィールドとして保持。モデルの `getTextureLightColor(IModelCaps)` で取得。

---

## 調査結果

### 対応済み (40個)

EntityCaps の static ブロックで register() されているもの。

| カテゴリ | caps |
|---------|------|
| ModelBase | onGround, isRiding, isChild |
| ModelBiped | heldItemLeft, heldItemRight, heldItems, isSneak, aimedBow |
| Entity基本 | Entity, health, ticksExisted, currentEquippedItem, currentArmor, healthFloat |
| 手持ち | currentLeftHandItem, currentRightHandItem |
| EntityLiving状態 | isWet, isDead, isInWeb, isSwingInProgress, isBurning, isInWater, isInvisible, isSprinting |
| 名前系 | getRidingName, getRidingType, entityName |
| 位置・速度 | posX, posY, posZ, pos, motionX, motionY, motionZ, motion |
| BoundingBox | boundingBox |
| 回転 | rotationYaw, rotationPitch, prevRotationYaw, prevRotationPitch, renderYawOffset, renderRidingYOffset |
| ブロック | PosBlockID, PosBlockState, PosBlockAir, PosBlockLight, PosBlockPower |
| 騎乗 | isRidingPlayer |
| ワールド | WorldTotalTime, WorldTime, MoonPhase |
| サイズ | height, width, YOffset, mountedYOffset, dominantArm |
| 1.16+ | isSwimming, roll, leaningPitch, lastLeaningPitch, isUsingRiptide, isFallFlying |
| Pose | pose, isPoseStanding, isPoseFallFlying, isPoseSleeping, isPoseSwimming, isPoseSpinAttack, isPoseCrouching, isPoseDying |

### 未対応 — 全 caps 詳細仕様

#### A. LivingEntity で実装可能 (3個)

| caps | ID | 返り値型 | 説明 | オリジナル実装 | LMML 実装方針 |
|------|----|---------|------|--------------|--------------|
| `caps_isJumping` | 0x0032 | boolean | ジャンプ中か | EntityCaps 側（MMMLib にソースなし） | `LivingEntity.jumping` (protected)。Mixin Accessor で取得 |
| `caps_isLeeding` | 0x003a | boolean | リードで繋がれているか | EntityCaps 側 | `MobEntity.isLeashed()` で取得。LivingEntity でない場合 false |
| `caps_isUpdateSize` | 0x0004 | boolean | リアルタイムでサイズ更新するか | MMMLib IModelCaps に定義のみ。Entity の身長変動で使用 | デフォルト false。メイドMod 側がオーバーライドで対応 |

#### B. Entity/テクスチャ関連 (3個)

| caps | ID | 返り値型 | 説明 | オリジナル実装 | LMML 実装方針 |
|------|----|---------|------|--------------|--------------|
| `caps_TextureEntity` | 0x0026 | Entity | テクスチャ選択用 Entity 参照 | EntityCaps 側。通常は owner と同一 | `owner` を返す |
| `caps_entityIdFactor` | 0x0120 | float | Entity ID 係数。アニメーション位相ずれ用 | ModelMultiBase: フィールド `entityIdFactor`。Entity側から `setCapsValue` で設定 | `entity.getId() * 70` 等でユニーク値を算出 |
| `caps_textureLightColor` | 0x0314 | float[] | 発光テクスチャの色補正 `{r,g,b,a}`。null=白 | ModelMultiMMMBase: `getTextureLightColor(IModelCaps)` で取得。ModelBaseDuo の描画で使用 | EntityCaps の責務外。モデル側(IMultiModel)で対応すべき |

#### C. メイドさん専用 (21個) — メイドMod 側の Entity 実装が必要

LMML はモデルローダーであり、メイドさん Entity の実装は別 Mod 側にある。
これらの caps はメイドMod が独自の IModelCaps 実装（EntityCaps を継承など）で対応すべきもの。

全て **boolean** 型。

| caps | ID | 説明 | モデルでの使用例 |
|------|----|------|----------------|
| `caps_isRendering` | 0x0100 | 本体（アーマーでなく）のレンダリング中か | ModelBaseSolo/Duo: `isRendering` フラグとして render() に渡す |
| `caps_isBloodsuck` | 0x0101 | 血を吸っているか（攻撃モード） | モデルの表情・ポーズ切替 |
| `caps_isFreedom` | 0x0102 | 自由行動中か | モデルのポーズ切替 |
| `caps_isTracer` | 0x0103 | トレーサーモードか | モデルのポーズ切替 |
| `caps_isPlaying` | 0x0104 | 遊んでいるか | モデルのポーズ切替 |
| `caps_isLookSuger` | 0x0105 | 砂糖を見ているか | モデルのポーズ切替（おねだり） |
| `caps_isBlocking` | 0x0106 | 盾等でブロッキング中か | モデルの腕ポーズ |
| `caps_isWait` | 0x0107 | 待機中か | ModelMultiBase: `isWait` フィールドで待機ポーズ。腕を前で組む等 |
| `caps_isWaitEX` | 0x0108 | 拡張待機中か | 追加の待機モーション |
| `caps_isOpenInv` | 0x0109 | インベントリ開いているか | UI 状態でのモデル表示制御 |
| `caps_isWorking` | 0x010a | 作業中か | モデルのポーズ切替（作業モーション） |
| `caps_isWorkingDelay` | 0x010b | 作業ディレイ中か | 作業アニメーションのタイミング |
| `caps_isContract` | 0x010c | 契約済みか | テクスチャ・モデル切替 |
| `caps_isContractEX` | 0x010d | 契約済み(拡張)か | テクスチャ・モデル切替 |
| `caps_isRemainsC` | 0x010e | 残存契約か（マスター不在） | テクスチャ・モデル切替 |
| `caps_isClock` | 0x010f | 時計を持っているか | 表示用 |
| `caps_isMasked` | 0x0110 | マスクしているか | 頭部パーツ表示制御 |
| `caps_isCamouflage` | 0x0111 | カモフラージュ中か（頭にブロック） | `renderItems()` で頭部装飾品の描画制御 |
| `caps_isPlanter` | 0x0112 | プランターか（頭に植物） | `renderItems()` で HeadTop からの装飾品描画 |
| `caps_isOverdrive` | 0x0113 | オーバードライブ中か | 戦闘モーション強化 |
| `caps_isOverdriveDelay` | 0x0114 | オーバードライブディレイ中か | オーバードライブアニメーション |

#### D. モデル・レンダリング関連 (9個)

これらは **ModelMultiBase 側で管理** されるcaps。EntityCaps ではなくモデル自身が get/set する。

| caps | ID | 返り値型 | 説明 | オリジナル実装 | LMML 実装方針 |
|------|----|---------|------|--------------|--------------|
| `caps_render` | 0x0130 | Render | Minecraft のレンダラー参照 | ModelMultiBase: `render` フィールド。`setRender()` で設定 | LMML では不要（アーキテクチャが異なる） |
| `caps_Arms` | 0x0131 | ModelRenderer[] | 手持ちアイテム用マウントポイント | ModelMultiBase: `Arms[2]` 配列。手持ちアイテム描画位置 | IMultiModel で対応 |
| `caps_HeadMount` | 0x0132 | ModelRenderer | 頭部マウントポイント | ModelMultiBase: `HeadMount` | **@Deprecated** — 対応不要 |
| `caps_HardPoint` | 0x0133 | ModelRenderer[] | ハードポイント配列 | ModelMultiBase: `HardPoint[]` | IMultiModel で対応 |
| `caps_stabiliser` | 0x0134 | Map<String, EquippedStabilizer> | スタビライザー装備 | ModelMultiMMMBase: `stabiliser` フィールド | メイドMod 依存 |
| `caps_Items` | 0x0135 | ItemStack[] | 保持アイテム配列 | EntityCaps 側で実装 | `entity.getItemsEquipped()` で取得可能 |
| `caps_Actions` | 0x0136 | int[] | アイテム挙動配列（使用/攻撃/ブロック等） | EntityCaps 側。メイドさん固有 | メイドMod 依存 |
| `caps_Grounds` | 0x0137 | float[] | 振り回し状態配列（左右の腕振り進行度） | EntityCaps 側 | メイドMod 依存 |
| `caps_Ground` | 0x0139 | float | Ground 単値 | 旧互換 | デフォルト 0F |

#### E. インベントリ・状態 (4個)

| caps | ID | 返り値型 | 説明 | オリジナル実装 | LMML 実装方針 |
|------|----|---------|------|--------------|--------------|
| `caps_Inventory` | 0x0138 | Inventory | インベントリオブジェクト | EntityCaps 側 | PlayerEntity なら `getInventory()`、メイドさんはメイドMod 側 |
| `caps_interestedAngle` | 0x0150 | float | 興味角度。引数に renderPartialTicks を取る | EntityCaps 側。`setLivingAnimations()` で `bipedHead.setRotateAngleZ(angle)` | メイドMod 依存。メイドさんの首傾げ |
| `caps_job` | 0x0151 | String | 職業名（小文字） | EntityCaps 側 | メイドMod 依存 |
| `caps_motionSitting` | 0x0401 | boolean? | お座りモーション | カスタム追加分 | メイドMod 依存 |

#### F. PlayerFormLittleMaid (5個)

プレイヤーがメイドさんの姿になるシステム用。LMML の EntityCaps の対応範囲外。

| caps | ID | 返り値型 | 説明 | オリジナル実装 |
|------|----|---------|------|--------------|
| `caps_ScaleFactor` | 0x0200 | float | スケール係数 | ModelMultiBase: `scaleFactor` フィールド（デフォルト 0.9375F） |
| `caps_PartsVisible` | 0x0201 | ? | パーツの表示/非表示制御 | PlayerForm 側で実装 |
| `caps_Posing` | 0x0202 | ? | ポージング指定 | PlayerForm 側で実装 |
| `caps_Actors` | 0x0203 | ? | アクター指定 | PlayerForm 側で実装 |
| `caps_PartsStrings` | 0x0204 | ? | パーツ文字列指定 | PlayerForm 側で実装 |

#### G. テスト・Face・テクスチャ (5個)

モデル側で get/set される。レンダリングパイプラインで使用。

| caps | ID | 返り値型/引数 | 説明 | オリジナル実装 |
|------|----|-------------|------|--------------|
| `caps_changeModel` | 0x0300 | set: (IModelCaps) | モデル変更時コールバック | ModelMultiMMMBase: `setCapsValue` で `changeModel(pEntityCaps)` を呼ぶ |
| `caps_renderFace` | 0x0310 | set: (IModelCaps, float x6, boolean) | Face テクスチャの分離描画 | ModelMultiMMMBase: `setCapsValue` で `renderFace()` を呼ぶ。Actors 用 |
| `caps_renderBody` | 0x0311 | set: (IModelCaps, float x6, boolean) | Body テクスチャの分離描画 | ModelMultiMMMBase: `setCapsValue` で `renderBody()` を呼ぶ。Actors 用 |
| `caps_setFaceTexture` | 0x0312 | get: (int index) → int | 表情テクスチャ UV 切替 | ModelMultiMMMBase: `getCapsValue` で `setFaceTexture(index)` を呼ぶ。GL の UV オフセット |
| `caps_textureData` | 0x0313 | get: () → TextureData | TextureData インスタンス | EntityCaps 側（テクスチャ管理） |

#### H. sleepingDirection (1個)

| caps | ID | 返り値型 | 説明 | LMML 実装方針 |
|------|----|---------|------|--------------|
| `caps_sleepingDirection` | 0x0800 | Direction? | 睡眠時の向き | **バグあり**: 後述。`entity.getSleepingDirection()` で取得 |

---

## バグ

### EntityCaps.java:223 — sleepingDirection の登録バグ

```java
// 行222: 正常
register("isPoseDying", caps_isPoseDying, (entity, arg) -> entity.isDead());
// 行223: バグ — caps_isPoseDying を上書きし、sleepingDirection が未登録
register("isPoseDying", caps_isPoseDying, (entity, arg) -> entity.getSleepingDirection());
```

**影響:**
- `caps_isPoseDying` の Getter が `entity.getSleepingDirection()` で上書きされ、死亡判定が壊れている
- `caps_sleepingDirection` は未登録のまま
- ただし `caps` Map は `putIfAbsent` なので名前→ID のマッピングは正しい（最初の登録が優先）

正しくは：
```java
register("sleepingDirection", caps_sleepingDirection, (entity, arg) -> entity.getSleepingDirection());
```

---

## LMML 独自追加分（MMMLib にない caps）

LMML の IModelCaps には MMMLib オリジナルにない caps が追加されている:

| caps | ID | 追加理由 |
|------|----|---------|
| `caps_currentLeftHandItem` | 0x0027 | 1.9+ の二刀流対応 |
| `caps_currentRightHandItem` | 0x0028 | 1.9+ の二刀流対応 |
| `caps_getRidingType` | 0x003c | 騎乗 Entity の種別判定 |
| `caps_entityName` | 0x003d | Entity 名取得 |
| `caps_renderRidingYOffset` | 0x006e | 騎乗時高さ調整 |
| `caps_PosBlockState` | 0x0082 | BlockState 取得（旧 PosBlockMeta の代替） |
| `caps_job` | 0x0151 | メイドさんの職業 |
| `caps_isSwimming` ~ `caps_isFallFlying` | 0x0600-0x0605 | 1.16+ の泳ぎ・エリトラ対応 |
| `caps_pose` ~ `caps_isPoseDying` | 0x0700-0x0707 | 1.14+ の EntityPose 対応 |
| `caps_sleepingDirection` | 0x0800 | 睡眠方向 |
| `caps_motionSitting` | 0x0401 | お座りモーション |

---

## 判断結果（2026-03-08 確定）

### 今回実装する

| # | caps | 対応内容 |
|---|------|---------|
| 1 | `caps_sleepingDirection` バグ修正 | EntityCaps:223 の登録を修正 |
| 2 | `caps_isJumping` (0x0032) | EntityCaps に追加。Mixin Accessor で `LivingEntity.jumping` を取得 |
| 3 | `caps_isLeeding` (0x003a) | EntityCaps に追加。`MobEntity.isLeashed()` |
| 4 | `caps_textureLightColor` (0x0314) | MultiModelLightLayer / MultiModelArmorLayer で発光描画時にモデルから色を取得して反映 |
| 5 | `caps_changeModel` (0x0300) | モデル変更時に `model.setCapsValue(caps_changeModel, caps)` を呼ぶ |

### 放置（古い・用途不明）

| caps | 理由 |
|------|------|
| `caps_isUpdateSize` (0x0004) | 古い機能。用途不明 |

### 危険（渡すとクラッシュの可能性）— 対応しない

| caps | 理由 |
|------|------|
| `caps_TextureEntity` (0x0026) | 古いモデル側のコードが Entity を直接操作してクラッシュする |
| `caps_Items` (0x0135) | 同上。旧 ItemStack を期待する古いモデルでクラッシュ |
| `caps_render` (0x0130) | 旧 Render クラスを期待。アーキテクチャ非互換 |

### メイド側調査時に更新

| caps | 理由 |
|------|------|
| `caps_entityIdFactor` (0x0120) | メイドMod のソースで算出方法を確認してから対応 |

### メイドMod 側で対応すべきもの（LMML の範囲外） — 登録せず放置

- 0x01xx 系（メイドさん専用状態）21個
- interestedAngle, job, motionSitting
- stabiliser, Actions, Grounds
- Inventory（メイドさんの場合）

### 技術的に不可能（現アーキテクチャと非互換）

| caps | 理由 |
|------|------|
| `caps_renderFace` (0x0310) | Actors 機能（テクスチャ3枚分離描画）が LMML にない。VertexConsumer 方式と非互換 |
| `caps_renderBody` (0x0311) | 同上 |
| `caps_setFaceTexture` (0x0312) | GL11 UV 操作が VertexConsumer 方式と非互換 |

### 対応不要

| caps | 理由 |
|------|------|
| `caps_HeadMount` (0x0132) | @Deprecated |
| `caps_textureData` (0x0313) | TextureHolder で代替済み |

### 別 Mod (TransformIntoLittleMaid) で利用の可能性あり — 定数は残す

| caps | 備考 |
|------|------|
| `caps_ScaleFactor` (0x0200) | TransformIntoLittleMaid で使用する可能性 |
| `caps_PartsVisible` (0x0201) | 同上 |
| `caps_Posing` (0x0202) | 同上 |
| `caps_Actors` (0x0203) | 同上 |
| `caps_PartsStrings` (0x0204) | 同上 |
