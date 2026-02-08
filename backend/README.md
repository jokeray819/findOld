# 怀旧帮寻物 MVP 后端

Spring Boot 3.x + Java 17 + MySQL，实现求助发帖、解答、赏金托管、达人认证、检索与个人中心等核心接口。

## 技术栈

- Spring Boot 3.2 / Java 17
- Spring Data JPA / MySQL 8
- Spring Security + JWT
- 微信小程序登录（code2session，无配置时返回 mock openid）

## 快速启动

1. 配置数据库：修改 `src/main/resources/application.yml` 中的 `spring.datasource.url/username/password`，或通过环境变量 `DB_PASSWORD` 传入密码。
2. 可选：配置 JWT 密钥 `JWT_SECRET`、微信小程序 `WECHAT_APP_ID`、`WECHAT_SECRET`。
3. 运行：`mvn spring-boot:run`，默认端口 8080。

## 主要 API

- `POST /api/auth/wechat/mini-login`：微信小程序登录（body: `{ "code": "..." }`），返回 JWT 与用户信息。
- `GET/POST/PUT /api/posts`、`GET /api/posts/:id`：帖子列表、详情、创建、编辑。
- `GET /api/posts/search?q=...`：模糊检索（q + type/sceneEra/tagIds/bountyMode）。
- `POST/GET /api/posts/:postId/answers`、`POST /api/posts/:postId/answers/:answerId/accept`：解答与采纳。
- `POST/GET /api/bounty/orders`、`POST /api/bounty/orders/:id/escrow`、`POST /api/bounty/orders/:id/confirm-pay`：赏金订单与托管/确认打款。
- `GET/POST /api/experts`、`POST /api/experts/apply`、`GET /api/experts/me`：达人列表、申请认证、当前用户认证状态。
- `POST/DELETE /api/favorites/posts/:postId`、`GET /api/favorites/posts`：收藏帖子。
- `POST/DELETE /api/follows/experts/:userId`、`GET /api/follows/experts`：关注达人。
- `GET /api/users/me`、`GET /api/users/me/posts`、`GET /api/users/me/answers`：个人中心。

除 `/api/auth/**` 外，其余接口需在 Header 中携带 `Authorization: Bearer <jwt>`。

## 数据库

首次启动时 JPA 会按实体建表（`ddl-auto: update`）。预设标签在启动时自动初始化（80后/90后/00后、童年玩具、老零食等）。

## 后续扩展

- 真实支付：托管时调微信支付统一下单，确认打款时调企业付款。
- 内容审核：帖子/解答已预留 `auditStatus` 字段，可接敏感词或人工审核。
- 全文检索：可接入 Elasticsearch 替代当前 MySQL 模糊查询。
