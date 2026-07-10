#!/usr/bin/env bash
# =============================================================================
# sync-claude-md.sh — 城际云团队 CLAUDE.md 同步脚本
# =============================================================================
# 功能:
#   1. 首次运行: 扫描用户全局 CLAUDE.md, 将团队 updateCLAUDE.md 内容以标记块
#      形式写入文件顶部(最高优先级), 保留用户原有个人设置
#   2. 后续更新: 仅替换标记块内的团队内容, 不动用户个人设置
#   3. 版本比对: 若团队版本号未变则跳过, 避免无意义写入
# =============================================================================
# 标记格式:
#   <!-- === TEAM:ChengJiYun:START:vX.Y.Z === -->
#   ... 团队内容 (由 chengjiyun-dev-tools 自动管理) ...
#   <!-- === TEAM:ChengJiYun:END:vX.Y.Z === -->
# =============================================================================

set -euo pipefail

# ---- 路径配置 ----
GLOBAL_CLAUDE_MD="${HOME}/.claude/CLAUDE.md"
PLUGIN_DIR="$(cd "$(dirname "$0")/.." && pwd)"
UPDATE_CLAUDE_MD="${PLUGIN_DIR}/updateCLAUDE.md"
PLUGIN_JSON="${PLUGIN_DIR}/.claude-plugin/plugin.json"
SYNC_STATE_FILE="${HOME}/.claude/.chengjiyun-sync-state.json"

# 团队标记正则
MARKER_START_PATTERN="<!-- === TEAM:ChengJiYun:START:v"
MARKER_END_PATTERN="<!-- === TEAM:ChengJiYun:END:v"

# ---- 颜色输出 ----
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info()  { echo -e "${BLUE}[城际云 Sync]${NC} $*"; }
log_ok()    { echo -e "${GREEN}[城际云 Sync]${NC} ✅ $*"; }
log_warn()  { echo -e "${YELLOW}[城际云 Sync]${NC} ⚠️  $*"; }

# ---- 获取团队插件版本 ----
get_plugin_version() {
  if [ -f "${PLUGIN_JSON}" ]; then
    # 用简单方式提取 version 字段 (不依赖 jq)
    grep -o '"version"[[:space:]]*:[[:space:]]*"[^"]*"' "${PLUGIN_JSON}" \
      | head -1 \
      | sed 's/.*"\([0-9.]*\)".*/\1/'
  else
    echo "0.0.0"
  fi
}

# ---- 从 CLAUDE.md 中提取已存在的团队版本 ----
get_claude_md_team_version() {
  if [ -f "${GLOBAL_CLAUDE_MD}" ]; then
    grep -o "${MARKER_START_PATTERN}[0-9.]*" "${GLOBAL_CLAUDE_MD}" 2>/dev/null \
      | head -1 \
      | sed "s/${MARKER_START_PATTERN}//" \
      || echo ""
  else
    echo ""
  fi
}

# ---- 读取同步状态 ----
read_sync_state() {
  if [ -f "${SYNC_STATE_FILE}" ]; then
    cat "${SYNC_STATE_FILE}"
  else
    echo '{"claudeMdVersion":"","pluginVersion":"","lastSync":"","verified":false}'
  fi
}

# ---- 写入同步状态 ----
write_sync_state() {
  local claude_md_ver="$1"
  local plugin_ver="$2"
  local verified="${3:-false}"
  local timestamp
  timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ" 2>/dev/null || date +"%Y-%m-%dT%H:%M:%SZ")

  cat > "${SYNC_STATE_FILE}" << STATE_EOF
{
  "claudeMdVersion": "${claude_md_ver}",
  "pluginVersion": "${plugin_ver}",
  "lastSync": "${timestamp}",
  "verified": ${verified}
}
STATE_EOF
}

