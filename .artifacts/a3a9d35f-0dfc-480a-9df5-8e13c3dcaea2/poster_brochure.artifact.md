# 🌌 SOUL Planet — 项目海报与产品介绍书 (Poster & Brochure)

> **项目名称**：SOUL Planet (Connect Souls)
> **应用类型**：全球化智能技能交换与社交移动应用 (Android)
> **核心技术**：Kotlin, Jetpack Compose, Material 3, Google Gemini AI SDK, Spring Boot, MySQL

---

## 🌟 一、 产品愿景与定位 (Vision & Positioning)

**SOUL Planet** 是一款面向全球终身学习者与跨文化交流者的创新型移动社交应用。
我们致力于打破传统社交软件的单调列表形式，通过**3D 宇宙星球视觉漫游**与**前沿生成式 AI 技术（Google Gemini）**，将全球拥有不同语言和专业技能的“Soulers”连接在一起，实现高效、趣味、智能的双向技能互补交换（Teach & Learn）。

---

## 🚀 二、 核心功能亮点 (Core Features)

```carousel
### 🪐 1. 3D 宇宙星球与智能匹配
- **沉浸式探索**：摆脱传统枯燥的上下滚动列表，采用 3D 空间坐标系与 Canvas 动画展现全球在线伙伴。
- **技能互补匹配**：基于用户的“可教授技能 (Teach)”与“想学习技能 (Learn)”进行双向智能匹配，一键生成五维能力雷达图。

<!-- slide -->

### 🤖 2. Gemini AI 智能客服与伴侣
- **全天候智能答疑**：深度集成 Google Gemini 大模型（Gemini-3.1-flash-lite），提供秒级、高质量的智能回复。
- **多语言与 FAQ 抽屉**：支持中、韩、英多语言无缝切换，内置结构化常见问题知识库（Bottom Sheet），提供极致的用户体验。

<!-- slide -->

### 📝 3. AI 智能测验大师 (AI 퀴즈)
- **实时动态出题**：输入任意学习主题（如 Python、英语会话等），AI 在云端实时生成高质量 4 选项客观测试题。
- **互动答题与批改**：高定 M3 卡片式单选交互、实时答题进度条，提交后立即给出 AI 自动化评分与专属总评。

<!-- slide -->

### 💬 4. 1:1 实时聊天与后端持久化
- **即时通讯**：支持与匹配伙伴、客服进行 1:1 流式消息收发。
- **无缝数据同步**：前端完美对接 Spring Boot 后端与 MySQL 数据库（`chat_room` 与 `chat_message` 表），确保移动端与网页端消息实时互通。
```

---

## 🛠️ 三、 技术架构与创新点 (Technical Architecture)

| 模块层级 | 技术选型与实现细节 |
| :--- | :--- |
| **UI 界面层** | **Jetpack Compose + Material 3**，遵循现代声明式 UI 设计，支持深色星空主题与毛玻璃特效。 |
| **状态与协程** | **Kotlin Coroutines + Flow**，实现高效的异步数据流管理与无阻塞 UI 更新。 |
| **AI 引擎层** | **Google AI Client SDK**，结合云端大模型与本地智能降级兜底机制，保障 100% 高可用。 |
| **网络与通信** | **Retrofit + OkHttp**（RESTful API）与 Spring Boot 后端、MySQL 数据库深度集成。 |
| **数据持久化** | **SharedPreferences** 实现安全的登录状态持久화与“记住账号密码”功能。 |

---

## 📈 四、 应用场景与价值 (Value Proposition)

1. **跨文化语言学习**：母语者与学习者结对互助，告别枯燥的死记硬背。
2. **专业技能交换**：程序员与设计师、音乐人与摄影师之间的知识破壁与协作。
3. **AI 赋能教育**：随时随地通过 AI 智能客服与自定义测验检验学习成果。

---
*SOUL Planet — Connect Souls, Explore Knowledge.*
