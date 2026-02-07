# 怀旧帮寻物 - 微信小程序前端

基于 uni-app (Vue 3) 的微信小程序，对接 [backend](../backend) 提供的 API。

**说明**：uni-app CLI 要求源码在 `src/` 下（如 `src/manifest.json`、`src/pages.json`、`src/pages/` 等），且自定义组件通过 `pages.json` 的 easycom 自动引入，无需在页面里 import。

## 环境要求

- Node.js 16+
- 微信开发者工具

## 安装与运行

```bash
# 安装依赖
npm install

# 开发模式（编译到 dist/dev/mp-weixin，用微信开发者工具打开该目录）
npm run dev:mp-weixin

# 生产构建
npm run build:mp-weixin
```

## 配置

1. **后端地址**：在 [api/request.js](api/request.js) 中修改 `BASE_URL`，开发时默认 `http://localhost:8080`。小程序正式版需在小程序后台配置 request 合法域名。
2. **微信小程序**：在 [manifest.json](manifest.json) 的 `mp-weixin.appid` 中填写你的 AppID。本地开发时在微信开发者工具中勾选「不校验合法域名」即可访问本地后端。

## 功能说明

- **首页**：求助列表，按类型/年代/赏金筛选，搜索入口，发布求助
- **帖子详情**：查看描述与图片、回答列表、写回答（公开+仅求助者可见）、采纳、收藏、赏金记录入口
- **发布/编辑**：物品类型、年代、描述、赏金方式与金额、快速标签、封面/手绘图 URL
- **搜索**：关键词 + 类型/年代筛选
- **赏金记录**：我的赏金订单，去托管、确认打款
- **我的收藏**：收藏的帖子列表
- **怀旧达人**：达人列表、我关注的、达人认证申请
- **个人中心**：登录、我的发帖/解答/赏金记录/收藏/关注的达人/达人认证、退出登录

## 目录结构

- `api/` 接口封装（request、auth、post、answer、tag、bounty、expert、favorite、follow、user）
- `store/user.js` 登录态与用户信息
- `pages/` 页面
- `components/` 公共组件（post-card、answer-item、tag-picker）
- `static/` 静态资源
