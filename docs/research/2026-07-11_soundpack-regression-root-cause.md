# サウンドパック再生回帰バグ・選択画面ぼかしバグの原因調査

## 背景

1.21.1 移植後、サウンドパックを選択してもメイドさんの声が鳴らない (デフォルトのガスト声のみ鳴る) 回帰バグが LMRB 側で確認された (TODO 高)。また、サウンドパック選択画面の背景ぼかしが画面自身の UI テクスチャまでぼかす問題も併発している。9.0.0 正式版の最大ブロッカーであるため原因を調査した。

## 調査結果

### バグ 1: サウンドパックの声が鳴らない

**根本原因: `Sound#getLocation()` の override が 1.21.1 移植時に失われ、リソースパスが二重ラップされている。**

再生経路は以下の通り。

1. `LMSoundLoader#load` が `ResourceHelper.getLocation("sounds", packName, fileName)` で
   `littlemaidmodelloader:sounds/<pack>/<file>.ogg` という Identifier を生成
   (`fileName` は `.ogg` 付きのまま渡される)。
2. 同じ Identifier で `ResourceWrapper.addResourcePath()` に実ファイルを登録し、
   `LMSoundManager#addSound()` で `Sound` を生成する。
3. 再生時、バニラの `SoundSystem#play` は `Sound#getLocation()` の返り値で
   `SoundLoader#loadStatic/loadStreamed` を呼び、リソースマネージャ (= ResourceWrapper 含む)
   から .ogg を取得する (SoundSystem.class バイトコードで確認)。

1.21.1 の `Sound#getLocation()` は `FINDER.toResourcePath(id)` であり、`FINDER` は
`new ResourceFinder("sounds", ".ogg")`。つまり id に対して **`sounds/` prefix と `.ogg` suffix を
再付与する** (`id.withPath("sounds/" + path + ".ogg")`)。

LMML は既に prefix/suffix 付きの id を渡しているため、SoundSystem が要求するパスは

```
littlemaidmodelloader:sounds/sounds/<pack>/<file>.ogg.ogg
```

となり、`ResourceWrapper.PATHS` に登録したキーと一致せず、.ogg が見つからず無音になる。

**1.20.1 で動いていた理由**: `LMSoundManager#addSound` は `Sound` の匿名サブクラスで
`getLocation()` を override し `getIdentifier()` (raw id) をそのまま返して二重ラップを回避していた。
1.21.1 移植のコミット `a2df84a` (フェーズ 2-6 API 追従) で `new Sound(String, ...)` →
`new Sound(Identifier, ...)` に書き換えた際、この匿名サブクラス (= override) が削除された。
1.20 ブランチとの diff で確認済み。

**ガスト声だけ鳴る理由**: デフォルト声はバニラ登録のサウンドイベント経由
(`LMSoundManager#getSound` の `contains(":")` 分岐 → バニラ `SoundManager#get`) であり、
ResourceWrapper を通らないため影響を受けない。

**修正方針**: 1.20.1 と同様に匿名サブクラスで `getLocation()` を override して
`getIdentifier()` を返す。1.21.1 の `Sound` は通常の public クラスで `getLocation()` も
public インスタンスメソッドのため override 可能 (javap で確認)。

代替案として「`Sound` に prefix/suffix なしの id を渡し、getLocation() の再付与結果が
ResourceWrapper の登録キーに一致するようにする」方法もあるが、`WeightedSoundSet` や
`LMSoundInstance#getId` など raw id を使う箇所への影響範囲が広がるため、override 復活が最小。

### バグ 2: 選択画面の背景ぼかしが UI まで巻き込む

**根本原因: `SoundPackSelectScreen#render` が UI テクスチャを `super.render()` より前に描画している。**

`SoundPackSelectScreen.java:87-89` では

1. `context.drawTexture(...)` で GUI テクスチャを描画
2. その後 `super.render(...)` を呼ぶ

1.21 の `Screen#render` は冒頭で `renderBackground` を呼び、そこで **ブラーポストプロセッサが
フレームバッファ全体に適用される**。そのため 1 で描いたテクスチャごとぼける。

**修正方針**: `super.render()` を先に呼び、その後に GUI テクスチャと
`soundPackListGUI.render()` を描画する順序に変更する。

なお `ModelSelectScreen` は `super.render()` を一切呼んでいないため、この問題は発生しない
(代わりに背景の暗転もない)。

## 結論

- 声が鳴らないバグ: `LMSoundManager#addSound` に `getLocation()` override を復活させる (1 箇所の修正)。
- ぼかしバグ: `SoundPackSelectScreen#render` の描画順を `super.render()` 先行に入れ替える (1 箇所の修正)。
- どちらも修正は小さく、実機での確認項目は「サウンドパック選択後に声が鳴る」
  「選択画面の UI が鮮明に表示される」の 2 点。
