#!/usr/bin/env bash
#
# Jaksho 一键启动脚本
# 同时启动后端 (Spring Boot + Dubbo) 和前端 (Vite Dev Server)
# 使用 Ctrl+C 可同时停止所有服务
#

set -uo pipefail

# ============================================================
#  配置区域 - 按需修改以下参数
#  所有参数均为可选，不填则使用 application.yml 中的默认值
# ============================================================

# 数据库密码，不填则使用 application.yml / datasource.yaml 中的配置
DB_PASSWORD=""

# 数据库连接信息，不填则使用 datasource.yaml 默认值
# 默认值：localhost / 3306 / jaksho_new / root
DB_HOST=""
DB_PORT=""
DB_NAME=""
DB_USER=""

# 后端服务端口（Dubbo Triple 协议），不填则使用 application.yml 默认值（8080）
BACKEND_PORT=""

# 前端开发服务器端口，不填则使用 vite.config.ts 默认值（3002）
FRONTEND_PORT=""

# 是否在启动前端之前执行 npm install（首次启动设为 true）
RUN_NPM_INSTALL=true

# Maven 额外参数
MVN_EXTRA_ARGS="-DskipTests"

# ============================================================
#  以下内容一般不需要修改
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
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

    log_info "所有服务已停止，再见！"
}

trap cleanup SIGINT SIGTERM EXIT

check_command() {
    if ! command -v "$1" &>/dev/null; then
        log_error "未找到命令: $1，请先安装"
        exit 1
    fi
}

# --- 前置检查 ---
log_step "检查运行环境..."
check_command java
check_command mvn
check_command node
check_command npm

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
        export NVM_DIR="$HOME/.nvm"
        if [[ ! -s "$NVM_DIR/nvm.sh" ]]; then
            curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
        fi
        \. "$NVM_DIR/nvm.sh"
        nvm install 22
        NODE_VER_FULL=$(node -v)
        log_info "Node.js 已安装: $NODE_VER_FULL"
    else
        log_error "已取消，请手动安装 Node.js 18+ 后重试"
        exit 1
    fi
fi

log_info "Java $(echo "$JAVA_VER_LINE" | awk -F '"' '{print $2}')"
log_info "Maven $(mvn -version 2>&1 | grep 'Apache Maven' | awk '{print $3}')"
log_info "Node  $NODE_VER_FULL"

# --- 构建后端启动参数（仅在配置了对应值时才覆盖） ---
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

# --- 编译后端 ---
log_step "编译后端模块..."
cd "$SCRIPT_DIR"
if ! mvn -pl jaksho -am compile $MVN_EXTRA_ARGS; then
    log_error "后端编译失败"
    exit 1
fi

# --- 启动后端 ---
log_step "启动后端服务..."
cd "$SCRIPT_DIR/jaksho"

MVN_CMD=(mvn spring-boot:run $MVN_EXTRA_ARGS)
if [[ -n "$JVM_ARGS" ]]; then
    MVN_CMD+=("-Dspring-boot.run.jvmArguments=$JVM_ARGS")
fi
if [[ -n "$SPRING_RUN_ARGS" ]]; then
    MVN_CMD+=("-Dspring-boot.run.arguments=$SPRING_RUN_ARGS")
fi

"${MVN_CMD[@]}" &
BACKEND_PID=$!
log_info "后端服务启动中... (PID: $BACKEND_PID)"

# --- 启动前端 ---
log_step "启动前端服务..."
cd "$SCRIPT_DIR/jaksho-fe"

if [[ "$RUN_NPM_INSTALL" == true ]]; then
    log_info "安装前端依赖..."
    if ! npm install; then
        log_error "npm install 失败，请检查网络或 package.json"
        exit 1
    fi
fi

FE_CMD=(npm run dev)
if [[ -n "$FRONTEND_PORT" ]]; then
    FE_CMD+=(-- --port "$FRONTEND_PORT")
    log_info "覆盖前端端口: $FRONTEND_PORT"
fi

"${FE_CMD[@]}" &
FRONTEND_PID=$!
log_info "前端服务启动中... (PID: $FRONTEND_PID)"

# --- 输出摘要 ---
_be_port="${BACKEND_PORT:-8080}"
_fe_port="${FRONTEND_PORT:-3002}"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Jaksho 启动完成${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "  后端 (Dubbo Triple): http://localhost:${_be_port}"
echo -e "  前端 (Vite Dev):     http://localhost:${_fe_port}"
echo -e "  按 ${YELLOW}Ctrl+C${NC} 停止所有服务"
echo -e "${GREEN}========================================${NC}"
echo ""

# 等待任意后台进程退出；若某个崩溃，cleanup 会通过 EXIT trap 清理另一个
wait -n "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
