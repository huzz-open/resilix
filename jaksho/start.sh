#!/usr/bin/env bash
#
# Jaksho 一键启动脚本
#
# 用法：
#   ./start.sh              前台启动（Ctrl+C 停止）
#   ./start.sh -d           后台守护模式启动
#   ./start.sh stop         停止所有服务
#   ./start.sh status       查看服务状态
#   ./start.sh logs         实时查看日志（tail -f）
#
# 所有配置均可通过环境变量外部指定，例如：
#   DB_TYPE=mysql DB_HOST=10.0.0.5 ./start.sh -d
#

set -uo pipefail

# ============================================================
#  配置区域 - 可在此修改，也可通过环境变量外部指定
# ============================================================

# 数据库类型：mariadb 或 mysql
# JDBC URL 始终使用 jdbc:mariadb:// 前缀（MariaDB Connector/J 3.x 兼容连接 MySQL）
# 此变量仅在 docker-start.sh 中影响镜像选择和初始化命令
DB_TYPE="${DB_TYPE:-mariadb}"

DB_PASSWORD="${DB_PASSWORD:-}"
DB_HOST="${DB_HOST:-}"
DB_PORT="${DB_PORT:-}"
DB_NAME="${DB_NAME:-}"
DB_USER="${DB_USER:-}"
BACKEND_PORT="${BACKEND_PORT:-}"
FRONTEND_PORT="${FRONTEND_PORT:-}"
RUN_NPM_INSTALL="${RUN_NPM_INSTALL:-true}"
MVN_EXTRA_ARGS="${MVN_EXTRA_ARGS:--DskipTests}"

# ============================================================
#  以下内容一般不需要修改
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"
PID_FILE="$SCRIPT_DIR/.jaksho.pid"
BACKEND_LOG="$LOG_DIR/backend.log"
FRONTEND_LOG="$LOG_DIR/frontend.log"
BACKEND_PID=""
FRONTEND_PID=""

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC}  $*"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
log_error() { echo -e "${RED}[ERROR]${NC} $*"; }
log_step()  { echo -e "${CYAN}[STEP]${NC}  $*"; }

mkdir -p "$LOG_DIR"

# ============================================================
#  子命令：stop
# ============================================================
do_stop() {
    if [[ ! -f "$PID_FILE" ]]; then
        log_info "没有运行中的服务（PID 文件不存在）"
        return 0
    fi

    source "$PID_FILE"
    local stopped=false

    if [[ -n "${SAVED_BACKEND_PID:-}" ]] && kill -0 "$SAVED_BACKEND_PID" 2>/dev/null; then
        kill "$SAVED_BACKEND_PID" 2>/dev/null || true
        log_info "后端服务已停止 (PID: $SAVED_BACKEND_PID)"
        stopped=true
    fi

    if [[ -n "${SAVED_FRONTEND_PID:-}" ]] && kill -0 "$SAVED_FRONTEND_PID" 2>/dev/null; then
        kill "$SAVED_FRONTEND_PID" 2>/dev/null || true
        log_info "前端服务已停止 (PID: $SAVED_FRONTEND_PID)"
        stopped=true
    fi

    rm -f "$PID_FILE"

    if [[ "$stopped" == false ]]; then
        log_info "服务未在运行"
    else
        log_info "所有服务已停止"
    fi
}

# ============================================================
#  子命令：status
# ============================================================
do_status() {
    if [[ ! -f "$PID_FILE" ]]; then
        log_info "服务未启动"
        return 1
    fi

    source "$PID_FILE"
    local running=false

    echo ""
    if [[ -n "${SAVED_BACKEND_PID:-}" ]] && kill -0 "$SAVED_BACKEND_PID" 2>/dev/null; then
        log_info "后端服务运行中 (PID: $SAVED_BACKEND_PID) — 日志: $BACKEND_LOG"
        running=true
    else
        log_warn "后端服务未运行"
    fi

    if [[ -n "${SAVED_FRONTEND_PID:-}" ]] && kill -0 "$SAVED_FRONTEND_PID" 2>/dev/null; then
        log_info "前端服务运行中 (PID: $SAVED_FRONTEND_PID) — 日志: $FRONTEND_LOG"
        running=true
    else
        log_warn "前端服务未运行"
    fi
    echo ""

    if [[ "$running" == false ]]; then
        rm -f "$PID_FILE"
        return 1
    fi
}

# ============================================================
#  子命令：logs
# ============================================================
do_logs() {
    if [[ ! -f "$BACKEND_LOG" ]] && [[ ! -f "$FRONTEND_LOG" ]]; then
        log_error "日志文件不存在，服务可能未启动过"
        exit 1
    fi
    tail -f "$BACKEND_LOG" "$FRONTEND_LOG" 2>/dev/null
}

