#!/usr/bin/env bash
# =============================================================================
# check-version.sh — 城际云团队 Plugin 版本检查脚本
# =============================================================================
# 功能:
#   1. 比较本地 plugin 版本与团队 Git 仓库的最新版本
#   2. 扫描全局 CLAUDE.md 中的团队版本标记
#   3. 若版本不一致，返回非零退出码 (触发自动更新)
#   4. 验证通过后写入状态文件，避免重复检查
# =============================================================================
# 状态文件: ~/.claude/.chengjiyun-sync-state.json
#   记录已校验的版本号，若版本号未变则跳过后续检查
# =============================================================================

set -euo pipefail

# ---- 路径配置 ----
PLUGIN_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PLUGIN_JSON="${PLUGIN_DIR}/.claude-plugin/plugin.json"
UPDATE_CLAUDE_MD="${PLUGIN_DIR}/updateCLAUDE.md"
GLOBAL_CLAUDE_MD="${HOME}/.claude/CLAUDE.md"
SYNC_STATE_FILE="${HOME}/.claude/.chengjiyun-sync-state.json"

# ---- 颜色输出 ----
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

log_info()  { echo -e "${BLUE}[城际云 VersionCheck]${NC} $*"; }
log_ok()    { echo -e "${GREEN}[城际云 VersionCheck]${NC} ✅ $*"; }
log_warn()  { echo -e "${YELLOW}[城际云 VersionCheck]${NC} ⚠️  $*"; }
log_err()   { echo -e "${RED}[城际云 VersionCheck]${NC} ❌ $*"; }

# ---- 获取本地插件版本 ----
get_local_plugin_version() {
  if [ -f "${PLUGIN_JSON}" ]; then
    grep -o '"version"[[:space:]]*:[[:space:]]*"[^"]*"' "${PLUGIN_JSON}" \
      | head -1 \
      | sed 's/.*"\([0-9.]*\)".*/\1/'
  else
    echo "0.0.0"
  fi
}

# ---- 获取远程 Git 仓库的最新版本 (通过 git ls-remote) ----
get_remote_plugin_version() {
  # 从 plugin.json 中提取仓库 URL
  local repo_url
  repo_url=$(grep -o '"url"[[:space:]]*:[[:space:]]*"[^"]*"' "${PLUGIN_JSON}" \
    | head -1 \
    | sed 's/.*"\([^"]*\)".*/\1/')

  if [ -z "${repo_url}" ] || [ "${repo_url}" = "" ]; then
    echo ""
    return 1
  fi

  # 尝试从远程获取 plugin.json 的最新版本号
  # 使用 git archive 获取远程文件内容 (只读操作，不 clone)
  local remote_version=""
  remote_version=$(git archive --remote="${repo_url}" HEAD .claude-plugin/plugin.json 2>/dev/null \
    | tar -xO 2>/dev/null \
    | grep -o '"version"[[:space:]]*:[[:space:]]*"[^"]*"' \
    | head -1 \
    | sed 's/.*"\([0-9.]*\)".*/\1/' || echo "")

  if [ -z "${remote_version}" ]; then
    # git archive 不可用时，尝试 git ls-remote 判断 HEAD 的 commit hash
    # 通过比较本地和远程 HEAD commit 来判断是否有更新
    local local_head remote_head
    local_head=$(git -C "${PLUGIN_DIR}" rev-parse HEAD 2>/dev/null || echo "")
    remote_head=$(git ls-remote "${repo_url}" HEAD 2>/dev/null | awk '{print $1}' || echo "")

    if [ -n "${remote_head}" ] && [ "${local_head}" != "${remote_head}" ]; then
      # HEAD 不一致，可能已更新，需要进一步检查
      echo "__UNKNOWN__"  # 特殊标记: 版本未知但确有更新
      return 0
    elif [ -n "${remote_head}" ] && [ "${local_head}" = "${remote_head}" ]; then
      # HEAD 一致，版本未变
      echo "${LOCAL_VERSION:-$(get_local_plugin_version)}"
      return 0
    else
      # 无法连接远程
      echo ""
      return 1
    fi
  fi

  echo "${remote_version}"
}

