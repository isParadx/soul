# 心灵之旅 - 学生心理咨询预约管理系统

## 项目简介

"心灵之旅"（Soul）是一个面向高校学生的心理咨询预约管理系统，旨在为大学生提供便捷的心理咨询服务预约渠道。系统支持三种用户角色：**学生**、**心理医生**和**管理员**，实现了从咨询师浏览、预约下单、咨询管理到意见反馈的完整业务闭环。

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 2.6.2 | 应用框架 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Druid | 1.2.20 | 数据库连接池 |
| JWT (jjwt) | 0.9.1 | 身份认证 |
| SpringDoc OpenAPI | 1.6.14 | 接口文档 |
| Lombok | 1.18.26 | 代码简化 |

### 前端 Web 端（管理端）

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5 | 前端框架 |
| TypeScript | 5.6 | 类型安全 |
| Vite | 5.4 | 构建工具 |
| Element Plus | 2.8 | UI 组件库 |
| Vue Router | 4.4 | 路由管理 |
| Axios | 1.7 | HTTP 请求 |

### 前端 App 端（移动端）

| 技术 | 说明 |
|------|------|
| uni-app x | 跨平台移动应用开发框架 |
| UTS | uni-app 原生开发语言 |

## 项目结构

```
soul/
├── back/                          # 后端 Spring Boot 项目
│   ├── src/main/java/com/paradx/soul/
│   │   ├── controller/            # 控制器层
│   │   │   ├── UserController.java
│   │   │   ├── DoctorController.java
│   │   │   ├── ConsultController.java
│   │   │   └── adviceController.java
│   │   ├── service/               # 服务层
│   │   │   ├── UserService.java
│   │   │   ├── DoctorService.java
│   │   │   ├── ConsultationService.java
│   │   │   ├── AdviceService.java
│   │   │   └── impl/              # 服务实现
│   │   ├── mapper/                # 数据访问层
│   │   ├── pojo/                  # 实体类
│   │   │   ├── User.java
│   │   │   ├── Doctor.java
│   │   │   ├── Consultation.java
│   │   │   ├── Advice.java
│   │   │   └── vo/                # 视图对象
│   │   ├── utils/                 # 工具类
│   │   │   ├── JwtHelper.java     # JWT 工具
│   │   │   ├── MD5Util.java       # MD5 加密
│   │   │   ├── Result.java        # 统一返回结果
│   │   │   ├── ResultCodeEnum.java
│   │   │   └── SpringDocConfig.java
│   │   └── SoulApplication.java   # 启动类
│   ├── src/main/resources/
│   │   ├── application.yaml       # 应用配置
│   │   └── mapper/                # MyBatis XML 映射文件
│   └── pom.xml
├── front/
│   ├── web/                       # Web 管理端（Vue 3）
│   │   ├── src/
│   │   │   ├── api/               # API 请求封装
│   │   │   ├── components/        # 组件
│   │   │   │   ├── CozeChat/      # AI 聊天组件
│   │   │   │   ├── Doctor/        # 医生相关组件
│   │   │   │   ├── Master/        # 管理员组件
│   │   │   │   └── OutMain/       # 登录注册组件
│   │   │   ├── views/             # 页面视图
│   │   │   ├── router/            # 路由配置
│   │   │   └── App.vue
│   │   └── package.json
│   └── app/                       # 移动端（uni-app x）
│       ├── pages/
│       │   ├── loAre/             # 登录注册
│       │   ├── tabbar/            # 底部导航页
│       │   │   ├── Home/          # 首页（咨询师列表）
│       │   │   ├── Order/         # 咨询预约
│       │   │   └── Myself/        # 个人中心
│       │   └── CozeChat/          # AI 聊天
│       └── manifest.json
└── soul.sql                       # 数据库初始化脚本
```

## 功能模块

### 学生端（App）

- **用户注册/登录**：支持账号密码注册登录，密码 MD5 加密存储
- **首页浏览**：查看心理咨询师列表，支持关键词搜索
- **医师详情**：查看咨询师详细信息（简介、擅长领域、寄语）
- **咨询预约**：选择咨询师进行预约，选择时间段
- **个人中心**：查看/修改个人信息、修改密码
- **意见反馈**：匿名提交意见建议
- **AI 聊天**：集成 Coze AI 聊天功能

### 医生端（Web）

- **预约管理**：查看学生的咨询预约记录
- **个人信息**：查看和编辑个人资料

### 管理员端（Web）

- **用户管理**：查看所有用户信息、搜索用户、重置密码、删除用户
- **医生管理**：添加/编辑/删除心理咨询师信息
- **预约管理**：查看所有预约记录、修改预约状态、删除预约
- **意见管理**：查看学生提交的匿名意见建议

## 数据库设计

系统包含 4 张核心数据表：

| 表名 | 说明 |
|------|------|
| `user` | 用户表（学生、医生、管理员共用，通过 role 字段区分） |
| `doctor` | 心理咨询师信息表 |
| `consultation` | 咨询预约记录表 |
| `advice` | 匿名意见建议表 |

### 用户角色说明

| role 值 | 角色 |
|---------|------|
| 0 | 学生 |
| 1 | 心理医生 |
| 2 | 管理员 |

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+
- Node.js 18+

### 1. 数据库初始化

```bash
# 创建数据库并导入数据
mysql -u root -p < soul.sql
```

### 2. 后端启动

```bash
cd back

# 修改 src/main/resources/application.yaml 中的数据库连接信息
# spring.datasource.druid.url
# spring.datasource.druid.username
# spring.datasource.druid.password

# 启动后端服务
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 3. Web 管理端启动

```bash
cd front/web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 4. App 移动端启动

使用 HBuilderX 打开 `front/app` 目录，运行到模拟器或真机。

## 接口文档

启动后端服务后，访问以下地址查看在线接口文档：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- API Docs：`http://localhost:8080/v3/api-docs`

详细的接口文档请参阅 [API接口文档.md](./API接口文档.md)。

## 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin (ID: 111111) | 123456 |
| 学生 | paradx (ID: 2233320103) | 123456 |
| 医生 | 李楠 (ID: 101) | 123456 |
| 医生 | 江心 (ID: 102) | 123456 |

> 密码在数据库中以 MD5 加密存储，默认密码 `123456` 的 MD5 值为 `e10adc3949ba59abbe56e057f20f883e`

## 统一返回格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 501 | 用户名错误 |
| 503 | 密码错误 |
| 504 | 未登录 |
| 505 | 用户名已被占用 |
