# team-config-sync — 城际云团队配置自动同步

**自动触发关键词**: 配置同步, config sync, 团队规范, 更新CLAUDE.md, plugin版本, 检查更新, 初始化开发环境, 安装plugin, setup, 团队开发包

---

## 职责

此 Skill 负责两件事：

### 1. CLAUDE.md 团队配置自动注入与同步

当用户**首次调用**此 Plugin 中的任意 Skill/Agent 时，自动执行：

1. **扫描** 本地全局 `~/.claude/CLAUDE.md` 文件
2. **注入** 团队标准配置（来自 `updateCLAUDE.md`）到 CLAUDE.md **顶部**（最高优先级）
3. **保留** 用户原有个人设置，放在团队配置区块之后
4. **冲突处理**: 团队规范优先级高于用户个人设置（因团队内容位于文件顶部，Claude 先读取）

后续每次更新：
- **仅替换** `<!-- TEAM:ChengJiYun:START:vX.Y.Z -->` 标记块内的团队内容
- **不改变** 用户个人设置（标记块之外的内容完全保留）

### 2. 开发时自动版本检查与更新

每次开发会话中自动执行版本比对：

1. **比较** 本地 Plugin 版本 ↔ 团队 Git 仓库 Plugin 版本
2. **扫描** 全局 CLAUDE.md 中的团队版本标记
3. **若落后**: 自动同步更新（拉取最新团队配置）
4. **验证通过**: 写入状态文件 `~/.claude/.chengjiyun-sync-state.json`，标记 `verified: true`，后续版本号未变时**不再重复检查**

---

## 执行流程

### 第一步：版本检查

执行版本检查脚本：

```bash
bash "${PLUGIN_DIR}/scripts/check-version.sh" check
```

如果检查结果需要更新（退出码 0），继续下一步。

### 第二步：CLAUDE.md 同步

执行同步脚本：

```bash
bash "${PLUGIN_DIR}/scripts/sync-claude-md.sh" sync
```

`sync` 模式的智能行为：
- **无标记**（首次运行）→ 注入团队配置到顶部 + 保留用户原有内容
- **有标记但版本不同** → 仅替换标记块内内容
- **有标记且版本相同** → 跳过

### 第三步：验证

执行验证：

```bash
bash "${PLUGIN_DIR}/scripts/sync-claude-md.sh" verify
```

验证通过后，状态文件标记 `verified: true`，该版本号下不再重复触发。

---

## CLAUDE.md 标记格式

团队配置在 CLAUDE.md 中使用以下标记包裹：

```markdown
<!-- === TEAM:ChengJiYun:START:v1.0.0 === -->
... 城际云团队标准配置内容 ...
<!-- === TEAM:ChengJiYun:END:v1.0.0 === -->
```

- **标记块内**: 团队管理，自动更新
- **标记块外**: 用户个人设置，永不覆盖

---

## 状态文件

位置: `~/.claude/.chengjiyun-sync-state.json`

```json
{
  "claudeMdVersion": "1.0.0",
  "pluginVersion": "1.0.0",
  "lastSync": "2026-07-09T12:00:00Z",
  "verified": true
}
```

- `verified: true` → 相同版本号下不再重复触发版本检查和同步
- `verified: false` → 下次会话触发时重新检查
- 插件版本号变更 → 自动重置 `verified: false`

---

## 手动调用

用户可以随时手动调用此 Skill：

```
/team-config-sync
```

或直接执行脚本：

```bash
# 同步 CLAUDE.md
bash scripts/sync-claude-md.sh sync

# 检查版本
bash scripts/check-version.sh check

# 查看状态
bash scripts/sync-claude-md.sh status
bash scripts/check-version.sh status

# 强制重新注入
bash scripts/sync-claude-md.sh force
```

---

## 注意事项

1. **最高优先级**: 团队配置写入 CLAUDE.md 文件顶部，Claude 按顺序读取，因此团队规范自动具有最高优先级
2. **不破坏用户设置**: 用户个人设置在标记块之外，更新团队配置时不会触碰
3. **幂等操作**: 相同版本号下重复执行不会产生副作用
4. **备份机制**: 首次注入前自动备份原 CLAUDE.md 文件
5. **离线场景**: 无法连接 Git 远程仓库时，仅检查本地 CLAUDE.md 与本地 Plugin 版本是否一致
