# Jaksho

Jaksho 是基于 Resilix 框架的 API 定义管理平台，提供业务字段、业务领域、值字典、接口定义、业务码等全生命周期管理能力。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.5.3 |
| 服务通信 | Apache Dubbo (Triple) | 3.3.6 |
| ORM | MyBatis-Plus | 3.5.12 |
| 数据库 | MariaDB | - |
| 前端框架 | Vue 3 + TypeScript | - |
| 构建工具 | Vite | 6.x |
| UI 组件库 | TDesign Vue Next | - |

## 环境要求

**本地开发：**

- **Java** 17+
- **Maven** 3.8+
- **Node.js** >= 18.18.0 (推荐 22.x)
- **npm** 10.x
- **MariaDB** 10.x / 11.x

**Docker 启动：**

- **Docker** 20.10+
- **Docker Compose** V2

---

## Docker 启动（推荐）

> 无需本地安装 Java / Node / MariaDB，Docker 环境即可运行。

1. 编辑 `docker-start.sh` 顶部的配置区域：

```bash
vi docker-start.sh
```

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `USE_BUILTIN_DB` | `true` | `true` = 自动启动 MariaDB 容器并初始化数据；`false` = 连接外部数据库 |
| `DB_PASSWORD` | `jaksho123` | 数据库密码（内置数据库时为 root 密码） |
| `DB_HOST` | 留空（内置时自动设为容器名） | 外部数据库地址（`USE_BUILTIN_DB=false` 时必填） |
| `DB_PORT` | `3306` | 数据库端口 |
| `DB_NAME` | `jaksho_new` | 数据库名称 |
| `DB_USER` | `root` | 数据库用户名 |
| `BACKEND_PORT` | `8080` | 后端映射到宿主机的端口 |
| `FRONTEND_PORT` | `3002` | 前端映射到宿主机的端口 |

2. 运行启动脚本：

```bash
./docker-start.sh
```

脚本会自动构建镜像并启动所有服务，内置数据库模式下会自动执行 SQL 初始化。按 `Ctrl+C` 停止所有服务。

启动成功后：

- 前端页面：`http://localhost:3002`
- 后端服务：`http://localhost:8080` (Dubbo Triple 协议)

**停止并清理：**

```bash
# 停止服务
docker compose --profile with-db down

# 停止服务并删除数据库数据卷（完全重置）
docker compose --profile with-db down -v
```

---

## 本地一键启动

