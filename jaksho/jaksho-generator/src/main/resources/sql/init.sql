CREATE TABLE `sr_biz_field_type`
(
    `id`               int(11)      NOT NULL AUTO_INCREMENT COMMENT '数据库主键id',
    `name`             varchar(100) NOT NULL COMMENT '业务字段类型名称',
    `description`      varchar(255) NOT NULL COMMENT '业务字段类型描述',
    `minimum`          int(11)      NOT NULL COMMENT '最小值或最小长度。当基础字段类型为数值类型的时候，设定其取值范围的最小值；当基础字段类型为字符串类型的时候，设定其最小长度',
    `maximum`          int(11)      NOT NULL COMMENT '最大值或最大长度。当基础字段类型为数值类型的时候，设定其取值范围的最大值；当基础字段类型为字符串类型的时候，设定其最大长度',
    `basic_field_type` varchar(20)  NOT NULL COMMENT '基础字段类型名称，取值范围如下：bool、byte、char、short、int、long、float、double、object、string',
    `create_time`      datetime     NOT NULL DEFAULT curtime() COMMENT '创建时间',
    `update_time`      datetime     NOT NULL DEFAULT curtime() COMMENT '更新时间',
    `remark`           varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
    `workspace_id`     int(11)      NOT NULL DEFAULT 0 COMMENT '工作空间数据库主键id',
    PRIMARY KEY (`id`)
);

CREATE TABLE `sr_object_biz_field_type_ref`
(
    `id`                int(11)  NOT NULL AUTO_INCREMENT COMMENT '数据库主键id',
    `biz_field_type_id` int(11)  NOT NULL COMMENT '该ref所属的业务字段类型数据库主键id（sr_biz_field_type数据库主键id）',
    `ref_id`            int(11)  NOT NULL DEFAULT '0' COMMENT '引用的基础字段id（sr_biz_field_type数据库主键id）',
    `parent_id`         int(11)  NOT NULL DEFAULT '0' COMMENT '父级id（sr_object_biz_field_type_ref数据库主键），0表示没有父级，也就是说该ref的字段是顶级字段，否则表示该ref的字段是子级字段',
    `order`             int(11)  NOT NULL DEFAULT '0' COMMENT '排序，值越小越靠前',
    `create_time`       datetime NOT NULL DEFAULT curtime() COMMENT '创建时间',
    `update_time`       datetime NOT NULL DEFAULT curtime() COMMENT '更新时间',
    `workspace_id`      int(11)  NOT NULL DEFAULT 0 COMMENT '工作空间数据库主键id',
    PRIMARY KEY (`id`)
);

CREATE TABLE sr_api_definition
(
    id                int AUTO_INCREMENT COMMENT '数据库主键ID' PRIMARY KEY,
    name              varchar(200)                   NOT NULL COMMENT '接口名称',
    method            varchar(10)                    NOT NULL COMMENT 'HTTP方法：GET,POST,PUT,DELETE,PATCH,HEAD,OPTIONS',
    path              varchar(500)                   NOT NULL COMMENT '接口路径，如: /users/{userId}',
    description       varchar(255) DEFAULT ''        NOT NULL COMMENT '接口描述',

    -- Body配置
    body_type         varchar(30)  DEFAULT 'NONE'    NOT NULL COMMENT '请求体类型：NONE,FORM_DATA,FORM_URLENCODED,RAW,BINARY',
    raw_type          varchar(20)  DEFAULT 'TEXT'    NOT NULL COMMENT 'raw类型的子类型：TEXT,JSON（当body_type=RAW时使用）',
    raw_text_field_id int          DEFAULT NULL COMMENT '当body_type=RAW且raw_type=TEXT时，指定对应的标准字段ID',

    workspace_id      int                            NOT NULL COMMENT '工作空间ID',
    create_time       datetime     DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    update_time       datetime     DEFAULT CURTIME() NOT NULL COMMENT '更新时间',
    remark            varchar(255) DEFAULT ''        NOT NULL COMMENT '备注',

    INDEX idx_workspace (workspace_id),
    INDEX idx_method_path (method, path(100)),
    INDEX idx_raw_text_field (raw_text_field_id)
) COMMENT '接口定义表';

CREATE TABLE sr_api_field
(
    id            int AUTO_INCREMENT COMMENT '数据库主键ID' PRIMARY KEY,
    api_id        int                            NOT NULL COMMENT '接口ID',
    field_type    varchar(20)                    NOT NULL COMMENT '字段类型：QUERY,PATH,FORM_DATA,FORM_URLENCODED,JSON,HEADER',
    biz_field_id  int                            NOT NULL COMMENT '标准字段ID，引用sr_biz_field.id',
    field_name    varchar(100)                   NOT NULL COMMENT '字段名称',

    -- 类型特有字段
    is_file_field tinyint      DEFAULT 0         NOT NULL COMMENT '是否为文件字段：0-否，1-是（仅FORM_DATA时有意义）',

    is_required   tinyint      DEFAULT 0         NOT NULL COMMENT '是否必填：0-否，1-是',
    description   varchar(255) DEFAULT ''        NOT NULL COMMENT '字段说明',
    sort_order    int          DEFAULT 0         NOT NULL COMMENT '在同一field_type内的排序序号',

    workspace_id  int                            NOT NULL COMMENT '工作空间ID',
    create_time   datetime     DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    update_time   datetime     DEFAULT CURTIME() NOT NULL COMMENT '更新时间',

    INDEX idx_api_type (api_id, field_type),
    INDEX idx_biz_field (biz_field_id),
    INDEX idx_sort (api_id, field_type, sort_order),
    INDEX idx_workspace (workspace_id),
    UNIQUE KEY uk_api_field_name (api_id, field_type, field_name)
) COMMENT '接口关联字段';