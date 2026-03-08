## Project Overview

Little Maid Model Loader (LMML) is a Minecraft mod that provides a multi-model system for entity rendering with dynamic external resource loading, implemented using Architectury API for cross-platform compatibility between Fabric and Forge.

## Architecture

- Architectury Loom multi-module: `common/`, `fabric/`, `forge/`
- 共通コードは `common/src/main/java/net/sistr/littlemaidmodelloader/`
- マルチモデル: `multimodel/` (モデル定義、レイヤー)
- リソース管理: `resource/` (loader, holder, manager, classloader)
- クライアント: `client/` (renderer, screen, resource)
- エンティティ: `entity/` (compound)
- ネットワーク: `network/`
- Mixin: `mixin/` (fabric/forge 各モジュール内)
- 設定: `config/`
- 外部リソース読み込み: ゲームディレクトリの `LMMLResources/` フォルダから

## Environment

- Minecraft 1.20.1, Gradle 7.4, Architectury API
- Java 17 が必要（`~/.gradle/gradle.properties` で `org.gradle.java.home` 設定済み）

## Build & Test

- `./gradlew spotlessApply` - コード整形 (google-java-format)
- `./gradlew spotlessCheck` - 整形チェック
- `./gradlew checkstyleMain` - Checkstyle スタイルチェック
- `./gradlew spotbugsMain` - SpotBugs バグ検出
- `python3 .claude/scripts/spotbugs-report.py` - SpotBugs レポート解析（`--summary`, `--priority N`）
- `./gradlew :common:test` - ユニットテスト (JUnit 5)
- `./gradlew build` - ビルド
- `./gradlew :common:build` - common モジュールのみビルド

## Localization and Communication Guidelines

- メイドさんのことはメイドさんと呼んでください。 (Always refer to maids as "メイドさん")

## TODO 管理

- `TODO.md` をタスクリストとして自律管理する
- 開発中に発見した課題・技術的負債・リファクタ候補などを随時追記する
- 完了したタスクは削除する（履歴は不要）
- 優先度（高/中/低）でカテゴリ分けする
- Notion タスクボードは公開されているため、ユーザーの明示的な指示がない限り変更しない（読み取りは自由）

## 作業記録

- 設計判断や重要な技術的決定を行った際は `docs/` に作業記録を残す
- ユーザーの指示がなくても、記録に値する判断をした場合は自律的に `/doc` スキルで記録する
- 形式: `docs/{category}/yyyy-mm-dd_{タイトル}.md`
- カテゴリ: `adr/`（設計判断）, `plan/`（作業プラン）, `research/`（調査メモ）等

## Cross-Environment Workflow

- WSL2 から Windows リポジトリへローカルremote経由で転送可能
- `git remote add local /mnt/v/Develop/Minecraft/LMML`
- Windows側でチェックアウト中のブランチにはpush不可。別ブランチ名にpush: `git push local 1.20:wsl/{branch-name}`

### Code Editing Guidelines
- 既存ファイルを Write で全体書き換えする際は、既存の内容が失われないよう注意する（Edit で差分追加を優先）
- 返り値にOptionalを使用し、フィールドや引数には@Nullableを使用する
- org.jetbrains.annotations.Nullableを使用する
- @Nullable フィールドはローカル変数にキャッシュしてから使用する（SpotBugs NP_NULL_PARAM_DEREF 対策）
- Mixin Accessor は `util/` に配置し、メソッド名に `_LM` サフィックスを付ける（例: `getBrewTime_LM()`）
- protected フィールド/メソッドへの外部アクセスが必要な場合、同パッケージ内ならパッケージプライベートゲッターを追加する（Mixin Accessor より簡潔）

### Architecture Notes
- `getNavigation()` は `MobEntity` に定義（`LivingEntity` ではない）
- `initGoals()` は `MobEntity` コンストラクタ内で呼ばれる — サブクラスのフィールドは未初期化。外部委譲時はラムダで遅延参照すること

### Rendering Notes
- カスタムシェーダーは `assets/minecraft/shaders/core/` に配置する（ShaderProgramコンストラクタが `minecraft` 名前空間前提）
- 最大輝度の light 値は `LightmapTextureManager.MAX_LIGHT_COORDINATE = 15728880`（`0xF000F0`）
- `MultiModelRenderLayer` は `RenderLayer` を継承し、`RenderPhase` の protected 定数にアクセスする
- 発光テクスチャ用カスタムシェーダー `lmml_emissive` は `ClientReloadShadersEvent`（Architectury）で登録

### API Research
- Minecraft バニラ・Fabric・Forge などの前提 Mod の API 調査には必ず `mc-api-research` エージェントを使用する
- `.gradle` キャッシュの jar を直接検索しない

### Design Review Guidelines
- レビュー観点: SOLID原則, Effective Java, Law of Demeter / Tell Don't Ask, OOPアンチパターン
- `super` 呼び出しを含む override メソッドは外部クラスに委譲できない — 本体に残す
- 状態を持たないオーケストレーション/ファクトリは static ユーティリティクラスで可（過度なオブジェクト化を避ける）
