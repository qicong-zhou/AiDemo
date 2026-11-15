# AI 智能备忘录后端系统

## 项目简介
本项目是一个基于 Java 后端开发的备忘录系统。核心功能提供备忘录的增删改查 API。此外，系统接入了 DeepSeek AI 大模型，在用户新增备忘录时，系统会自动分析内容并生成标签（Tag），实现智能标签/分类。

## 技术栈
- 开发语言：Java 17
- 后端框架：Spring Boot 3
- 数据库：MySQL 8.0
- ORM框架：MyBatis
- AI 模型：DeepSeek API

## 启动方式
1. 初始化数据库：在本地 MySQL 中执行项目根目录下的 `schema.sql` 文件，创建 `memo_db` 数据库及表结构。
2. 配置环境：找到项目中的 `application.properties.example` 文件，复制一份并重命名为 `application.properties`。打开文件，将数据库密码和 DeepSeek API 密钥替换为你自己的信息。
3. 运行项目：在 IDEA 中运行 `AiDemoApplication` 的 `main` 方法。当控制台输出 `Tomcat started on port 8080` 时，表示启动成功。

## 接口测试
项目根目录下附带了 `test.http` 文件。在 IDEA 中打开该文件，点击请求左侧的绿色三角即可直接发送测试请求，体验 AI 自动打标签功能。
