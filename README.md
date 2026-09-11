# 在线订餐系统

基于 Spring Boot + Vue 3 的在线订餐系统，支持用户注册登录、菜品浏览、购物车下单、余额充值，管理员可管理菜品和订单。

## 🛠 技术栈

- Frontend: Vue 3 + Vite + Element Plus + Pinia
- Backend: Java 24 + Spring Boot 3.4.1 + MyBatis
- Database: MySQL 8.0
- Auth: JWT + BCrypt

## 🚀 快速启动

1. 确保 Docker Desktop 已启动
2. 在项目根目录执行：
```bash
docker compose up --build
```
3. 等待容器启动完成（首次构建约 3-5 分钟）

## 🔗 服务地址

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:3367 |
| 后端 API | http://localhost:8367 |
| MySQL | localhost:33067 (root / root) |

## 🧪 测试账号

- **管理员**: admin / admin
- **普通用户**: 自行注册

## ✅ 功能验证

### 用户功能
1. 访问 http://localhost:3367 → 注册新用户 → 登录
2. 浏览菜品列表 → 选择数量 → 加入购物车
3. 进入购物车 → 结算（需先在个人中心充值余额）
4. 个人中心查看订单历史和余额

### 管理员功能
1. 使用 admin/admin 登录
2. 菜品管理：添加、修改、删除菜品
3. 订单管理：查看所有订单（未完成/已完成），标记订单完成

## 📁 项目结构

```
code-3367/
├── frontend/                    # Vue 3 前端
│   ├── src/
│   │   ├── api/                # API 接口封装
│   │   ├── views/              # 页面组件
│   │   ├── router/             # 路由配置
│   │   ├── store/              # Pinia 状态管理
│   │   └── main.js
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/com/restaurant/
│   │   ├── controller/         # 控制器层
│   │   ├── service/            # 业务逻辑层
│   │   ├── mapper/             # MyBatis Mapper
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   └── config/             # 配置类（JWT/Security/CORS）
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis XML
│   │   ├── application.yml
│   │   └── init.sql            # 数据库初始化脚本
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
└── README.md
```

## 📝 API 接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/users/register | 用户注册 | 公开 |
| POST | /api/users/login | 用户登录 | 公开 |
| GET | /api/users/info | 获取用户信息 | 登录 |
| POST | /api/users/recharge | 余额充值 | 登录 |
| GET | /api/dishes/list | 菜品列表 | 公开 |
| POST | /api/dishes/add | 添加菜品 | 管理员 |
| PUT | /api/dishes/{id} | 修改菜品 | 管理员 |
| DELETE | /api/dishes/{id} | 删除菜品 | 管理员 |
| POST | /api/orders/create | 创建订单 | 登录 |
| GET | /api/orders/my | 我的订单 | 登录 |
| GET | /api/orders/all | 所有订单 | 管理员 |
| GET | /api/orders/{id}/items | 订单详情 | 登录 |
| PUT | /api/orders/{id}/status | 更新订单状态 | 管理员 |

## 停止服务

```bash
docker compose down        # 停止服务
docker compose down -v     # 停止并清空数据
```