# ---- 获取 CLAUDE.md 中的团队版本 ----
get_claude_md_version() {
  if [ -f "${GLOBAL_CLAUDE_MD}" ]; then
    grep -o "TEAM:ChengJiYun:START:v[0-9.]*" "${GLOBAL_CLAUDE_MD}" 2>/dev/null \
      | head -1 \
      | sed 's/.*v//' \
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

# ---- 检查是否需要更新 ----
check_needs_update() {
  local local_ver remote_ver md_ver needs_update
  local_ver=$(get_local_plugin_version)
  md_ver=$(get_claude_md_version)
  needs_update="false"
  local reasons=()

  log_info "========== 版本检查 =========="
  log_info "本地插件版本:   v${local_ver}"

  # 1. 检查远程插件版本
  remote_ver=$(get_remote_plugin_version 2>/dev/null || echo "")
  if [ -n "${remote_ver}" ] && [ "${remote_ver}" != "__UNKNOWN__" ]; then
    log_info "远程插件版本:   v${remote_ver}"
    if [ "${remote_ver}" != "${local_ver}" ]; then
      needs_update="true"
      reasons+=("插件版本需更新: v${local_ver} → v${remote_ver}")
    else
      log_ok "插件版本一致"
    fi
  elif [ "${remote_ver}" = "__UNKNOWN__" ]; then
    log_warn "远程仓库有更新，但无法获取版本号"
    needs_update="true"
    reasons+=("远程仓库 HEAD 已变更")
  else
    log_info "远程插件版本:   (无法连接，跳过)"
  fi

  # 2. 检查 CLAUDE.md 中的团队版本
  if [ -z "${md_ver}" ]; then
    log_warn "CLAUDE.md 团队版本: 未检测到 (需首次注入)"
    needs_update="true"
    reasons+=("CLAUDE.md 缺少团队标记")
  else
    log_info "CLAUDE.md 团队版本: v${md_ver}"
    if [ "${md_ver}" != "${local_ver}" ]; then
      needs_update="true"
      reasons+=("CLAUDE.md 版本滞后: v${md_ver} → v${local_ver}")
    else
      log_ok "CLAUDE.md 版本一致"
    fi
  fi

  # 3. 检查 sync-state 是否已标记为 verified
  local state_verified
  state_verified=$(read_sync_state | grep -o '"verified"[[:space:]]*:[[:space:]]*true' || echo "")
  if [ -n "${state_verified}" ] && [ "${needs_update}" = "false" ]; then
    log_ok "已验证且无需更新，跳过"
    return 1  # 返回1表示无需操作
  fi

  # 4. 输出结果
  if [ "${needs_update}" = "true" ]; then
    log_err "需要更新:"
    for reason in "${reasons[@]}"; do
      echo "       • ${reason}"
    done
    return 0  # 返回0表示需要更新
  else
    log_ok "所有组件均是最新版本"
    # 写入 verified 状态
    local timestamp
    timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ" 2>/dev/null || date +"%Y-%m-%dT%H:%M:%SZ")
    cat > "${SYNC_STATE_FILE}" << STATE_EOF
{
  "claudeMdVersion": "${md_ver}",
  "pluginVersion": "${local_ver}",
  "lastSync": "${timestamp}",
  "verified": true
}
STATE_EOF
    return 1  # 无需操作
  fi
}

# ---- 主入口 ----
main() {
  local cmd="${1:-check}"

  case "${cmd}" in
    check)
      # 检查版本并输出结果
      if check_needs_update; then
        log_warn "需要执行同步更新"
        exit 0
      else
        log_ok "无需更新"
        exit 0
      fi
      ;;
    needs-update)
      # 仅返回退出码: 0=需要更新, 1=不需要
      check_needs_update
      ;;
    remote-version)
      get_remote_plugin_version
      ;;
    local-version)
      get_local_plugin_version
      ;;
    md-version)
      get_claude_md_version
      ;;
    status)
      echo "=== 城际云 Version Check 状态 ==="
      echo ""
      echo "本地插件版本:     v$(get_local_plugin_version)"
      echo "远程插件版本:     v$(get_remote_plugin_version 2>/dev/null || echo 'N/A')"
      echo "CLAUDE.md 版本:   v$(get_claude_md_version)"
      echo ""
      echo "同步状态文件:"
      read_sync_state
      ;;
    *)
      echo "用法: $0 {check|needs-update|remote-version|local-version|md-version|status}"
      exit 1
      ;;
  esac
}

main "$@"