> 适用于首次搭建或快速验证，需要先完成[数据库初始化](#1-初始化数据库)。

1. 编辑 `start.sh` 顶部的配置区域，至少修改 `DB_PASSWORD`：

```bash
vi start.sh
```

脚本顶部的可配置参数（均为可选，不填则使用 `application.yml` / `datasource.yaml` / `vite.config.ts` 中的默认值）：

| 参数 | 留空时的默认行为 | 说明 |
|------|------------------|------|
| `DB_PASSWORD` | 使用 datasource.yaml 配置 | 数据库密码 |
| `DB_HOST` | `localhost` | 数据库主机地址 |
| `DB_PORT` | `3306` | 数据库端口 |
| `DB_NAME` | `jaksho_new` | 数据库名称 |
| `DB_USER` | `root` | 数据库用户名 |
| `BACKEND_PORT` | `8080` | 后端服务端口（Dubbo Triple） |
| `FRONTEND_PORT` | `3002` | 前端开发服务器端口 |
| `RUN_NPM_INSTALL` | `true` | 启动前是否执行 `npm install` |
| `MVN_EXTRA_ARGS` | `-DskipTests` | Maven 额外参数 |

2. 运行启动脚本：

```bash
./start.sh
```

脚本会自动启动后端和前端，按 `Ctrl+C` 同时停止所有服务。

启动成功后：

- 后端服务：`http://localhost:8080` (Dubbo Triple 协议)
- 前端页面：`http://localhost:3002`

---

## Step by Step 启动指南

### 1. 初始化数据库

连接 MariaDB，创建数据库并执行初始化脚本：

```sql
CREATE DATABASE IF NOT EXISTS jaksho_new
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE jaksho_new;
```

按顺序执行以下 SQL 文件（位于 `jaksho-generator/src/main/resources/sql/`）：

```
init.sql                                    -- 核心表结构 + 预置数据
init-sr_biz_field_type.sql                  -- 业务字段类型预置数据
init-sr_biz_field.sql                       -- 业务字段预置数据
V1.0.3__add_response_template_support.sql   -- 响应模板支持（增量）
```

### 2. 启动后端

```bash
# 进入 jaksho 目录
cd jaksho

# 设置数据库密码环境变量
export DB_PASSWORD=your_db_password

# 先编译所有依赖模块（首次会下载依赖，需要几分钟）
mvn -pl jaksho -am compile -DskipTests

# 进入启动模块目录，运行应用
cd jaksho
mvn spring-boot:run
```

看到以下日志表示后端启动成功：

```
Dubbo triple server started on port 8080
```

> **提示**：也可以在 IDE 中直接运行 `top.huzz.jaksho.JakshoApplication` 的 `main` 方法，
> 需要在运行配置中添加环境变量 `DB_PASSWORD`。

### 3. 启动前端

```bash
# 进入前端目录
cd jaksho-fe

# 安装依赖（首次启动或依赖更新后执行）
npm install

# 启动开发服务器
npm run dev
```

看到以下输出表示前端启动成功：

```
  VITE v6.x.x  ready in xxx ms

  ➜  Local:   http://localhost:3002/
```

打开浏览器访问 `http://localhost:3002` 即可。

前端开发服务器已配置 API 代理，`/api/*` 请求会自动转发到后端 `http://127.0.0.1:8080`。

### 4. 验证服务

可以使用 `http-request/` 目录下的 `.http` 文件进行接口测试（支持 IntelliJ HTTP Client）。

---

## 项目结构

```
jaksho/
├── jaksho/                 # 主启动模块（Spring Boot 应用入口）
├── jaksho-api/             # API 层（Dubbo Service 接口、DTO、领域服务）
├── jaksho-domain/          # 领域层（实体、MyBatis Mapper XML、数据源配置）
├── jaksho-common/          # 公共工具模块
├── jaksho-generator/       # 代码生成器（含数据库初始化 SQL）
├── jaksho-fe/              # 前端应用（Vue 3 + Vite + TDesign）
├── docker/                 # Docker 相关文件
│   ├── backend.Dockerfile  #   后端镜像构建
│   ├── frontend.Dockerfile #   前端镜像构建（Nginx 托管）
│   ├── nginx.conf          #   Nginx 配置（API 代理 + SPA 路由）
│   └── initdb/             #   数据库初始化脚本
├── docker-compose.yml      # Docker Compose 编排
├── docker-start.sh         # Docker 一键启动脚本
├── start.sh                # 本地一键启动脚本
├── http-request/           # HTTP 接口测试文件
└── pom.xml                 # Maven 父 POM
```

## 常用命令

| 用途 | 命令 |
|------|------|
| 后端编译 | `mvn -pl jaksho -am compile` |
| 后端启动 | `mvn -pl jaksho -am compile && cd jaksho && mvn spring-boot:run` |
| 后端打包 | `mvn -pl jaksho -am package` |
| 前端安装依赖 | `cd jaksho-fe && npm install` |
| 前端开发 | `cd jaksho-fe && npm run dev` |
| 前端构建 | `cd jaksho-fe && npm run build` |
| 前端 Lint | `cd jaksho-fe && npm run lint:fix` |

## 配置说明

### 数据库连接

数据源配置位于 `jaksho-domain/src/main/resources/datasource.yaml`：

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| URL | `jdbc:mariadb://localhost:3306/jaksho_new` | 数据库连接地址 |
| 用户名 | `root` | 数据库用户名 |
| 密码 | `${DB_PASSWORD}` | 通过环境变量设置 |

### 前端环境变量

位于 `jaksho-fe/.env`：

| 变量 | 说明 |
|------|------|
| `VITE_API_URL_PREFIX` | API 路径前缀（默认 `/api`） |
| `VITE_IS_REQUEST_PROXY` | 是否启用代理（开发环境建议 `true`） |

## FAQ

**Q: 后端启动报数据库连接错误？**
确认 MariaDB 已启动，数据库 `jaksho_new` 已创建，并且 `DB_PASSWORD` 环境变量设置正确。

**Q: 前端页面空白或接口 404？**
确认后端已在 `8080` 端口启动，前端 Vite 代理会将 `/api/*` 请求转发到 `http://127.0.0.1:8080`。

**Q: Maven 构建失败提示找不到 `resilix` 依赖？**
Jaksho 依赖 Resilix 框架，需要先在项目根目录执行 `mvn install -DskipTests` 安装 Resilix 到本地仓库。
