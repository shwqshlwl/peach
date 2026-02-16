# 桃醉心生 - 前端应用

基于 Vue 3 + Vite 的桃树认领平台前端应用。

## 快速启动

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

应用将在 http://localhost:3000 启动

### 3. 构建生产版本

```bash
npm run build
```

## 技术栈

- Vue 3.4
- Vite 5.0
- Element Plus 2.5
- Pinia 2.1
- Vue Router 4.2
- Axios 1.6

## 项目结构

```
src/
├── api/            # API接口
├── assets/         # 静态资源
├── components/     # 组件
├── router/         # 路由配置
├── stores/         # 状态管理
├── styles/         # 样式文件
├── utils/          # 工具函数
├── views/          # 页面组件
├── App.vue         # 根组件
└── main.js         # 入口文件
```

## 页面说明

- `/` - 首页
- `/trees` - 桃树列表
- `/tree/:id` - 桃树详情
- `/my-tree` - 我的桃树
- `/community` - 树主社区
- `/profile` - 个人中心
- `/login` - 登录
- `/register` - 注册

## 开发说明

- 后端API地址配置在 `vite.config.js` 的 proxy 中
- 全局样式在 `src/styles/global.scss`
- 请求拦截器在 `src/utils/request.js`