# ============================================================
#  路由子命令
# ============================================================
case "${1:-}" in
    stop)   do_stop; exit $? ;;
    status) do_status; exit $? ;;
    logs)   do_logs; exit $? ;;
esac

DAEMON_MODE=false
if [[ "${1:-}" == "-d" || "${1:-}" == "--daemon" ]]; then
    DAEMON_MODE=true
fi

# ============================================================
#  检查是否已在运行
# ============================================================
if [[ -f "$PID_FILE" ]]; then
    source "$PID_FILE"
    if [[ -n "${SAVED_BACKEND_PID:-}" ]] && kill -0 "$SAVED_BACKEND_PID" 2>/dev/null; then
        log_error "服务已在运行 (后端 PID: $SAVED_BACKEND_PID)，请先执行 ./start.sh stop"
        exit 1
    fi
    rm -f "$PID_FILE"
fi

# ============================================================
#  cleanup（前台模式用）
# ============================================================
CLEANING_UP=false
cleanup() {
    if [[ "$CLEANING_UP" == true ]]; then return; fi
    CLEANING_UP=true
    echo ""
    log_info "正在停止所有服务..."

    if [[ -n "$BACKEND_PID" ]] && kill -0 "$BACKEND_PID" 2>/dev/null; then
        kill "$BACKEND_PID" 2>/dev/null || true
        wait "$BACKEND_PID" 2>/dev/null || true
        log_info "后端服务已停止 (PID: $BACKEND_PID)"
    fi

    if [[ -n "$FRONTEND_PID" ]] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
        kill "$FRONTEND_PID" 2>/dev/null || true
        wait "$FRONTEND_PID" 2>/dev/null || true
        log_info "前端服务已停止 (PID: $FRONTEND_PID)"
    fi

    rm -f "$PID_FILE"
    log_info "所有服务已停止，再见！"
}

if [[ "$DAEMON_MODE" == false ]]; then
    trap cleanup SIGINT SIGTERM EXIT
fi

# ============================================================
#  前置检查
# ============================================================
log_step "检查运行环境..."
check_command() {
    if ! command -v "$1" &>/dev/null; then
        log_error "未找到命令: $1，请先安装"
        exit 1
    fi
}

check_command java
check_command mvn

export NVM_DIR="${NVM_DIR:-$HOME/.nvm}"
if [[ -s "$NVM_DIR/nvm.sh" ]]; then
    \. "$NVM_DIR/nvm.sh"
fi

JAVA_VER_LINE=$(java -version 2>&1 | grep -E '(java|openjdk) version')
JAVA_VER=$(echo "$JAVA_VER_LINE" | awk -F '"' '{print $2}' | cut -d. -f1)
if [[ -z "$JAVA_VER" ]] || [[ "$JAVA_VER" -lt 17 ]]; then
    log_error "需要 Java 17+，当前版本: $JAVA_VER_LINE"
    exit 1
fi

NODE_VER_FULL=$(node -v 2>/dev/null || echo "none")
NODE_MAJOR=$(echo "$NODE_VER_FULL" | sed 's/^v//' | cut -d. -f1)
if [[ "$NODE_VER_FULL" == "none" ]] || [[ -z "$NODE_MAJOR" ]] || [[ "$NODE_MAJOR" -lt 18 ]]; then
    if [[ "$NODE_VER_FULL" == "none" ]]; then
        log_error "未安装 Node.js（需要 18+）"
    else
        log_error "需要 Node.js 18+，当前版本: $NODE_VER_FULL"
    fi
    read -rp "$(echo -e "${YELLOW}[ASK]${NC}  是否自动安装 Node.js 22（通过 nvm）？[Y/n] ")" INSTALL_NODE
    INSTALL_NODE="${INSTALL_NODE:-Y}"
    if [[ "$INSTALL_NODE" =~ ^[Yy]$ ]]; then
        log_step "安装 nvm + Node.js 22 ..."
        if [[ ! -s "$NVM_DIR/nvm.sh" ]]; then
            curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
            \. "$NVM_DIR/nvm.sh"
        fi
        nvm install 22
        NODE_VER_FULL=$(node -v)
        log_info "Node.js 已安装: $NODE_VER_FULL"
    else
        log_error "已取消，请手动安装 Node.js 18+ 后重试"
        exit 1
    fi
fi

check_command node
check_command npm

log_info "Java $(echo "$JAVA_VER_LINE" | awk -F '"' '{print $2}')"
log_info "Maven $(mvn -version 2>&1 | grep 'Apache Maven' | awk '{print $3}')"
log_info "Node  $NODE_VER_FULL"

# ============================================================
#  构建后端启动参数
# ============================================================
JVM_ARGS=""
SPRING_RUN_ARGS=""

if [[ -n "$DB_PASSWORD" ]]; then
    export DB_PASSWORD
    log_info "使用脚本配置的 DB_PASSWORD"
fi

