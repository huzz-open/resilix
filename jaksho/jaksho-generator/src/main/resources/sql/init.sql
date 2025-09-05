CREATE TABLE IF NOT EXISTS `sr_biz_field`
(
    `id`                int(11) AUTO_INCREMENT         NOT NULL COMMENT '数据库主键ID',
    `name`              varchar(100)                   NOT NULL COMMENT '业务字段名称',
    `description`       varchar(255) DEFAULT ''        NOT NULL COMMENT '业务字段描述',
    `biz_field_type_id` int(11)                        NOT NULL COMMENT '业务字段的业务字段类型id（sr_biz_field_type数据库主键id）',
    `create_time`       datetime     DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    `update_time`       datetime     DEFAULT CURTIME() NOT NULL COMMENT '更新时间',
    `workspace_id`      int(11)                        NOT NULL COMMENT '工作空间ID',
    PRIMARY KEY (`id`),
    UNIQUE uk_biz_field_name (`workspace_id`, `name`)
) COMMENT '业务字段';

CREATE TABLE IF NOT EXISTS `sr_biz_domain`
(
    `id`           int(11) AUTO_INCREMENT         NOT NULL COMMENT '数据库主键ID',
    `name`         varchar(100)                   NOT NULL COMMENT '业务领域名称',
    `description`  varchar(255) DEFAULT ''        NOT NULL COMMENT '业务领域描述描述',
    `create_time`  datetime     DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    `update_time`  datetime     DEFAULT CURTIME() NOT NULL COMMENT '更新时间',
    `workspace_id` int(11)                        NOT NULL COMMENT '工作空间ID',
    PRIMARY KEY (`id`),
    UNIQUE uk_biz_domain_name (`workspace_id`, `name`)
) COMMENT '业务领域';

CREATE TABLE IF NOT EXISTS `sr_biz_field_domain`
(
    `id`                int(11) AUTO_INCREMENT     NOT NULL COMMENT '数据库主键ID',
    `biz_field_id`      int(11)                    NOT NULL COMMENT '业务字段id（sr_biz_field数据库主键id）',
    `biz_domain_id`     int(11)                    NOT NULL COMMENT '业务领域id（sr_biz_domain数据库主键id）',
    `biz_field_type_id` int(11)                    NOT NULL COMMENT '业务字段类型id（sr_biz_field_type数据库主键id）。业务领域字段可以拥有新的业务字段类型',
    `create_time`       datetime DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    `update_time`       datetime DEFAULT CURTIME() NOT NULL COMMENT '更新时间',
    `workspace_id`      int(11)                    NOT NULL COMMENT '工作空间ID',
    PRIMARY KEY (`id`),
    UNIQUE uk_biz_field_domain (`workspace_id`, `biz_field_id`, `biz_domain_id`, `biz_field_type_id`)
) COMMENT '业务字段领域';

