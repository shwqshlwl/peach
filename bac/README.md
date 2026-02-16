# 桃醉心生 - 后端服务

基于 Spring Boot 3.2.0 的桃树认领平台后端服务。

## 快速启动

### 1. 配置数据库

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE peach_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据
mysql -u root -p peach_adoption < src/main/resources/sql/schema.sql
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: your_username
    password: your_password
```

### 3. 启动服务

```bash
mvn clean install
mvn spring-boot:run
```

服务将在 http://localhost:8080 启动

## 技术栈

- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Redis
- Spring Security + JWT
- Lombok
- Hutool

## 项目结构

```
src/main/java/com/peachtree/
├── PeachAdoptionApplication.java   # 启动类
├── config/                         # 配置类
├── controller/                     # 控制器
├── entity/                         # 实体类
├── mapper/                         # 数据访问层
├── service/                        # 业务逻辑层
└── common/                         # 通用类
```

## API文档

详见项目根目录 README.md
