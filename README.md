# Emortion Journal - 感情記録アプリケーション

感情を記録し、振り返ることができるアプリケーションのバックエンドAPIサーバーです。

## 概要

Emortion Journalは、日々の感情や気分を記録・管理するためのWebアプリケーションです。このリポジトリはバックエンドAPIを提供し、ユーザー認証、感情エントリのCRUD操作、データの永続化を担当します。

### 主な機能

- ユーザー登録とJWT認証
- 感情レベルとメモの記録
- タグによる分類
- 自分のエントリの取得・更新・削除
- セキュアなパスワード管理（BCrypt）

## 技術スタック

- **Java**: 17
- **フレームワーク**: Spring Boot 3.5.6
  - Spring Web
  - Spring Data JPA
  - Spring Security
- **データベース**: PostgreSQL
- **認証**: JWT (JSON Web Token)
- **ビルドツール**: Maven

## 前提条件

このプロジェクトを実行するには、以下がインストールされている必要があります：

- Java 17 以上
- Maven 3.6 以上
- PostgreSQL（接続可能な状態）

## セットアップ

### 1. リポジトリのクローン

```bash
git clone https://github.com/yourusername/emortion-journal.git
cd emortion-journal
```

### 2. データベースの準備

PostgreSQLで新しいデータベースを作成します：

```sql
CREATE DATABASE emortion_journal;
```

### 3. 環境変数の設定

プロジェクトルートに `.env.example` ファイルを参考に、必要な環境変数を設定します。

**環境変数一覧：**

| 変数名 | 説明 | 例 |
|--------|------|-----|
| `SPRING_DATASOURCE_URL` | PostgreSQLの接続URL | `jdbc:postgresql://localhost:5432/emortion_journal` |
| `SPRING_DATASOURCE_USERNAME` | データベースユーザー名 | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | データベースパスワード | `your_password` |
| `jwt.secret` | JWT署名用の秘密鍵（Base64エンコード） | `your_base64_secret` |
| `jwt.expirationSeconds` | JWTトークンの有効期限（秒） | `3600` |

環境変数は `application.properties` で読み込まれます。システム環境変数またはIDEの実行設定で設定してください。

### 4. ビルドと起動

Maven Wrapperを使用してアプリケーションをビルド・起動します：

```bash
# ビルド
./mvnw clean install

# 起動
./mvnw spring-boot:run
```

アプリケーションは `http://localhost:8080` で起動します。

## API仕様

### 認証

JWT (JSON Web Token) を使用した認証システムです。ログイン後に取得したトークンを、以降のリクエストのAuthorizationヘッダーに含めます。

```
Authorization: Bearer <your_jwt_token>
```

### エンドポイント一覧

#### 1. ユーザー登録

新規ユーザーを登録します。

**エンドポイント:** `POST /api/users`

**リクエストボディ:**

```json
{
  "username": "user123",
  "password": "securePassword123"
}
```

**レスポンス（201 Created）:**

```json
{
  "id": 1,
  "username": "user123",
  "password": null,
  "createdAt": "2026-01-06"
}
```

#### 2. ログイン

ユーザー認証を行い、JWTトークンを取得します。

**エンドポイント:** `POST /api/login`

**リクエストボディ:**

```json
{
  "username": "user123",
  "password": "securePassword123"
}
```