CREATE TABLE IF NOT EXISTS `sr_biz_field_type`
(
    `id`               int(11)                        NOT NULL AUTO_INCREMENT COMMENT '数据库主键id',
    `name`             varchar(100)                   NOT NULL COMMENT '业务字段类型名称',
    `description`      varchar(255) DEFAULT ''        NOT NULL COMMENT '业务字段类型描述',

    `minimum`          int(11)                        NULL COMMENT '最小值或最小长度。在基础字段类型取不同的值的时候有不同的含义。
     * <p/>当{@link #collectionType}为{@link CollectionType#NONE}时，表示该字段不为集合类型，取值含义如下：
     * <li/>数值类型：设定其取值范围的最小值；
     * <li/>string：设定其最小长度
     * <li/>file：设定文件最小大小
     * <p/>反之，如果该字段为集合类型，则该值表示集合中元素的最小个数。',
    `maximum`          int(11)                        NULL COMMENT '最大值或最大长度。在基础字段类型取不同的值的时候有不同的含义。
     * <p/>当{@link #collectionType}为{@link CollectionType#NONE}时，表示该字段不为集合类型，取值含义如下：
     * <li/>数值类型：设定其取值范围的最大值；
     * <li/>string：设定其最大长度
     * <li/>file：设定文件最大大小
     * <p/>反之，如果该字段为集合类型，则该值表示集合中元素的最大个数。',
    `collection_type`  varchar(10)  DEFAULT 'NONE'    NOT NULL COMMENT '集合类型',
    `basic_field_type` varchar(20)                    NOT NULL COMMENT '基础字段类型名称',
    `create_time`      datetime     DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    `update_time`      datetime     DEFAULT CURTIME() NOT NULL COMMENT '更新时间',
    `workspace_id`     int(11)      DEFAULT 0         NOT NULL COMMENT '工作空间数据库主键id',
    PRIMARY KEY (`id`)
) COMMENT '业务字段类型';

CREATE TABLE IF NOT EXISTS `sr_object_biz_field_type_ref`
(
    `id`                  int(11) AUTO_INCREMENT        NOT NULL COMMENT '数据库主键id',
    `ulid`                varchar(32)                   NOT NULL COMMENT '对象唯一标识符',
    `parent_ulid`         varchar(32) DEFAULT ''        NOT NULL COMMENT '父对象唯一标识符，根对象该值为空字符串',
    `sort_order`          int(11)     DEFAULT 0         NOT NULL COMMENT '在同一层级内的排序序号，值越小越靠前，从0开始',
    `biz_field_type_id`   int(11)                       NOT NULL COMMENT '该ref所属的业务字段类型数据库主键id（sr_biz_field_type数据库主键id）',
    `biz_field_domain_id` int(11)     DEFAULT 0         NOT NULL COMMENT '标准字段ID，引用sr_biz_field_domain.id',
    `create_time`         datetime    DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    `update_time`         datetime    DEFAULT CURTIME() NOT NULL COMMENT '更新时间',
    `workspace_id`        int(11)     DEFAULT 0         NOT NULL COMMENT '工作空间数据库主键id',
    INDEX idx_field_type_id_field_domain_id (`workspace_id`, `biz_field_type_id`, `biz_field_domain_id`),
    PRIMARY KEY (`id`)
) COMMENT '对象业务字段类型引用表';

CREATE TABLE IF NOT EXISTS sr_api_definition
(
    `id`                    int(11) AUTO_INCREMENT         NOT NULL COMMENT '数据库主键ID',
    `name`                  varchar(100)                   NOT NULL COMMENT '接口名称',
    `method`                varchar(10)                    NOT NULL COMMENT 'HTTP方法',
    `path`                  varchar(200)                   NOT NULL COMMENT '接口路径，如: /users/{userId}',
    `description`           varchar(255) DEFAULT ''        NOT NULL COMMENT '接口描述',
    `body_type`             varchar(30)  DEFAULT 'NONE'    NOT NULL COMMENT '请求体类型',
    `raw_type`              varchar(20)  DEFAULT 'TEXT'    NOT NULL COMMENT 'raw类型的子类型，当body_type=RAW起作用',
    `raw_biz_field_type_id` int(11)      DEFAULT -1        NOT NULL COMMENT '当body_type=RAW且raw_type=TEXT时，指定对应的业务字段类型id',
    `workspace_id`          int(11)                        NOT NULL COMMENT '工作空间ID',
    `create_time`           datetime     DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    `update_time`           datetime     DEFAULT CURTIME() NOT NULL COMMENT '更新时间',
    `remark`                varchar(255) DEFAULT ''        NOT NULL COMMENT '备注',

    PRIMARY KEY (`id`)
) COMMENT '接口定义表';

CREATE TABLE IF NOT EXISTS sr_api_definition_field
(
    `id`                  int(11) AUTO_INCREMENT         NOT NULL COMMENT '数据库主键ID',
    `api_id`              int(11)                        NOT NULL COMMENT '接口ID',
    `ulid`                varchar(32)                    NOT NULL COMMENT '字段唯一标识符',
    `parent_ulid`         varchar(32)  DEFAULT ''        NOT NULL COMMENT '父字段唯一标识符，只有field_type为JSON的时候，父字段才起作用，用于表示字段的层级关系，根字段该值为空字符串',
    `sort_order`          int(11)      DEFAULT 0         NOT NULL COMMENT '在同一field_type内的排序序号，值越小越靠前，从0开始',
    `field_type`          varchar(20)                    NOT NULL COMMENT '字段类型',
    `biz_field_domain_id` int(11)                        NOT NULL COMMENT '标准字段ID，引用sr_biz_field_domain.id',
    `is_required`         boolean      DEFAULT 0         NOT NULL COMMENT '是否必填：0-否，1-是',
    `description`         varchar(255) DEFAULT ''        NOT NULL COMMENT '字段说明',
    `workspace_id`        int(11)                        NOT NULL COMMENT '工作空间ID',
    `create_time`         datetime     DEFAULT CURTIME() NOT NULL COMMENT '创建时间',
    `update_time`         datetime     DEFAULT CURTIME() NOT NULL COMMENT '更新时间',

    PRIMARY KEY (`id`)
) COMMENT '接口关联字段';