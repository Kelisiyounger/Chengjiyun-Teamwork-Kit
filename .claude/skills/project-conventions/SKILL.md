---
name: project-conventions
description: |
  城际云项目约定与通用开发模式。当用户创建新项目、搭建项目结构、配置构建工具、
  或询问 Git 工作流、CI/CD 流程、代码审查流程时自动加载。
  触发关键词：项目结构、目录结构、Git 工作流、分支策略、commit 规范、
  CI/CD、构建配置、Gradle、pnpm、代码审查流程、PR 模板、项目初始化。
---

# 城际云项目约定 Skill

## 使用场景
- 初始化新项目，搭建标准目录结构
- 了解团队的 Git 分支策略和 Commit 规范
- 配置 Gradle / pnpm 构建脚本
- 了解代码审查 (Code Review) 流程
- CI/CD 流水线配置

## 快速索引

| 主题 | 参考文件 |
|------|----------|
| 项目目录结构规范 | 见下方 |
| Git 工作流与 Commit 规范 | [git-workflow.md](./references/git-workflow.md) |
| Java 构建配置 (Gradle) | 见下方 |
| 前端构建配置 (pnpm) | 见下方 |
| 代码审查流程 | 见下方 |

---

## 一、项目目录结构规范

```
<project-root>/
├── backend/                         # Spring Boot 后端
│   ├── src/main/java/com/chengjiyun/<module>/
│   │   ├── controller/              # 控制器层
│   │   ├── service/                 # 服务接口
│   │   │   └── impl/                # 服务实现
│   │   ├── mapper/                  # 数据访问层 (MyBatis Plus)
│   │   ├── entity/                  # 数据库实体
│   │   ├── dto/                     # 数据传输对象
│   │   ├── vo/                      # 视图对象
│   │   ├── request/                 # 请求参数对象
│   │   ├── constant/                # 常量定义
│   │   ├── enums/                   # 枚举类
│   │   ├── exception/               # 业务异常
│   │   ├── config/                  # Spring 配置
│   │   └── utils/                   # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml          # 主配置
│   │   ├── application-dev.yml      # 开发环境
│   │   ├── application-prod.yml     # 生产环境
│   │   └── db/migration/            # Flyway 迁移脚本
│   ├── src/test/java/               # 测试代码
│   ├── build.gradle                 # Gradle 构建脚本
│   └── settings.gradle
│
├── frontend/                        # React 前端
│   ├── src/
│   │   ├── components/              # 通用组件
│   │   ├── pages/                   # 路由页面
│   │   ├── hooks/                   # 自定义 Hooks
│   │   ├── services/                # API 调用层
│   │   ├── stores/                  # 状态管理 (Zustand)
│   │   ├── utils/                   # 工具函数
│   │   ├── types/                   # TypeScript 类型定义
│   │   └── styles/                  # 全局样式
│   ├── public/
│   ├── package.json
│   ├── tsconfig.json
│   ├── vite.config.ts
│   └── .eslintrc.cjs
│
├── docs/                            # 项目文档
│   ├── architecture.md              # 架构说明
│   ├── api-guide.md                 # API 文档
│   └── deployment.md                # 部署文档
│
├── .claude/                         # Claude Code 配置（团队共享）
│   ├── settings.json
│   ├── CLAUDE.md
│   ├── skills/
│   └── agents/
│
├── .github/                         # GitHub Actions / PR 模板
│   ├── workflows/
│   │   └── ci.yml
│   └── PULL_REQUEST_TEMPLATE.md
│
├── .gitignore
├── docker-compose.yml               # 本地开发环境
└── README.md
```

## 二、后端构建配置 (Gradle 8.x)

```groovy
// build.gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.0'
    id 'io.spring.dependency-management' version '1.1.5'
    id 'com.diffplug.spotless' version '6.25.0'  // 代码格式化
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

spotless {
    java {
        googleJavaFormat()        // Google Java Format
        formatAnnotations()       // 注解格式化
    }
}

// checkstyle / pmd 阿里巴巴规约检查（CI 中强制）
// 本地开发可选安装 p3c IDEA 插件
```

## 三、前端构建配置 (pnpm + Vite)

- 包管理器：**pnpm** 9.x（禁止 npm / yarn）
- 构建工具：Vite 5.x
- 格式化：Prettier + ESLint (`@alibaba/eslint-config-ali`)
- Commit 前：lint-staged 自动修复

## 四、代码审查流程

| 阶段 | 操作 | 负责人 |
|------|------|--------|
| 1. 自审 | 开发者先用 code-reviewer Agent 自查 | 开发者 |
| 2. 提交 | 创建 PR，填写模板，关联 Issue | 开发者 |
| 3. 初审 | 至少 1 位团队成员 Code Review | Reviewer |
| 4. CI | 通过 CI 流水线（构建 + 测试 + Lint） | 自动 |
| 5. 合并 | Squash Merge 到 main 分支 | Reviewer |

> 💡 Claude Code 的 `/code-reviewer` Agent 可以替代第 1 步自审，在提交前快速发现常见问题。

## 五、通用约定

- 技术选型优先使用团队已有技术栈，引入新技术需架构组评审
- 第三方依赖优先选择活跃维护（最近 6 个月有更新）、Star > 1K 的开源项目
- 所有环境变量和密钥通过 CI/CD Secrets 或 Vault 注入，禁止写入代码
- 数据库变更必须通过 Flyway Migration，禁止手动改库
- 接口变更需同步更新 API 文档
