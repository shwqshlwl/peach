# 桃醉心生——认领一颗小桃树

一个完整的桃树认领平台，用户可以在线认领桃树，实时查看生长状态，参与互动养护，最终收获新鲜桃子。

## 项目特色

🌸 **沉浸式体验** - 360°全景展示果园，高清地图选择桃树  
📝 **生长日记** - 专业果农定期更新，记录桃树成长每一刻  
🎮 **互动养护** - 在线浇水、施肥、签到，获取积分奖励  
🎁 **会员权益** - 保底产量、免费配送、积分兑换等多重福利  
👥 **社区交流** - 树主专属社区，分享种植乐趣  

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.5
- **缓存**: Redis
- **安全**: Spring Security + JWT
- **工具**: Lombok, Hutool

### 前端
- **框架**: Vue 3.4
- **构建工具**: Vite 5.0
- **UI组件**: Element Plus 2.5
- **状态管理**: Pinia 2.1
- **路由**: Vue Router 4.2
- **HTTP客户端**: Axios 1.6

## 项目结构

```
T/
├── bac/                                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/peachtree/
│   │   │   │   ├── PeachAdoptionApplication.java
│   │   │   │   ├── config/             # 配置类
│   │   │   │   ├── controller/         # 控制器
│   │   │   │   ├── entity/             # 实体类
│   │   │   │   ├── mapper/             # MyBatis映射器
│   │   │   │   ├── service/            # 业务逻辑
│   │   │   │   └── common/             # 通用类
│   │   │   └── resources/
│   │   │       ├── application.yml     # 配置文件
│   │   │       └── sql/schema.sql      # 数据库脚本
│   │   └── pom.xml                     # Maven依赖
│   
└── fro/                                    # 前端项目
    ├── src/
    │   ├── api/                        # API接口
    │   ├── assets/                     # 静态资源
    │   ├── components/                 # 组件
    │   ├── router/                     # 路由配置
    │   ├── stores/                     # 状态管理
    │   ├── styles/                     # 样式文件
    │   ├── utils/                      # 工具函数
    │   ├── views/                      # 页面组件
    │   ├── App.vue                     # 根组件
    │   └── main.js                     # 入口文件
    ├── index.html
    ├── package.json
    └── vite.config.js
```

## 核心功能

### 前台功能

#### 1. 桃树认领
- 高清地图/360°全景展示果园
- 桃树信息展示（编号、品种、树龄、预计产量）
- 一键认领与在线支付（微信/支付宝）
- 认领权益说明

#### 2. 我的桃树
- 桃树专属主页
- 生长日记时间轴
- 互动养护（浇水、施肥、签到）
- 积分系统
- 自定义树牌
- 收货地址管理

#### 3. 会员与社区
- 会员等级与积分
- 树主社区（发帖、评论、点赞）
- 晒单分享

### 后台功能

#### 1. 桃树管理
- 桃树信息录入与编辑
- 状态管理（可认领/已认领/维护中）
- 位置坐标管理

#### 2. 订单管理
- 认领订单查看
- 支付状态管理
- 用户信息管理

#### 3. 内容管理
- 生长日记发布
- 图片/视频上传

#### 4. 配送管理
- 收货地址管理
- 物流信息录入

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- Node.js 16+

### 后端启动

1. **创建数据库**
```bash
mysql -u root -p
```

```sql
CREATE DATABASE peach_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **导入数据库脚本**
```bash
mysql -u root -p peach_adoption < bac/src/main/resources/sql/schema.sql
```

3. **修改配置文件**

编辑 `bac/src/main/resources/application.yml`，修改数据库和Redis连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/peach_adoption?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  
  redis:
    host: localhost
    port: 6379
```

4. **启动后端服务**
```bash
cd bac
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. **安装依赖**
```bash
cd fro
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

3. **构建生产版本**
```bash
npm run build
```

## API接口文档

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/user/{id}` - 获取用户信息

### 桃树接口
- `GET /api/peach-trees/list` - 获取桃树列表
- `GET /api/peach-trees/detail/{id}` - 获取桃树详情
- `POST /api/peach-trees/save` - 保存桃树（管理员）
- `DELETE /api/peach-trees/delete/{id}` - 删除桃树（管理员）

### 订单接口
- `POST /api/orders/create` - 创建认领订单
- `POST /api/orders/pay/{orderId}` - 支付订单
- `GET /api/orders/user/{userId}` - 获取用户订单列表
- `GET /api/orders/detail/{orderId}` - 获取订单详情

### 互动接口
- `POST /api/interaction/action` - 执行互动操作（浇水/施肥/签到）
- `GET /api/growth-diary/tree/{treeId}` - 获取生长日记

### 社区接口
- `POST /api/community/post` - 发布帖子
- `GET /api/community/posts` - 获取帖子列表
- `POST /api/community/like/{postId}` - 点赞帖子

### 地址接口
- `POST /api/address/add` - 添加收货地址
- `GET /api/address/user/{userId}` - 获取用户地址列表
- `PUT /api/address/update` - 更新地址
- `DELETE /api/address/delete/{id}` - 删除地址

## 默认账户

系统初始化后会创建以下测试账户：

**管理员账户**
- 用户名: `admin`
- 密码: `123456`

**普通用户**
- 用户名: `11`
- 密码: `123456`

## 数据库设计

### 核心表结构

- `user` - 用户表
- `peach_tree` - 桃树表
- `adoption_order` - 认领订单表
- `growth_diary` - 生长日记表
- `interaction_record` - 互动记录表
- `shipping_address` - 收货地址表
- `delivery_record` - 配送记录表
- `community_post` - 社区帖子表
- `comment` - 评论表
- `system_message` - 系统消息表

详细表结构请查看 `bac/src/main/resources/sql/schema.sql`

## 设计特色

### 视觉设计
- 采用粉色系主题，营造温馨甜蜜的氛围
- 使用中文书法字体"马善政楷"作为标题字体
- 流畅的动画效果和过渡
- 响应式设计，适配多种设备

### 用户体验
- 简洁直观的操作流程
- 实时反馈和提示
- 流畅的页面切换
- 丰富的互动元素

## 功能扩展建议

- [ ] 集成真实支付接口（微信支付、支付宝）
- [ ] 实现实时视频监控功能
- [ ] 添加小程序端
- [ ] 实现消息推送（短信、微信）
- [ ] 添加数据统计和报表
- [ ] 实现后台管理系统
- [ ] 添加优惠券和营销活动
- [ ] 实现好友推荐奖励机制

## 注意事项

1. **安全配置**: 生产环境请修改JWT密钥和数据库密码
2. **文件上传**: 需要配置阿里云OSS或其他对象存储服务
3. **支付接口**: 需要申请微信支付和支付宝商户号
4. **HTTPS**: 生产环境建议使用HTTPS协议

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎联系：
- 邮箱: contact@peachtree.com
- 电话: 400-123-4567

---

**桃醉心生** - 让每一颗桃树都有自己的故事 🍑
