# TODO

## 中

- [ ] `PlayerList.tracking` の実装がプラットフォームで食い違っている。Fabric は `PlayerLookup.tracking()`(バニラ tracker の listener = **自分自身を含まない**、視界距離で絞られる)、NeoForge/Forge は `world.getPlayers()`(全員、距離無視、自分も含む)。同じ API 名で意味が違うため、これに依存するコードは片方のプラットフォームで壊れる。実際に TILM で「自分のモデル同期の返信が Fabric でだけ本人に届かない」不具合が出た(2026-07-26、TILM 側で暫定対処済み: `TILM/docs/research/2026-07-26_self-model-sync-hitbox-mismatch.md`)。NeoForge 側を tracker ベースに揃えるのが本筋だが、`sendS2CPacket` の利用側が「自分にも届く」前提になっていないか確認してから直すこと
- [ ] 防具モデル選択GUIでの選択解除機能

## 低

- [ ] LivingVoiceRate実装
- [ ] アイテム保持時の描画位置がズレてるかも問題
- [ ] パック内ファイルパスに記号使用時の２重登録
- [ ] アーマーモデルについて、エンチャント光沢が出ない不具合について確認する
- [ ] サウンドパック選択画面を開いたとき、サウンドが2重に再生される。1.21.1 移植前からの仕様か不明で要調査（実機検証時に発覚、対応は保留）