**レスポンス（200 OK）:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user123"
}
```

**エラーレスポンス（401 Unauthorized）:**

```json
{
  "message": "Invalid username or password"
}
```

#### 3. 感情エントリの作成

新しい感情記録を作成します（要認証）。

**エンドポイント:** `POST /api/entries`

**ヘッダー:**

```
Authorization: Bearer <token>
```

**リクエストボディ:**

```json
{
  "moodLevel": 7,
  "memo": "今日はとても良い一日でした",
  "tags": ["happy", "productive"]
}
```

**レスポンス（201 Created）:**

```json
{
  "id": 1,
  "userId": 1,
  "entryDate": "2026-01-06T14:30:00",
  "moodLevel": 7,
  "memo": "今日はとても良い一日でした",
  "tags": ["happy", "productive"]
}
```

#### 4. 感情エントリ一覧の取得

ログインユーザーの全エントリを取得します（降順）。

**エンドポイント:** `GET /api/entries`

**ヘッダー:**

```
Authorization: Bearer <token>
```

**レスポンス（200 OK）:**

```json
[
  {
    "id": 2,
    "userId": 1,
    "entryDate": "2026-01-06T14:30:00",
    "moodLevel": 7,
    "memo": "今日はとても良い一日でした",
    "tags": ["happy", "productive"]
  },
  {
    "id": 1,
    "userId": 1,
    "entryDate": "2026-01-05T10:15:00",
    "moodLevel": 5,
    "memo": "普通の日",
    "tags": ["neutral"]
  }
]
```

#### 5. 感情エントリの更新

既存のエントリを更新します（所有者のみ）。

**エンドポイント:** `PUT /api/entries/{id}`

**ヘッダー:**

```
Authorization: Bearer <token>
```

**リクエストボディ:**

```json
{
  "moodLevel": 8,
  "memo": "更新されたメモ",
  "tags": ["updated"]
}
```

**レスポンス（200 OK）:**

```json
{
  "id": 1,
  "userId": 1,
  "entryDate": "2026-01-05T10:15:00",
  "moodLevel": 8,
  "memo": "更新されたメモ",
  "tags": ["updated"]
}
```

#### 6. 感情エントリの削除

指定したエントリを削除します（所有者のみ）。

**エンドポイント:** `DELETE /api/entries/{id}`

**ヘッダー:**

```
Authorization: Bearer <token>
```

**レスポンス（204 No Content）:**

レスポンスボディなし

## データモデル

### UserEntity (users テーブル)

| カラム名 | 型 | 説明 |
|----------|-----|------|
| id | BIGINT | 主キー（自動生成） |
| username | VARCHAR(50) | ユーザー名（ユニーク） |
| password_hash | VARCHAR(255) | BCryptハッシュ化されたパスワード |
| created_at | DATE | アカウント作成日 |

### MoodEntry (mood_entries テーブル)

| カラム名 | 型 | 説明 |
|----------|-----|------|
| id | BIGINT | 主キー（自動生成） |
| user_id | BIGINT | ユーザーID（外部キー） |
| entry_date | TIMESTAMP | エントリ作成日時（自動生成） |
| mood_level | INTEGER | 感情レベル（1-10などの数値） |
| memo | TEXT | メモ（任意） |

### mood_entry_tags テーブル

| カラム名 | 型 | 説明 |
|----------|-----|------|
| entry_id | BIGINT | エントリID（外部キー） |
| tags | VARCHAR(255) | タグ文字列 |

## セキュリティ

### JWT認証フロー

1. クライアントが `/api/login` にusernameとpasswordを送信
2. サーバーが認証情報を検証
3. 認証成功時、JWTトークンを発行
4. クライアントは以降のリクエストで `Authorization: Bearer <token>` ヘッダーを付与
5. サーバーは `JwtRequestFilter` でトークンを検証し、ユーザーを識別

### パスワードの保護

- ユーザー登録時にBCryptアルゴリズムでパスワードをハッシュ化
- データベースには平文パスワードを保存しない
- Spring Securityの `PasswordEncoder` を使用

### CORS設定

以下のオリジンからのアクセスを許可：

- `http://localhost:5173` （Vite開発サーバー）
- `http://localhost:3000` （Next.js開発サーバー）
- `https://emortion-journal-frontend.vercel.app/` （本番環境）

## 開発

### ビルド

```bash
./mvnw clean install
```

### テスト実行

```bash
./mvnw test
```

### 開発モードでの起動

```bash
./mvnw spring-boot:run
```

### コードのフォーマット

```bash
./mvnw spring-javaformat:apply
```

## デプロイ

### 本番環境での設定

1. **環境変数の設定**
   - デプロイ先のプラットフォーム（Railway、Heroku等）で環境変数を設定
   - 特に `jwt.secret` は強力なランダム文字列（Base64エンコード）を使用

2. **データベース接続**
   - PostgreSQLインスタンスのURLを `SPRING_DATASOURCE_URL` に設定
   - SSLが必要な場合は接続URLに `?sslmode=require` を追加

3. **ビルドとパッケージング**
   ```bash
   ./mvnw clean package -DskipTests
   ```
   - `target/emortion-journal-0.0.1-SNAPSHOT.jar` が生成されます

4. **本番起動**
   ```bash
   java -jar target/emortion-journal-0.0.1-SNAPSHOT.jar
   ```

### セキュリティ上の注意

- 本番環境では必ず強力な `jwt.secret` を使用してください
- データベースの認証情報は環境変数で管理し、コードにハードコーディングしないでください
- HTTPSを使用してトークンの盗聴を防いでください

## プロジェクト構造

```
src/main/java/com/example/emortion_journal/
├── EmotionJournalApplication.java    # メインクラス
├── config/
│   └── SecurityConfig.java           # Spring Security設定
├── security/
│   ├── JwtTokenUtil.java             # JWTユーティリティ
│   └── JwtRequestFilter.java         # JWT認証フィルター
├── contoroller/
│   ├── UserEntryController.java      # ユーザー登録・ログイン
│   ├── MoodEntryController.java      # 感情エントリAPI
│   └── DebugController.java          # デバッグ用
├── model/
│   ├── UserEntity.java               # ユーザーエンティティ
│   ├── MoodEntry.java                # 感情エントリエンティティ
│   ├── UserEntityRepository.java     # ユーザーリポジトリ
│   └── MoodEntryRepository.java      # エントリリポジトリ
├── service/
│   ├── UserEntryService.java         # ユーザー管理サービス
│   └── MoodEntryService.java         # エントリ管理サービス
└── exception/
    ├── ResourceNotFoundException.java # カスタム例外
    └── GlobalExceptionHandler.java    # グローバル例外ハンドラ
```

## ライセンス

このプロジェクトは個人開発プロジェクトです。

## 関連リンク

- フロントエンド: https://emortion-journal-frontend.vercel.app/
- Spring Boot公式ドキュメント: https://spring.io/projects/spring-boot
- JWT公式サイト: https://jwt.io/