if [[ -n "$DB_HOST" || -n "$DB_PORT" || -n "$DB_NAME" ]]; then
    _host="${DB_HOST:-localhost}"
    _port="${DB_PORT:-3306}"
    _name="${DB_NAME:-jaksho_new}"
    JVM_ARGS="$JVM_ARGS -Dspring.datasource.url=jdbc:mariadb://${_host}:${_port}/${_name}?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useInformationSchema=true"
    log_info "覆盖数据库连接: ${_host}:${_port}/${_name}"
fi

if [[ -n "$DB_USER" ]]; then
    JVM_ARGS="$JVM_ARGS -Dspring.datasource.username=$DB_USER"
    log_info "覆盖数据库用户: $DB_USER"
fi

if [[ -n "$BACKEND_PORT" ]]; then
    SPRING_RUN_ARGS="--dubbo.protocol.port=$BACKEND_PORT"
    log_info "覆盖后端端口: $BACKEND_PORT"
fi

# ============================================================
#  编译后端
# ============================================================
log_step "编译后端模块..."
cd "$SCRIPT_DIR"
if ! mvn -pl jaksho -am compile $MVN_EXTRA_ARGS; then
    log_error "后端编译失败"
    exit 1
fi

# ============================================================
#  安装前端依赖
# ============================================================
if [[ "$RUN_NPM_INSTALL" == true ]]; then
    log_step "安装前端依赖..."
    cd "$SCRIPT_DIR/jaksho-fe"
    if ! npm install; then
        log_error "npm install 失败，请检查网络或 package.json"
        exit 1
    fi
fi

# ============================================================
#  启动后端（日志重定向到文件）
# ============================================================
log_step "启动后端服务..."
cd "$SCRIPT_DIR/jaksho"

MVN_CMD=(mvn spring-boot:run $MVN_EXTRA_ARGS)
if [[ -n "$JVM_ARGS" ]]; then
    MVN_CMD+=("-Dspring-boot.run.jvmArguments=$JVM_ARGS")
fi
if [[ -n "$SPRING_RUN_ARGS" ]]; then
    MVN_CMD+=("-Dspring-boot.run.arguments=$SPRING_RUN_ARGS")
fi

> "$BACKEND_LOG"
"${MVN_CMD[@]}" >> "$BACKEND_LOG" 2>&1 &
BACKEND_PID=$!
log_info "后端服务启动中... (PID: $BACKEND_PID, 日志: logs/backend.log)"

# ============================================================
#  启动前端（日志重定向到文件）
# ============================================================
log_step "启动前端服务..."
cd "$SCRIPT_DIR/jaksho-fe"

FE_CMD=(npm run dev)
if [[ -n "$FRONTEND_PORT" ]]; then
    FE_CMD+=(-- --port "$FRONTEND_PORT")
    log_info "覆盖前端端口: $FRONTEND_PORT"
fi

> "$FRONTEND_LOG"
"${FE_CMD[@]}" >> "$FRONTEND_LOG" 2>&1 &
FRONTEND_PID=$!
log_info "前端服务启动中... (PID: $FRONTEND_PID, 日志: logs/frontend.log)"

# ============================================================
#  写入 PID 文件
# ============================================================
cat > "$PID_FILE" <<EOF
SAVED_BACKEND_PID=$BACKEND_PID
SAVED_FRONTEND_PID=$FRONTEND_PID
EOF

# ============================================================
#  输出摘要
# ============================================================
_be_port="${BACKEND_PORT:-8080}"
_fe_port="${FRONTEND_PORT:-3002}"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Jaksho 启动完成${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "  后端 (Dubbo Triple): http://localhost:${_be_port}"
echo -e "  前端 (Vite Dev):     http://localhost:${_fe_port}"
echo -e ""
echo -e "  日志目录: ${CYAN}$LOG_DIR${NC}"
echo -e "    后端日志: tail -f logs/backend.log"
echo -e "    前端日志: tail -f logs/frontend.log"
echo -e "    全部日志: ${CYAN}./start.sh logs${NC}"
echo -e ""
if [[ "$DAEMON_MODE" == true ]]; then
    echo -e "  运行模式: ${YELLOW}后台守护${NC}"
    echo -e "  停止服务: ${CYAN}./start.sh stop${NC}"
    echo -e "  查看状态: ${CYAN}./start.sh status${NC}"
else
    echo -e "  运行模式: ${YELLOW}前台${NC}"
    echo -e "  按 ${YELLOW}Ctrl+C${NC} 停止所有服务"
fi
echo -e "${GREEN}========================================${NC}"
echo ""

# ============================================================
#  前台模式等待 / 后台模式退出
# ============================================================
if [[ "$DAEMON_MODE" == true ]]; then
    log_info "后台模式启动完成，脚本退出"
    trap - EXIT
    exit 0
fi

wait -n "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
