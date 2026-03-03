# 北欧风格民宿管理系统 (Nordic Homestay Management System)

## 项目简介
这是一个基于 Spring Boot 和 Vue 3 开发的民宿管理系统，采用前后端分离架构。系统设计风格为北欧极简风（Nordic Minimalism），旨在提供温馨、舒适的用户体验。

项目包含三个主要模块：
1.  **客户端 (Client)**：供房客使用，支持房源浏览、预订、支付（模拟）、订单管理、收藏夹、个人中心等功能。
2.  **管理端 (Admin)**：供管理员使用，支持房源管理、订单管理、定价规则设置、数据看板等功能。
3.  **服务端 (Backend)**：提供 RESTful API 接口，处理业务逻辑和数据持久化。

## 技术栈

### 后端 (Backend)
-   **语言**: Java 17+
-   **框架**: Spring Boot 3
-   **数据库**: MySQL 8.0
-   **ORM**: Spring Data JPA
-   **安全**: Spring Security + JWT
-   **工具**: Lombok, Maven

### 前端 (Frontend)
-   **框架**: Vue 3 (Composition API)
-   **构建工具**: Vite
-   **状态管理**: Pinia
-   **路由**: Vue Router
-   **UI 组件库**:
    -   客户端: Vant UI 4
    -   管理端: Element Plus
-   **语言**: TypeScript

## 功能特性

### 客户端
-   **房源浏览**: 首页推荐、房源详情、图片轮播、设施展示。
-   **预订流程**: 日期选择、人数设置、订单确认、模拟支付。
-   **用户中心**: 登录/注册（支持房东/房客角色）、我的订单（状态流转）、收藏夹、个人信息修改。
-   **房东功能**: 房东可发布和编辑自己的房源，管理房源图片和设施。

### 管理端
-   **仪表盘**: 查看入住率、订单统计等关键指标。
-   **房源管理**: 房源的增删改查，上下架管理。
-   **订单管理**: 查看所有订单，办理入住/退房，取消订单。
-   **动态定价**: 设置周末、节假日等特殊日期的价格规则。

## 快速开始

### 1. 数据库准备
1.  创建数据库 `homestay_db`。
2.  导入 `homestay_schema.sql` 和 `init_data.sql` (如果有) 初始化表结构和数据。

### 2. 后端启动
1.  进入 `homestay-backend` 目录。
2.  配置 `application.yml` 中的数据库连接信息。
3.  运行 `HomestayApplication` 主类。

### 3. 前端启动
**客户端**:
```bash
cd homestay-client
npm install
npm run dev
```

**管理端**:
```bash
cd homestay-admin
npm install
npm run dev
```

## 目录结构
```
d:\XM\YYNB
├── homestay-admin/      # 管理端前端代码
├── homestay-backend/    # 后端 Java 代码
├── homestay-client/     # 客户端前端代码
├── homestay_schema.sql  # 数据库 Schema
└── README.md            # 项目说明文档
```
