# WebAI聊天应用

## 项目简介
WebAI聊天应用是一个基于Vue3 + Vite + Element Plus + SpringBoot + SpringAI + MySQL + Redis的智能聊天平台，支持多会话管理、AI对话、消息持久化、模型灵活切换、token鉴权、公司/用户/模型管理等功能，适用于智能客服、办公助手等多场景。

## 技术架构
- 前端：Vue3、Vite、Element Plus、Echarts、Axios、dayjs、marked、nanoid、vue-router
- 后端：SpringBoot、SpringAI，集成OpenAI、Ollama等大模型
- 数据库：MySQL（结构化数据）、Redis（缓存会话上下文）

## 主要功能
### 用户端
- 注册、登录、token鉴权
- 创建/删除/查看会话，支持会话与HR（模型）关联
- AI智能聊天，消息内容持久化，支持多轮上下文
- 历史消息查看，个人信息管理

### 管理端
- 公司管理
- 用户管理
- HR（模型）管理
- token管理与系统配置

## 数据库核心表设计
- 用户表：id、用户名、密码、头像、角色、注册时间等
- 会话表：id、用户id、会话名、创建时间、关联hr模型id等
- 消息表：id、会话id、发送者、消息内容、消息类型、时间戳等
- HR（模型）表：id、模型名、描述、接口类型、参数配置等
- 公司表：id、公司名、描述等
- token表：id、用户id、token、过期时间等

## 安全与性能
- 前后端均校验token，保障接口安全
- 支持高并发，消息流式处理，Redis缓存提升性能
- 防止AI幻读，保证上下文一致性

## 运行与部署
1. 后端：
   - 进入springAIServe目录，配置数据库与Redis，运行SpringBoot服务
2. 前端：
   - 进入WebUI目录，npm install，npm run dev 启动前端
3. 访问：
   - 用户端、管理端均可通过浏览器访问

## 适用场景
- 智能客服
- 企业办公助手
- 多角色AI对话

## 未来展望
- 支持知识库问答、语音对话、多模型融合
- 持续优化性能与安全

---

如需详细开发文档、接口说明或数据库脚本，请联系项目维护者。
