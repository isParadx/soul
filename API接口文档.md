# 心灵之旅 - 后端 API 接口文档

## 基本信息

- **Base URL**：`http://localhost:8080`
- **认证方式**：JWT Token（通过请求头 `token` 传递）
- **Content-Type**：`application/json`
- **在线文档**：启动后端后访问 `http://localhost:8080/swagger-ui.html`

---

## 统一响应格式

所有接口返回统一的 JSON 结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 响应状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 501 | 用户名错误 |
| 503 | 密码错误 |
| 504 | 未登录 |
| 505 | 用户名已被占用 |

---

## 一、用户模块（User）

### 1.1 用户注册

**接口地址**：`POST /user/regist`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userid | Long | 是 | 用户ID（学号/工号） |
| nickname | String | 是 | 昵称 |
| sex | Integer | 是 | 性别（0-女，1-男） |
| password | String | 是 | 密码（明文，后端自动 MD5 加密） |
| phone | Long | 否 | 手机号 |
| email | String | 否 | 邮箱 |
| role | Integer | 是 | 角色（0-学生，1-医生，2-管理员） |

**请求示例**：

```json
{
  "userid": 2233320105,
  "nickname": "张三",
  "sex": 1,
  "password": "123456",
  "phone": 13800138000,
  "email": "zhangsan@qq.com",
  "role": 0
}
```

**响应示例**：

```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": null
}

// 用户名已被占用
{
  "code": 505,
  "message": "userNameUsed",
  "data": null
}
```

---

### 1.2 用户登录

**接口地址**：`POST /user/login`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userid | Long | 是 | 用户ID（学号/工号） |
| password | String | 是 | 密码（明文） |

**请求示例**：

```json
{
  "userid": 2233320103,
  "password": "123456"
}
```

**响应示例**：

```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": "eyJhbGciOiJIUzUxMiJ9..."
}
```

> `data` 字段返回 JWT Token，后续需要鉴权的接口需在请求头中携带此 Token。

---

### 1.3 获取当前登录用户信息

**接口地址**：`GET /user/getUserInfo`

