#!/usr/bin/env bash
#
# Jaksho Docker 一键启动脚本
# 使用 docker compose 启动前端 + 后端，可选内置数据库
# 使用 Ctrl+C 停止所有服务
#

set -euo pipefail

# ============================================================
#  配置区域 - 按需修改以下参数
# ============================================================

# 是否使用内置数据库（true = 启动一个 MariaDB 容器并自动初始化；false = 使用外部数据库）
USE_BUILTIN_DB=true

# 数据库密码（使用内置数据库时会作为 root 密码；使用外部数据库时需填写对应密码）
DB_PASSWORD="jaksho123"

# 数据库连接信息
# 使用内置数据库时：DB_HOST 会自动设为容器服务名，无需手动填写
# 使用外部数据库时：请填写实际的数据库地址
DB_HOST=""
DB_PORT="3306"
DB_NAME="jaksho_new"
DB_USER="root"

# 后端服务映射到宿主机的端口
BACKEND_PORT="8080"

# 前端服务映射到宿主机的端口
FRONTEND_PORT="3002"

# ============================================================
#  以下内容一般不需要修改
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC}  $*"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
log_error() { echo -e "${RED}[ERROR]${NC} $*"; }
log_step()  { echo -e "${CYAN}[STEP]${NC}  $*"; }

# --- 前置检查 ---
log_step "检查运行环境..."
if ! command -v docker &>/dev/null; then
    log_error "未找到 docker，请先安装 Docker"
    exit 1
fi
if ! docker compose version &>/dev/null; then
    log_error "未找到 docker compose，请安装 Docker Compose V2"
    exit 1
fi
log_info "Docker $(docker --version | awk '{print $3}' | tr -d ',')"
log_info "Compose $(docker compose version --short)"

# --- 组装环境变量 ---
if [[ "$USE_BUILTIN_DB" == true ]]; then
    export DB_HOST="db"
    log_info "使用内置数据库，数据将自动初始化"
else
    if [[ -z "$DB_HOST" ]]; then
        log_error "使用外部数据库时必须设置 DB_HOST"
        exit 1
    fi
    log_info "使用外部数据库: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
fi

export DB_PASSWORD DB_HOST DB_PORT DB_NAME DB_USER
export BACKEND_PORT FRONTEND_PORT

# --- 启动服务 ---
COMPOSE_CMD=(docker compose)

if [[ "$USE_BUILTIN_DB" == true ]]; then
    COMPOSE_CMD+=(--profile with-db)

    log_step "启动数据库..."
    "${COMPOSE_CMD[@]}" up -d db

    log_info "等待数据库就绪..."
    RETRIES=0
    MAX_RETRIES=30
    until docker compose --profile with-db exec db mariadb -u root -p"${DB_PASSWORD}" -e "SELECT 1" &>/dev/null; do
        RETRIES=$((RETRIES + 1))
        if [[ $RETRIES -ge $MAX_RETRIES ]]; then
            log_error "数据库启动超时，请检查日志: docker compose --profile with-db logs db"
            exit 1
        fi
        sleep 2
    done
    log_info "数据库已就绪"
fi

log_step "构建并启动应用服务..."
"${COMPOSE_CMD[@]}" up --build

