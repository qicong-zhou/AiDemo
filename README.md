# AI 智能备忘录（AiMemo）

一个前后端不分离的个人备忘录系统。后端负责数据与 AI 能力，前端负责界面，构建后由 Spring Boot 一起托管，访问一个地址即可使用。

## 功能

- 备忘录的增删改查
- 新增/修改时由 DeepSeek 自动分析内容，生成分类标签（Tag）
- 按标签筛选、关键词搜索
- 左栏列表 + 右栏详情的双栏布局，支持编辑、删除、空状态、加载骨架屏

## 技术栈

**后端**
- Java 17 / Spring Boot 3
- MySQL 8.0 / MyBatis
- DeepSeek API（自动打标签）

**前端**
- Vue 3（`<script setup>` 组合式 API）
- Vite 构建

## 项目结构

```
AiDemo/
├── frontend/                       # Vue 前端源码
│   ├── src/
│   │   ├── App.vue                 # 页面全部逻辑、模板、样式
│   │   ├── main.js                 # 入口，把 App 挂到 #app
│   │   └── style.css               # 全局样式
│   ├── index.html
│   ├── vite.config.js              # 开发代理 + 构建输出目录
│   └── package.json
├── src/main/java/org/example/aidemo/
│   ├── controller/                 # 接口层
│   ├── service/                    # 业务层（调用 AI、组装数据）
│   ├── mapper/                     # MyBatis SQL
│   ├── entity/                     # 实体类
│   └── utils/AiUtils.java          # DeepSeek 调用封装
├── src/main/resources/
│   ├── static/                     # 前端构建产物（由 npm run build 生成，勿手改）
│   ├── application.properties      # 本地配置（不提交，含密码）
│   ├── application.properties.example
│   └── schema.sql                  # 建表语句
└── pom.xml
```

## 启动方式

### 1. 初始化数据库

在本地 MySQL 中执行 `schema.sql`，创建 `memo_db` 数据库及表结构。

### 2. 配置环境

复制 `src/main/resources/application.properties.example` 为 `application.properties`，填入你自己的数据库账号密码和 DeepSeek API Key。

### 3. 启动后端

在 IDEA 中运行 `AiDemoApplication` 的 `main` 方法。控制台出现 `Tomcat started on port 8080` 即启动成功。

打开 <http://localhost:8080/> 就能看到页面。

> `static/` 目录里已经包含构建好的前端文件，**只跑后端不需要安装 Node.js**。

## 前端开发（可选）

只有需要修改界面时才需要这一步。项目已提交构建产物，直接启动后端就能用。

```bash
cd frontend
npm install          # 首次运行，安装依赖
npm run dev          # 开发服务器，默认 http://localhost:5173
```

`vite.config.js` 里已配置代理，开发时 `/memos` 会自动转发到 `localhost:8080`，因此前后端要同时运行。

修改后的界面要同步到 `8080`，需重新构建：

```bash
npm run build        # 产物输出到 src/main/resources/static/
```

然后重启后端。

**注意：不要直接修改 `src/main/resources/static/` 下的文件**，每次 `npm run build` 都会清空重建，改动会丢失。要改就改 `frontend/src/App.vue`。

## 接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/memos` | 查询全部备忘录 |
| GET | `/memos/{id}` | 查询单条 |
| POST | `/memos` | 新增（自动生成标签） |
| PUT | `/memos/{id}` | 修改（重新生成标签） |
| DELETE | `/memos/{id}` | 删除 |
| GET | `/memos/search?keyword=` | 按标签搜索 |
| GET | `/memos/tags` | 标签及数量统计 |

## 接口测试

项目根目录附带了 `test.http`，在 IDEA 中打开，点击请求左侧的绿色三角即可发送请求，体验 AI 自动打标签。