# ---- 核心: 同步 CLAUDE.md ----
sync_claude_md() {
  local plugin_version
  plugin_version=$(get_plugin_version)

  log_info "插件版本: v${plugin_version}"

  # 检查 updateCLAUDE.md 是否存在
  if [ ! -f "${UPDATE_CLAUDE_MD}" ]; then
    log_warn "updateCLAUDE.md 不存在: ${UPDATE_CLAUDE_MD}"
    return 1
  fi

  local team_content
  team_content=$(cat "${UPDATE_CLAUDE_MD}")

  local marker_start="<!-- === TEAM:ChengJiYun:START:v${plugin_version} === -->"
  local marker_end="<!-- === TEAM:ChengJiYun:END:v${plugin_version} === -->"

  # 情况1: 全局 CLAUDE.md 不存在 → 直接创建
  if [ ! -f "${GLOBAL_CLAUDE_MD}" ]; then
    log_info "全局 CLAUDE.md 不存在，创建中..."
    mkdir -p "$(dirname "${GLOBAL_CLAUDE_MD}")"
    cat > "${GLOBAL_CLAUDE_MD}" << MD_EOF
${marker_start}
${team_content}
${marker_end}
MD_EOF
    log_ok "已创建全局 CLAUDE.md (v${plugin_version})"
    write_sync_state "${plugin_version}" "${plugin_version}" "true"
    return 0
  fi

  # 情况2: 检查是否已有团队标记
  local existing_version
  existing_version=$(get_claude_md_team_version)

  if [ -n "${existing_version}" ]; then
    # ---- 已有团队标记: 仅更新团队区块 ----
    log_info "检测到已有团队标记 v${existing_version}"

    if [ "${existing_version}" = "${plugin_version}" ]; then
      log_ok "团队内容已是最新版本 v${plugin_version}，跳过同步"
      write_sync_state "${plugin_version}" "${plugin_version}" "true"
      return 0
    fi

    # 提取用户个人内容 (标记块之前和之后的内容)
    local before_team after_team
    before_team=$(sed "/${MARKER_START_PATTERN}/,\$d" "${GLOBAL_CLAUDE_MD}")
    after_team=$(sed "1,/${MARKER_END_PATTERN}/d" "${GLOBAL_CLAUDE_MD}")

    # 更新 marker 中的版本号
    marker_start="<!-- === TEAM:ChengJiYun:START:v${plugin_version} === -->"
    marker_end="<!-- === TEAM:ChengJiYun:END:v${plugin_version} === -->"

    # 重建文件: 前置内容 + 新团队内容 + 用户个人内容
    local tmp_file="${GLOBAL_CLAUDE_MD}.tmp.$$"
    {
      if [ -n "${before_team}" ]; then
        echo "${before_team}"
      fi
      echo "${marker_start}"
      echo "${team_content}"
      echo "${marker_end}"
      if [ -n "${after_team}" ]; then
        echo "${after_team}"
      fi
    } > "${tmp_file}"

    mv "${tmp_file}" "${GLOBAL_CLAUDE_MD}"
    log_ok "团队区块已更新: v${existing_version} → v${plugin_version}"
    write_sync_state "${plugin_version}" "${plugin_version}" "true"

  else
    # ---- 无团队标记: 首次注入 ----
    log_info "首次运行，注入团队配置到全局 CLAUDE.md..."

    local existing_content
    existing_content=$(cat "${GLOBAL_CLAUDE_MD}")

    # 备份原文件
    cp "${GLOBAL_CLAUDE_MD}" "${GLOBAL_CLAUDE_MD}.bak.$(date +%Y%m%d_%H%M%S)"
    log_info "已备份原 CLAUDE.md → ${GLOBAL_CLAUDE_MD}.bak.$(date +%Y%m%d_%H%M%S)"

    # 检查现有内容是否与团队内容实质相同 (避免重复)
    # 使用规范化比较: 去除尾随空白和空行差异
    local existing_normalized team_normalized
    existing_normalized=$(echo "${existing_content}" | sed 's/[[:space:]]*$//' | sed '/^$/d')
    team_normalized=$(echo "${team_content}" | sed 's/[[:space:]]*$//' | sed '/^$/d')

    local tmp_file="${GLOBAL_CLAUDE_MD}.tmp.$$"

    if [ "${existing_normalized}" = "${team_normalized}" ]; then
      # 用户现有内容就是团队内容 → 仅添加标记，不重复
      log_info "检测到现有 CLAUDE.md 与团队内容一致，仅添加标记块"
      cat > "${tmp_file}" << MD_EOF
${marker_start}
${existing_content}
${marker_end}
MD_EOF
    else
      # 用户有自己的设置 → 团队内容放顶部 (最高优先级)，用户内容保留在后面
      log_info "检测到用户个人设置，团队配置注入到顶部"
      cat > "${tmp_file}" << MD_EOF
${marker_start}
${team_content}
${marker_end}

---

${existing_content}
MD_EOF
    fi

    mv "${tmp_file}" "${GLOBAL_CLAUDE_MD}"
    log_ok "团队配置已注入到全局 CLAUDE.md 顶部 (v${plugin_version})"
    log_info "用户原有内容保留在团队区块之后"
    write_sync_state "${plugin_version}" "${plugin_version}" "true"
  fi

  return 0
}

# ---- 校验同步状态 ----
verify_sync() {
  local plugin_version
  plugin_version=$(get_plugin_version)

  if [ ! -f "${GLOBAL_CLAUDE_MD}" ]; then
    log_warn "全局 CLAUDE.md 不存在，同步未完成"
    return 1
  fi

  local md_version
  md_version=$(get_claude_md_team_version)

  if [ "${md_version}" = "${plugin_version}" ]; then
    log_ok "验证通过: CLAUDE.md 团队版本 v${md_version} == 插件版本 v${plugin_version}"
    write_sync_state "${plugin_version}" "${plugin_version}" "true"
    return 0
  else
    log_warn "版本不一致: CLAUDE.md=v${md_version} vs 插件=v${plugin_version}"
    return 1
  fi
}

# ---- 主入口 ----
main() {
  local cmd="${1:-sync}"

  case "${cmd}" in
    sync)
      sync_claude_md
      ;;
    verify)
      verify_sync
      ;;
    status)
      echo "=== 城际云 Team Config Sync 状态 ==="
      echo "插件版本: $(get_plugin_version)"
      echo "CLAUDE.md 团队版本: $(get_claude_md_team_version)"
      echo "同步状态:"
      read_sync_state
      ;;
    force)
      log_info "强制执行同步..."
      # 删除旧标记行，强制重新注入
      if [ -f "${GLOBAL_CLAUDE_MD}" ]; then
        sed -i "/${MARKER_START_PATTERN}/d;/${MARKER_END_PATTERN}/d" "${GLOBAL_CLAUDE_MD}"
      fi
      sync_claude_md
      ;;
    *)
      echo "用法: $0 {sync|verify|status|force}"
      echo "  sync   - 执行智能同步 (默认)"
      echo "  verify - 校验同步状态"
      echo "  status - 显示当前状态"
      echo "  force  - 强制重新注入团队配置"
      exit 1
      ;;
  esac
}

main "$@"
