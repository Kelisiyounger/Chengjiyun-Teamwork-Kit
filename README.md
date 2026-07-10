# 城际云 — 团队 AI 开发基础配置

> **版本**: 1.0.0 | **分发方式**: Git 仓库 → `git clone` 即用  
> **维护**: 城际云技术架构组

---

## 这是什么？

一套**团队 AI 开发基础配置**——技术负责人在项目启动前将代码规范、项目约定、审查标准、自动化 Hooks、斜杠命令统一部署到项目 Git 仓库的 `.claude/` 目录中。团队成员 `git clone` 后 Claude Code 自动加载，无需任何安装操作。

### 使用模式

```
技术负责人                         团队成员
──────────                        ──────────
项目启动前 部署配置                   git clone 项目
  │                                  │
  ├─ CLAUDE.md (团队规范)             ├─ 代码自动遵循规范
  ├─ .claude/settings.json (Hooks)    ├─ 保存时自动格式化
  ├─ .claude/commands/ (斜杠命令)     ├─ /project:code-review 审查
  ├─ .claude/rules/ (模块化规则)      ├─ 提交时自动 lint
  ├─ .claude/skills/ (智能工作流)     └─ 不需要任何额外操作
  └─ git commit & push
     │
  规范更新时                            │
     │                               git pull
     修改配置 → git push  ─────────→   自动同步最新规范
```

---

## 目录结构

```
your-project/
├── CLAUDE.md                    ← 团队共享指令（提交到 Git）
├── CLAUDE.local.md              ← 个人覆盖（Git 忽略）
├── .gitignore                   ← 忽略个人文件
│
├── .claude/
│   ├── settings.json            ← 权限 + Hooks 配置（提交到 Git）
│   ├── settings.local.json      ← 个人权限覆盖（Git 忽略）
│   │
│   ├── commands/                ← 自定义斜杠命令
│   │   ├── code-review.md       →  /project:code-review
│   │   ├── fix-issue.md         →  /project:fix-issue
│   │   └── deploy.md            →  /project:deploy
│   │
│   ├── rules/                   ← 模块化指令（全局生效）
│   │   ├── code-style.md        ← Java/TS/SQL 规范
│   │   ├── testing.md           ← 测试策略
│   │   └── api-conventions.md   ← API 设计约定
│   │
│   ├── skills/                  ← 自动调用的工作流
│   │   ├── code-standards/      ← 代码规范 Skill
│   │   ├── project-conventions/ ← 项目约定 Skill
│   │   └── team-config-sync/    ← 配置同步 Skill
│   │
│   └── agents/                  ← 子代理角色定义
│       ├── code-reviewer.md     ← 代码审查代理
│       └── security-auditor.md  ← 安全审计代理
│
├── scripts/                     ← 工具脚本
│   ├── sync-claude-md.sh        ← 全局 CLAUDE.md 同步
│   └── check-version.sh         ← 版本检查
│
└── README.md
```

---

## 技术负责人：部署到项目

### 新项目部署

```bash
# 1. 克隆本配置仓库
git clone https://github.com/Kelisiyounger/Team-plugin.git framework
cd framework

# 2. 按需调整配置内容
#    编辑 CLAUDE.md — 修改技术栈、规范细节
#    编辑 .claude/settings.json — 调整权限和 Hooks
#    编辑 .claude/rules/ — 增删模块化规则

# 3. 部署到你的项目仓库
cp -r .claude/ /path/to/your-project/
cp CLAUDE.md CLAUDE.local.md .gitignore /path/to/your-project/
cd /path/to/your-project
git add .claude/ CLAUDE.md CLAUDE.local.md .gitignore
git commit -m "feat: 集成城际云团队 AI 开发基础配置 v1.0.0"
git push
```

### 已有项目集成

```bash
# 直接将 .claude/ 和 CLAUDE.md 复制到项目根目录即可
cp -r framework/.claude your-existing-project/
cp framework/CLAUDE.md your-existing-project/
cp framework/CLAUDE.local.md your-existing-project/
cat framework/.gitignore >> your-existing-project/.gitignore
cd your-existing-project
git add .claude/ CLAUDE.md CLAUDE.local.md .gitignore
git commit -m "feat: 集成团队 AI 开发基础配置"
git push
```