**请求头**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | 登录时获取的 JWT Token |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userid": 2233320103,
    "nickname": "paradx",
    "sex": 0,
    "password": "e10adc3949ba59abbe56e057f20f883e",
    "phone": 15867253472,
    "email": "12312321@qq.com",
    "img": null,
    "role": 0
  }
}
```

---

### 1.4 获取所有用户信息

**接口地址**：`GET /user/getAllUserInfo`

**请求参数**（Query）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keywords | String | 否 | 搜索关键词（模糊匹配昵称） |

**请求示例**：

```
GET /user/getAllUserInfo?keywords=para
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "userid": 2233320103,
      "nickname": "paradx",
      "sex": 0,
      "password": "e10adc3949ba59abbe56e057f20f883e",
      "phone": 15867253472,
      "email": "12312321@qq.com",
      "img": null,
      "role": 0
    }
  ]
}
```

---

### 1.5 修改用户信息

**接口地址**：`POST /user/changeUserInfo`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userid | Long | 是 | 用户ID |
| nickname | String | 否 | 昵称 |
| sex | Integer | 否 | 性别 |
| phone | Long | 否 | 手机号 |
| email | String | 否 | 邮箱 |
| img | byte[] | 否 | 头像（Base64 编码） |

**请求示例**：

```json
{
  "userid": 2233320103,
  "nickname": "新昵称",
  "sex": 1,
  "phone": 13900139000,
  "email": "newemail@qq.com"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 1.6 修改用户密码

**接口地址**：`POST /user/changePassword`

**请求头**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | JWT Token |

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| oldPassword | String | 是 | 原密码（明文） |
| newPassword | String | 是 | 新密码（明文） |

**请求示例**：

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

**响应示例**：

```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": null
}

// 原密码错误
{
  "code": 503,
  "message": "passwordError",
  "data": null
}
```

---

### 1.7 重置用户密码（管理员）

**接口地址**：`POST /user/ResetPassword`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| (Body) | Long | 是 | 目标用户ID |

**请求示例**：

```json
2233320103
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

> 将目标用户密码重置为默认密码 `123456`。

---

### 1.8 删除用户（管理员）

**接口地址**：`POST /user/delUser`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| (Body) | Long | 是 | 目标用户ID |

**请求示例**：

```json
2233320105
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 二、医生模块（Doctor）

### 2.1 查询所有医生 / 搜索医生

**接口地址**：`GET /doctor/getAllDocter`

**请求参数**（Query）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keywords | String | 否 | 搜索关键词（模糊匹配医生姓名、类型等） |

**请求示例**：

```
GET /doctor/getAllDocter?keywords=李
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 101,
      "name": "李楠",
      "intruduce": "我是一名资深的心理医生",
      "type": "情感,生活",
      "say": "我是你的树洞"
    },
    {
      "id": 102,
      "name": "江心",
      "intruduce": "我有着丰富的心理医生资历",
      "type": "情感,抑郁",
      "say": "我是一个倾听者"
    }
  ]
}
```

---

### 2.2 添加医生信息（管理员）

**接口地址**：`POST /doctor/addDoctorInfo`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 医生ID |
| name | String | 是 | 医生姓名 |
| intruduce | String | 是 | 医生简介 |
| type | String | 否 | 擅长领域 |
| say | String | 否 | 医生寄语 |

**请求示例**：

```json
{
  "id": 105,
  "name": "王医生",
  "intruduce": "国家二级心理咨询师，从业10年",
  "type": "情感,焦虑,抑郁",
  "say": "愿你被世界温柔以待"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 2.3 修改医生信息（管理员）

**接口地址**：`POST /doctor/changeDoc`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 医生ID |
| name | String | 否 | 医生姓名 |
| intruduce | String | 否 | 医生简介 |
| type | String | 否 | 擅长领域 |
| say | String | 否 | 医生寄语 |

**请求示例**：

```json
{
  "id": 101,
  "name": "李楠",
  "intruduce": "资深心理医生，擅长情感咨询",
  "type": "情感,生活,焦虑",
  "say": "我是你的树洞，随时倾听"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 2.4 删除医生（管理员）

**接口地址**：`POST /doctor/delDocter`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| (Body) | Long | 是 | 目标医生ID |

**请求示例**：

```json
105
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 三、预约管理模块（Consultation）

### 3.1 查询预约记录

**接口地址**：`GET /consult/checkOrder`

**请求参数**（Query）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keywords | String | 否 | 搜索关键词（模糊匹配） |

**请求示例**：

```
GET /consult/checkOrder?keywords=李
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 6,
      "docId": 101,
      "stuId": 2233320103,
      "appointmentTime": "2024-11.27/13:00-14:00",
      "status": "已结束",
      "feedback": "您的状况良好"
    },
    {
      "id": 13,
      "docId": 102,
      "stuId": 2233320103,
      "appointmentTime": "2024-12.06/9:00-10:00",
      "status": "未开始",
      "feedback": null
    }
  ]
}
```

---

### 3.2 添加预约

**接口地址**：`POST /consult/setOrder`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| docId | Long | 是 | 医生ID |
| stuId | Long | 是 | 学生ID |
| appointmentTime | String | 是 | 预约时间（格式：`yyyy-MM.dd/HH:mm-HH:mm`） |
| status | String | 否 | 预约状态（默认"未开始"） |
| feedback | String | 否 | 反馈信息 |

**请求示例**：

```json
{
  "docId": 101,
  "stuId": 2233320103,
  "appointmentTime": "2024-12.10/14:00-15:00"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 3.3 删除预约

**接口地址**：`POST /consult/delOrder`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| (Body) | Integer | 是 | 预约记录ID |

**请求示例**：

```json
13
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 3.4 修改预约信息

**接口地址**：`POST /consult/changeInfo`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Integer | 是 | 预约记录ID |
| docId | Long | 否 | 医生ID |
| stuId | Long | 否 | 学生ID |
| appointmentTime | String | 否 | 预约时间 |
| status | String | 否 | 预约状态 |
| feedback | String | 否 | 反馈信息 |

**请求示例**：

```json
{
  "id": 13,
  "status": "已结束",
  "feedback": "咨询效果良好，建议继续跟进"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 四、意见建议模块（Advice）

### 4.1 提交匿名意见

**接口地址**：`POST /advice/putAdvice`

**请求参数**（JSON Body）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| (Body) | String | 是 | 意见内容（纯文本） |

**请求示例**：

```json
"希望增加更多的心理咨询师选择"
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 4.2 查询所有意见（管理员）

**接口地址**：`GET /advice/getAdvice`

**请求参数**：无

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "context": "第一条测试意见",
      "time": "2024-11-25T07:46:21"
    },
    {
      "id": 2,
      "context": "第二条测试意见",
      "time": "2024-11-25T07:50:11"
    }
  ]
}
```

---

## 数据模型

### User（用户）

| 字段 | 类型 | 说明 |
|------|------|------|
| userid | Long | 用户ID（主键，学号/工号） |
| nickname | String | 昵称 |
| sex | Integer | 性别（0-女，1-男） |
| password | String | 密码（MD5 加密） |
| phone | Long | 手机号 |
| email | String | 邮箱 |
| img | byte[] | 头像图片 |
| role | Integer | 角色（0-学生，1-医生，2-管理员） |

### Doctor（心理咨询师）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 医生ID（主键） |
| name | String | 姓名 |
| intruduce | String | 简介 |
| type | String | 擅长领域 |
| say | String | 医生寄语 |

### Consultation（咨询预约）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 预约单号（主键，自增） |
| docId | Long | 医生ID |
| stuId | Long | 学生ID |
| appointmentTime | String | 预约时间 |
| status | String | 预约状态（默认"未开始"） |
| feedback | String | 反馈信息 |

### Advice（匿名意见）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 意见序号（主键，自增） |
| context | String | 意见内容 |
| time | Date | 提交时间 |

---

## 接口总览

| 模块 | 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|------|
| 用户 | 注册 | POST | /user/regist | 新用户注册 |
| 用户 | 登录 | POST | /user/login | 用户登录获取 Token |
| 用户 | 获取当前用户信息 | GET | /user/getUserInfo | 根据 Token 获取用户信息 |
| 用户 | 获取所有用户 | GET | /user/getAllUserInfo | 查询用户列表（支持搜索） |
| 用户 | 修改用户信息 | POST | /user/changeUserInfo | 修改个人信息 |
| 用户 | 修改密码 | POST | /user/changePassword | 修改当前用户密码 |
| 用户 | 重置密码 | POST | /user/ResetPassword | 管理员重置用户密码 |
| 用户 | 删除用户 | POST | /user/delUser | 管理员删除用户 |
| 医生 | 查询医生列表 | GET | /doctor/getAllDocter | 查询所有医生（支持搜索） |
| 医生 | 添加医生 | POST | /doctor/addDoctorInfo | 管理员添加医生 |
| 医生 | 修改医生 | POST | /doctor/changeDoc | 管理员修改医生信息 |
| 医生 | 删除医生 | POST | /doctor/delDocter | 管理员删除医生 |
| 预约 | 查询预约 | GET | /consult/checkOrder | 查询预约记录（支持搜索） |
| 预约 | 添加预约 | POST | /consult/setOrder | 学生提交预约 |
| 预约 | 删除预约 | POST | /consult/delOrder | 删除预约记录 |
| 预约 | 修改预约 | POST | /consult/changeInfo | 修改预约信息 |
| 意见 | 提交意见 | POST | /advice/putAdvice | 匿名提交意见建议 |
| 意见 | 查询意见 | GET | /advice/getAdvice | 管理员查看所有意见 |
