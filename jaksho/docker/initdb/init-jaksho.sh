#!/bin/bash
set -e

SQL_DIR=/sql

echo ">>> Initializing jaksho database..."

docker_process_sql < "$SQL_DIR/init.sql"
docker_process_sql < "$SQL_DIR/init-sr_biz_field_type.sql"
docker_process_sql < "$SQL_DIR/init-sr_biz_field.sql"
docker_process_sql < "$SQL_DIR/V1.0.3__add_response_template_support.sql"

echo ">>> jaksho database initialized."
