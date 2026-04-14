#!/usr/bin/env bash
#
# Jaksho Docker 一键启动脚本
# 动态生成 docker-compose.yml 并启动前端 + 后端，可选内置数据库
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

COMPOSE_FILE="$SCRIPT_DIR/docker-compose.generated.yml"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC}  $*"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
log_error() { echo -e "${RED}[ERROR]${NC} $*"; }
log_step()  { echo -e "${CYAN}[STEP]${NC}  $*"; }

cleanup() {
    echo ""
    log_info "正在停止所有服务..."
    docker-compose -f "$COMPOSE_FILE" down 2>/dev/null || true
    log_info "所有服务已停止，再见！"
    exit 0
}

trap cleanup SIGINT SIGTERM

# --- 前置检查 ---
log_step "检查运行环境..."
if ! command -v docker &>/dev/null; then
    log_error "未找到 docker，请先安装 Docker"
    exit 1
fi
if ! command -v docker-compose &>/dev/null; then
    log_error "未找到 docker-compose，请先安装 Docker Compose"
    exit 1
fi
log_info "Docker $(docker --version | awk '{print $3}' | tr -d ',')"
log_info "Compose $(docker-compose version --short)"

# --- 组装环境变量 ---
if [[ "$USE_BUILTIN_DB" == true ]]; then
    DB_HOST="db"
    log_info "使用内置数据库，数据将自动初始化"
else
    if [[ -z "$DB_HOST" ]]; then
        log_error "使用外部数据库时必须设置 DB_HOST"
        exit 1
    fi
    log_info "使用外部数据库: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
fi

JDBC_URL="jdbc:mariadb://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useInformationSchema=true"

# --- 生成 docker-compose.yml ---
log_step "生成 docker-compose.yml ..."

DB_SECTION=""
if [[ "$USE_BUILTIN_DB" == true ]]; then
DB_SECTION="
  db:
    image: mariadb:11
    environment:
      MARIADB_ROOT_PASSWORD: \"${DB_PASSWORD}\"
      MARIADB_DATABASE: \"${DB_NAME}\"
    volumes:
      - db-data:/var/lib/mysql
      - ./jaksho-generator/src/main/resources/sql:/sql:ro
      - ./docker/initdb:/docker-entrypoint-initdb.d:ro
"
fi

cat > "$COMPOSE_FILE" <<EOF
version: "2"

services:
${DB_SECTION}
  backend:
    build:
      context: ..
      dockerfile: jaksho/docker/backend.Dockerfile
    environment:
      DB_PASSWORD: "${DB_PASSWORD}"
      SPRING_DATASOURCE_URL: "${JDBC_URL}"
      SPRING_DATASOURCE_USERNAME: "${DB_USER}"
    ports:
      - "${BACKEND_PORT}:8080"
    restart: unless-stopped

  frontend:
    build:
      context: .
      dockerfile: docker/frontend.Dockerfile
    ports:
      - "${FRONTEND_PORT}:80"
    depends_on:
      - backend

volumes:
  db-data:
EOF

log_info "已生成 $COMPOSE_FILE"

# --- 启动服务 ---
if [[ "$USE_BUILTIN_DB" == true ]]; then
    log_step "启动数据库..."
    docker-compose -f "$COMPOSE_FILE" up -d db

    log_info "等待数据库就绪..."
    RETRIES=0
    MAX_RETRIES=30
    until docker-compose -f "$COMPOSE_FILE" exec -T db mariadb -u root -p"${DB_PASSWORD}" -e "SELECT 1" &>/dev/null; do
        RETRIES=$((RETRIES + 1))
        if [[ $RETRIES -ge $MAX_RETRIES ]]; then
            log_error "数据库启动超时，请检查日志: docker-compose -f $COMPOSE_FILE logs db"
            exit 1
        fi
        sleep 2
    done
    log_info "数据库已就绪"
fi

log_step "构建并启动应用服务..."

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Jaksho Docker 启动${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "  前端:  http://localhost:${FRONTEND_PORT}"
echo -e "  后端:  http://localhost:${BACKEND_PORT}"
echo -e "  按 ${YELLOW}Ctrl+C${NC} 停止所有服务"
echo -e "${GREEN}========================================${NC}"
echo ""

docker-compose -f "$COMPOSE_FILE" up --build backend frontend
