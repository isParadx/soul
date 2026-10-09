# 心灵之旅（Soul）—— 学生心理咨询预约管理系统

> 一个面向高校学生的心理咨询预约管理系统，覆盖「咨询师浏览 → 预约下单 → 咨询管理 → 意见反馈」的完整业务闭环，支持学生 / 心理医生 / 管理员三种角色。

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](<https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen>)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5-42b883)](https://vuejs.org/)
[![uni-app](<https://img.shields.io/badge/uni--app%20x-App-2b9939>)](https://uniapp.dcloud.net.cn/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1)](https://www.mysql.com/)

「心灵之旅」（Soul）为大学生提供便捷的心理咨询服务预约渠道：学生通过移动端浏览并预约心理咨询师，医生在 Web 端处理预约与咨询记录，管理员在 Web 端统一管理用户、医生、预约与意见。项目名取自「用心的倾听，是一场心灵的旅行」。

## 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [安装与使用说明](#安装与使用说明)
- [使用示例](#使用示例)
- [数据库设计](#数据库设计)
- [接口文档](#接口文档)
- [贡献指南](#贡献指南)
- [许可证](#许可证)
- [联系信息和致谢](#联系信息和致谢)

## 功能特性

| 角色     | 端       | 功能                                                                                            |
| -------- | -------- | ----------------------------------------------------------------------------------------------- |
| 学生     | 移动 App | 注册 / 登录、浏览与搜索咨询师、医师详情、选择日期与时段预约、我的预约、修改资料与密码、意见反馈 |
| 心理医生 | Web      | 预约管理（查看学生预约、维护咨询反馈）、编辑个人信息与擅长领域标签                              |
| 管理员   | Web      | 用户管理（含密码重置、删除用户并级联清理数据）、医生管理、预约管理、意见管理                    |

**系统亮点：**

- **账号自动分配**：学生、医生账号由系统按规则生成，用户无需自行设计账号；支持「账号 / 手机号 / 邮箱」三种方式登录。
- **密码安全**：BCrypt 加密存储，密码需 6-20 位且同时包含字母和数字。
- **订单号规则化**：预约单号按 `yyyyMMdd + 4 位当日流水` 生成，共 12 位长数字，便于检索与对账。
- **数据一致性**：删除用户时在同一事务内级联清理其全部关联订单与医生档案，管理员账户受保护。
- **双端体验**：Web 管理端（Vue 3 + Element Plus）与移动端（uni-app x）统一的青绿卡片式 UI。

## 技术栈

### 后端

| 技术              | 版本                   | 说明                                                            |
| ----------------- | ---------------------- | --------------------------------------------------------------- |
| Java              | 21                     | 开发语言                                                        |
| Spring Boot       | 3.2.5                  | 应用框架                                                        |
| MyBatis-Plus      | 3.5.7                  | ORM 框架（mybatis-plus-spring-boot3-starter）                   |
| MySQL             | 8.0                    | 关系型数据库（驱动 mysql-connector-j 8.2.0）                    |
| Druid             | 1.2.23                 | 数据库连接池（druid-spring-boot-3-starter）                     |
| JWT (jjwt)        | 0.12.5                 | 身份认证，兼容`token` 与 `Authorization: Bearer` 两种请求头 |
| BCrypt            | spring-security-crypto | 密码加密存储                                                    |
| SpringDoc OpenAPI | 2.5.0                  | 在线接口文档                                                    |
| Lombok            | 1.18.42                | 代码简化                                                        |

### 前端 Web 端（管理端）

| 技术         | 版本 | 说明      |
| ------------ | ---- | --------- |
| Vue          | 3.5  | 前端框架  |
| TypeScript   | 5.6  | 类型安全  |
| Vite         | 5.4  | 构建工具  |
| Element Plus | 2.8  | UI 组件库 |
| Vue Router   | 4.4  | 路由管理  |
| Axios        | 1.7  | HTTP 请求 |

### 前端 App 端（移动端）

| 技术       | 说明                   |
| ---------- | ---------------------- |
| uni-app x  | 跨平台移动应用开发框架 |
| UTS / uvue | 原生渲染语言与页面格式 |

## 项目结构

```
soul/
├── back/                                  # 后端 Spring Boot 项目
│   └── src/main/
│       ├── java/com/paradx/soul/
│       │   ├── annotation/                # 自定义注解（@RequireRole 接口角色权限）
│       │   ├── config/                    # JWT / 角色拦截器、WebMvc 配置、全局异常处理
│       │   ├── filter/                    # XSS 过滤器
│       │   ├── controller/                # User / Doctor / Consult / Tag / Advice 控制器
│       │   ├── service/ + impl/           # 业务逻辑层
│       │   ├── mapper/                    # 数据访问层（MyBatis-Plus）
│       │   ├── pojo/ + vo/                # 实体类与请求对象
│       │   ├── utils/                     # JwtHelper / BCryptUtil / DeviceUtil / ValidationUtil / Result 等
│       │   └── SoulApplication.java       # 启动类
│       └── resources/
│           ├── application.yaml           # 应用配置
│           └── mapper/                    # MyBatis XML 映射文件
├── front/
│   ├── web/                               # Web 管理端（Vue 3 + Element Plus）
│   │   └── src/
│   │       ├── api/                       # Axios 封装（token 统一存 sessionStorage）
│   │       ├── components/
│   │       │   ├── Doctor/                # 医生端：预约管理、个人信息
│   │       │   ├── Master/                # 管理端：用户 / 医生 / 预约 / 意见管理
│   │       │   └── OutMain/               # 登录、注册、完善信息
│   │       ├── views/                     # 首页、医生工作台、管理后台
│   │       └── router/                    # 路由配置
│   └── app/                               # 移动端（uni-app x）
│       ├── pages/
│       │   ├── loAre/                     # 登录 / 注册
│       │   └── tabbar/                    # 首页、预约、个人中心（含详情 / 改密 / 反馈 / 资料）
│       └── static/                        # 图标与图片资源
└── soul.sql                               # 数据库初始化脚本（建库、建表、初始数据，一键导入）
```

### 文件与目录说明

| 文件 / 目录                         | 用途                                                                       |
| ----------------------------------- | -------------------------------------------------------------------------- |
| `back/`                           | 后端 Spring Boot 工程，对外提供 REST 接口（默认 8080 端口）                |
| `back/.../controller/`            | 接口层，负责参数接收与统一响应封装                                         |
| `back/.../service/`               | 业务逻辑层，承载账号分配、订单号生成、级联删除等核心规则                   |
| `back/.../mapper/`                | 数据访问层，MyBatis-Plus Mapper 与 XML 映射                                |
| `back/src/main/resources/mapper/` | MyBatis XML 映射文件                                                       |
| `front/web/`                      | Web 管理端（医生端 + 管理端），Vue 3 + Element Plus                        |
| `front/web/src/api/axios.ts`      | Axios 实例、token 注入与统一错误处理                                       |
| `front/app/`                      | 移动端（学生端），uni-app x                                                |
| `front/app/pages/`                | 页面：登录注册、首页、预约、个人中心等                                     |
| `soul.sql`                        | **唯一的数据库初始化脚本**，含建库、建表与初始数据，导入即完成初始化 |

## 安装与使用说明

### 环境要求

- JDK 21+
- MySQL 8.0+
- Maven 3.6+
- Node.js 18+（Vite 5，建议 20+）
- HBuilderX（运行移动端时需要）

### 1. 克隆仓库

```bash
git clone https://github.com/isParadx/soul.git
cd soul
```

### 2. 数据库初始化

```bash
# 创建数据库、建表并导入初始数据
mysql -u root -p < soul.sql
```

> `soul.sql` 已包含建库、建表（含账号规则、订单号规则等结构定义）与初始数据，导入即可完成初始化，**无需执行任何额外的迁移脚本**。

### 3. 后端启动

```bash
cd back

# 修改 src/main/resources/application.yaml 中的数据库连接信息：
#   spring.datasource.druid.url
#   spring.datasource.druid.username
#   spring.datasource.druid.password

# 启动后端服务
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`。

### 4. Web 管理端启动

```bash
cd front/web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 5. App 移动端启动

使用 HBuilderX 打开 `front/app` 目录，运行到模拟器或真机。

> 真机调试时，请将接口基地址中的 `localhost` 改为电脑的局域网 IP。

### 默认账号

数据库初始化后会生成以下测试账号：

| 角色     | 账号                                              | 密码         |
| -------- | ------------------------------------------------- | ------------ |
| 管理员   | `111111`                                        | `admin123` |
| 心理医生 | `200001` / `200002` / `200003` / `200004` | `YS123456` |
| 学生     | `2024000001`                                    | `XS123456` |

> 密码在数据库中以 BCrypt 加密存储，上表为明文初始密码，登录后建议及时修改。

## 使用示例

### 使用场景

| 使用场景       | 操作端   | 说明                                                                     |
| -------------- | -------- | ------------------------------------------------------------------------ |
| 学生预约咨询   | 移动 App | 首页选择咨询师 → 医师详情 → 选择日期与时段 → 提交预约，系统返回订单号 |
| 医生处理预约   | Web      | 登录后进入预约管理，查看学生预约并维护咨询反馈                           |
| 管理员维护数据 | Web      | 用户 / 医生 / 预约 / 意见四个管理模块；删除用户时自动级联清理其订单      |

### 接口调用示例（登录）

`account` 字段可填账号、手机号或邮箱；`X-Device-Type` 为可选的设备类型请求头（`pc` / `mobile`，不传则按 User-Agent 自动识别）。

```bash
curl -X POST "http://localhost:8080/user/login" \
  -H "Content-Type: application/json" \
  -H "X-Device-Type: pc" \
  -d '{"account":"111111","password":"admin123"}'
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 111111,
    "nickname": "admin",
    "role": "管理员",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "deviceType": "pc"
  }
}
```

登录成功后将返回的 `token` 放入请求头即可访问受保护接口（`token` 与 `Authorization: Bearer <token>` 两种写法均支持）：

```bash
curl "http://localhost:8080/user/getAllUserInfo?keywords=" \
  -H "token: <登录返回的 token>"
```

## 数据库设计

系统共 5 张表：

| 表名             | 说明                                                   |
| ---------------- | ------------------------------------------------------ |
| `user`         | 用户表（学生、医生、管理员共用，通过 role 字段区分）   |
| `doctor`       | 心理咨询师信息表（`id` 与 `user.userid` 保持一致） |
| `consultation` | 咨询预约记录表（`id` 即系统分配的订单号）            |
| `advice`       | 匿名意见建议表                                         |
| `tag`          | 医生擅长领域标签字典                                   |

### 用户角色说明

| role 值 | 角色     |
| ------- | -------- |
| 0       | 学生     |
| 1       | 心理医生 |
| 2       | 管理员   |

### 账号与密码规则

| 角色   | 账号（userid）规则         | 示例                           |
| ------ | -------------------------- | ------------------------------ |
| 学生   | 入学年份（4 位）+ 6 位序号 | `2024000001`、`2026000002` |
| 医生   | 2 + 5 位序号               | `200001`、`200002`         |
| 管理员 | 固定`111111`             | `111111`                     |

- **密码**：长度 6-20 位，必须同时包含字母和数字；数据库中一律以 **BCrypt** 加密存储。
- **手机号**必填且唯一，**邮箱**选填且唯一；登录时按输入内容自动识别为账号、手机号或邮箱。
- 账号 `userid` 由系统在注册时按上表规则分配，用户无需自行填写。

### 预约订单号规则

`consultation.id` 由系统在创建预约时按 **`yyyyMMdd`（8 位日期）+ 4 位当日流水** 分配，共 12 位长数字。例如 `202610090001` 表示 2026-10-09 的第 1 笔订单。订单号不依赖数据库自增，由后端生成并做并发冲突重试，前端展示即为订单号。

### 删除用户的数据一致性

管理员删除用户时，后端在**同一事务**内级联清理，避免残留脏数据：

1. 删除该用户作为学生提交的、以及作为医生接诊的**全部预约订单**；
2. 若为医生账号，同步删除其**医生档案**（避免首页出现「无账号医生」）；
3. 最后删除用户本身。

其中**管理员账户（role = 2）受保护，不允许通过该接口删除**。

## 接口文档

启动后端服务后，可访问以下地址查看在线接口文档：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- API Docs：`http://localhost:8080/v3/api-docs`

详细的接口清单与字段说明请参阅 [API接口文档.md](./API接口文档.md)。

### 统一返回格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 状态码 | 说明           |
| ------ | -------------- |
| 200    | 操作成功       |
| 501    | 用户名错误     |
| 503    | 密码错误       |
| 504    | 未登录         |
| 505    | 用户名已被占用 |

## 贡献指南

欢迎为本项目提交改进，无论是缺陷修复、功能建议还是文档完善。

**参与方式：**

1. **了解项目**：阅读本 README 与 [API接口文档.md](./API接口文档.md)，了解整体架构与设计约定。
2. **查找可贡献点**：查看 Issues 与待办事项，或直接提出你的想法。
3. **提交代码**：`Fork` 本仓库 → 新建分支开发并自测 → 提交 `Pull Request`。
4. **提交问题**：使用清晰、具体的标题描述问题，并附上复现步骤、预期行为与实际行为。

**代码规范约定：**

- 后端遵循现有分层结构（controller / service / mapper / pojo），保持统一返回体与异常处理。
- 数据库结构变更请**同步更新 `soul.sql`**（本项目以 `soul.sql` 作为唯一的库结构来源）。
- 前端样式沿用现有青绿卡片式风格，移动端样式使用单类选择器以兼容 uvue。
- 提交的代码应可正常编译 / 构建（后端 `mvn compile`、Web `npm run build`）。

## 许可证

本项目为个人毕业设计作品，用于学习与交流。默认遵循 **MIT License**，你可以自由使用、修改与分发，但需保留原始版权声明；如需正式开源，可在仓库根目录添加 `LICENSE` 文件。

## 联系信息和致谢

**作者**：isParadx

- GitHub：[@isParadx](https://github.com/isParadx)
- 邮箱：1956293215@qq.com
- 项目地址：https://github.com/isParadx/soul

如有问题、建议或合作意向，欢迎通过 Issues 或邮件与我联系。