### 更新配置

技术负责人修改配置内容后：

```bash
# 在项目仓库中直接编辑
cd your-project
# 编辑 CLAUDE.md / .claude/rules/ / .claude/settings.json 等

git add .claude/ CLAUDE.md
git commit -m "chore: 更新团队 AI 开发配置 — 调整 xxx 规范"
git push
```

团队成员 `git pull` 后自动同步最新配置，无需额外操作。

---

## 团队成员：使用方式

```bash
# 1. 克隆项目（唯一操作）
git clone <项目地址>
cd <项目目录>

# 2. （可选）创建个人覆盖
# 编辑 CLAUDE.local.md，添加你的个人偏好
# 此文件不会被提交到 Git

# 3. 正常使用 Claude Code
claude
```

**就这么简单**。Claude Code 自动读取项目中的配置，写代码时：

- 自动遵循阿里巴巴 Java/TS/SQL 规范
- 保存时自动格式化（Spotless / Prettier / ESLint）
- 提交前自动 lint-staged
- `/project:code-review` 一键代码审查
- `/project:deploy` 部署前检查清单

---

## 核心功能

### 1. CLAUDE.md — 团队 AI 行为指令

团队统一的 AI 指令（安全规则、技术栈、编码规范、工作流），提交到 Git 后全团队生效。个人差异通过 `CLAUDE.local.md` 覆盖。

### 2. .claude/rules/ — 模块化规则

- `code-style.md` — Java/TS/SQL 代码风格
- `testing.md` — 测试策略与覆盖要求
- `api-conventions.md` — RESTful API 设计约定

Claude Code 自动加载所有 `.md` 文件。技术负责人可按领域增删。

### 3. .claude/commands/ — 自定义斜杠命令

| 命令 | 作用 |
|------|------|
| `/project:code-review` | 按阿里巴巴规范审查代码 |
| `/project:fix-issue` | 根据 Issue 定位问题并生成修复 |
| `/project:deploy` | 部署前完整检查清单 |

### 4. .claude/skills/ — 智能工作流

3 个 Skill 按需自动加载：`code-standards` / `project-conventions` / `team-config-sync`

### 5. .claude/agents/ — 专用审查代理

`code-reviewer`（5 维审查）+ `security-auditor`（安全专项审计）

### 6. Hooks — 自动化触发

| 事件 | 动作 |
|------|------|
| 写入 `*.java` | Spotless 自动格式化 |
| 写入 `*.tsx?` | Prettier + ESLint |
| 写入 `*.json/md/yaml` | Prettier |
| `git commit` | lint-staged |
| AI 任务完成 | 系统通知 |

---

## 文件优先级

Claude Code 按以下顺序读取（后读的覆盖先读的）：

```
1. CLAUDE.md                  ← 团队共享指令
2. CLAUDE.local.md            ← 个人覆盖（最高优先级）
3. .claude/rules/*.md         ← 模块化规则
4. .claude/settings.json      ← 团队权限 + Hooks
5. .claude/settings.local.json ← 个人权限覆盖
```

---

## 常见问题

**Q: 团队成员 clone 后配置没有生效？**
A: 确认项目根目录存在 `CLAUDE.md` 和 `.claude/` 目录。运行 `claude doctor` 排查。

**Q: 团队规则和个人偏好冲突？**
A: 在 `CLAUDE.local.md` 中添加你的偏好（Git 忽略），会覆盖团队规则。

**Q: 技术负责人如何为特定项目定制规则？**
A: 在 `.claude/rules/` 下新增 `.md` 文件，或在 `CLAUDE.md` 中调整规范内容后提交。

**Q: 如何贡献改进？**
A: 提交 MR 到 [Kelisiyounger/Team-plugin](https://github.com/Kelisiyounger/Team-plugin)。
