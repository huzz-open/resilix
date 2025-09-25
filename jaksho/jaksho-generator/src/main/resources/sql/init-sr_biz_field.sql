-- 直接插入 sr_biz_field 与 sr_biz_field_domain 的初始化数据
-- 风格对齐：jaksho_new.sr_biz_field_type 中的显式 INSERT 形式
-- 注意：biz_field_type_id 来自已存在的 sr_biz_field_type（见 init-sr_biz_field_type.sql 中的 id）

-- ========== 用户/账号/认证 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (1, 'user_id', '用户ID（正整数）', 75, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (2, 'user_ulid', '用户ULID', 7, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (3, 'user_uuid', '用户UUID', 8, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (4, 'username', '用户名（5-50字符）', 5, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (5, 'password_hash', '密码密文（最大255）', 6, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (6, 'email', '邮箱（5-254字符）', 9, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (7, 'phone_cn', '中国手机号（含国家码）', 10, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (8, 'phone_intl', '国际电话（E.164）', 11, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (9, 'is_enabled', '是否启用', 2, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (10, 'is_deleted', '逻辑删除标记', 3, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (11, 'two_factor_enabled', '是否启用双因素认证', 1, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (12, 'two_factor_secret', '双因素密钥（20-64）', 12, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (13, 'last_login_at', '上次登录时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (14, 'password_updated_at', '密码更新时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (15, 'created_at', '创建时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (16, 'updated_at', '更新时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (17, 'failed_login_attempts', '连续失败登录次数（0-10）', 71, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (18, 'account_locked_until', '账户锁定截止（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (19, 'password_reset_token', '密码重置令牌（20-64）', 12, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (20, 'password_reset_expires_at', '密码重置过期（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (21, 'email_verified', '邮箱是否已验证', 1, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (22, 'phone_verified', '手机号是否已验证', 1, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (23, 'user_role', '用户角色标识（1-50）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (24, 'user_status', '用户状态标识（1-50）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (25, 'avatar_url', '头像URL（1-200）', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (26, 'bio', '个人简介（0-255）', 19, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (27, 'nickname', '昵称（1-20）', 16, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (28, 'full_name', '姓名（1-100）', 22, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (29, 'first_name', '名（1-50）', 20, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (30, 'last_name', '姓（1-50）', 21, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 地址/组织 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (31, 'address_line1', '地址行1（1-200）', 23, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (32, 'address_line2', '地址行2（1-200）', 23, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (33, 'city', '城市（1-50）', 24, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (34, 'province', '省/州（1-50）', 25, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (35, 'postal_code', '邮编（3-10）', 26, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (36, 'country_code', '国家代码（ISO 3166-1）', 44, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (37, 'company', '公司名称（1-100）', 27, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (38, 'department', '部门名称（1-50）', 28, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (39, 'position', '职位名称（1-50）', 29, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 网络/链接/编码 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (40, 'homepage', '个人主页URL', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (41, 'website', '个人网站URL', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (42, 'github_url', 'GitHub 个人链接', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (43, 'linkedin_url', 'LinkedIn 个人链接', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (44, 'twitter_url', 'Twitter 个人链接', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (45, 'facebook_url', 'Facebook 个人链接', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (46, 'color_hex', '头像主题色（#RRGGBB）', 39, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 通用标签/标识 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (47, 'tags', '标签（1-30）', 14, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (48, 'slug', '短标识（1-50）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 订单（核心） ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (49, 'order_id', '订单ID（正整数）', 75, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (50, 'order_ulid', '订单ULID', 7, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (51, 'order_number', '订单编号（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (52, 'order_status', '订单状态（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (53, 'customer_id', '客户ID（正整数）', 75, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (54, 'customer_email', '客户邮箱', 9, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (55, 'currency', '货币代码（ISO 4217）', 46, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (56, 'subtotal_cents', '小计（分）', 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (57, 'discount_cents', '折扣金额（分）', 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (58, 'shipping_cents', '运费（分）', 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (59, 'tax_cents', '税额（分）', 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (60, 'total_cents', '订单总金额（分）', 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (61, 'total_items', '总商品件数', 74, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (62, 'payment_status', '支付状态（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (63, 'payment_method', '支付方式（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (64, 'payment_provider', '支付渠道（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (65, 'payment_transaction_id', '支付交易号（20-64）', 12, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (66, 'shipping_method', '配送方式（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (67, 'shipping_status', '物流状态（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (68, 'shipping_tracking_number', '运单号（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (69, 'shipping_carrier', '承运商（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (70, 'shipped_at', '发货时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (71, 'delivered_at', '签收时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 产品/目录（少量示例，后续可继续补充） ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (72, 'product_id', '商品ID（正整数）', 75, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (73, 'sku', 'SKU（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (74, 'product_name', '商品名称（标题）', 17, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (75, 'price_cents', '售价（分）', 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (76, 'product_url', '商品URL', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== sr_biz_field_domain（为以上字段建立默认领域映射，biz_domain_id=-1） ==========
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (1, -1, 75, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (2, -1, 7, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (3, -1, 8, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (4, -1, 5, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (5, -1, 6, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (6, -1, 9, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (7, -1, 10, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (8, -1, 11, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (9, -1, 2, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (10, -1, 3, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (11, -1, 1, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (12, -1, 12, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (13, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (14, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (15, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (16, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (17, -1, 71, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (18, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (19, -1, 12, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (20, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (21, -1, 1, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (22, -1, 1, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (23, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (24, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (25, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (26, -1, 19, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (27, -1, 16, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (28, -1, 22, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (29, -1, 20, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (30, -1, 21, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (31, -1, 23, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (32, -1, 23, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (33, -1, 24, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (34, -1, 25, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (35, -1, 26, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (36, -1, 44, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (37, -1, 27, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (38, -1, 28, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (39, -1, 29, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (40, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (41, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (42, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (43, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (44, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (45, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (46, -1, 39, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (47, -1, 14, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (48, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (49, -1, 75, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (50, -1, 7, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (51, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (52, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (53, -1, 75, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (54, -1, 9, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (55, -1, 46, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (56, -1, 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (57, -1, 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (58, -1, 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (59, -1, 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (60, -1, 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (61, -1, 74, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (62, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (63, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (64, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (65, -1, 12, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (66, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (67, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (68, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (69, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (70, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (71, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (72, -1, 75, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (73, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (74, -1, 17, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (75, -1, 94, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (76, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 追加：订单扩展 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (77, 'order_source', '订单来源（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (78, 'order_channel', '订单渠道（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (79, 'utm_source', 'UTM Source（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (80, 'utm_medium', 'UTM Medium（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (81, 'utm_campaign', 'UTM Campaign（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (82, 'utm_term', 'UTM Term（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (83, 'utm_content', 'UTM Content（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (84, 'order_notes', '订单备注（描述）', 19, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (85, 'promo_code', '优惠码（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (86, 'is_gift', '是否礼品', 1, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (87, 'gift_message', '礼品留言（摘要）', 18, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (88, 'invoice_url', '发票URL', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (89, 'packing_slip_url', '装箱单URL', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (77, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (78, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (79, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (80, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (81, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (82, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (83, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (84, -1, 19, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (85, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (86, -1, 1, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (87, -1, 18, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (88, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (89, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 追加：支付 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (90, 'payment_id', '支付记录ID（短标识或外部号）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (91, 'payment_ulid', '支付记录ULID', 7, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (92, 'transaction_id', '交易号（20-64）', 12, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (93, 'payment_status_code', '支付状态（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (94, 'payment_method_code', '支付方式（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (95, 'payment_provider_code', '支付渠道（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (96, 'authorized_at', '授权时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (97, 'captured_at', '入账时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (98, 'refunded_at', '退款时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (99, 'refund_reason', '退款原因（描述）', 19, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (90, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (91, -1, 7, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (92, -1, 12, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (93, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (94, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (95, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (96, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (97, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (98, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (99, -1, 19, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 追加：商品/目录 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (100, 'spu', 'SPU（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (101, 'upc', 'UPC（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (102, 'ean', 'EAN（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (103, 'isbn', 'ISBN（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (104, 'brand', '品牌（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (105, 'category', '类目（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (106, 'product_tags', '商品标签（1-30）', 14, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (107, 'image_url', '主图URL', 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (108, 'gallery_urls', '图集URL数组', 117, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (109, 'seo_title', 'SEO 标题', 17, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (110, 'seo_description', 'SEO 描述', 19, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (111, 'product_slug', '商品URL标识', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (100, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (101, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (102, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (103, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (104, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (105, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (106, -1, 14, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (107, -1, 30, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (108, -1, 117, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (109, -1, 17, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (110, -1, 19, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (111, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 追加：库存/物流 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (112, 'warehouse_id', '仓库标识（短标识/外部号）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (113, 'bin_code', '库位编码（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (114, 'aisle', '通道（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (115, 'shelf', '货架（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (116, 'lot_number', '批次号（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (117, 'batch_number', '批号（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (118, 'serial_number', '序列号（短代码）', 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (119, 'shipment_tracking_no', '物流运单号（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (120, 'shipment_carrier_code', '承运商编码（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (121, 'shipment_status_code', '物流状态（短标识）', 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (122, 'shipment_shipped_at', '发货时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (123, 'shipment_delivered_at', '签收时间（秒级时间戳）', 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (112, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (113, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (114, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (115, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (116, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (117, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (118, -1, 15, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (119, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (120, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (121, -1, 13, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (122, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (123, -1, 77, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

-- ========== 追加：用户资料扩展 ==========
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (124, 'weixin_id', '微信号（6-20）', 52, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (125, 'id_card_cn', '中国身份证号', 48, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (126, 'social_credit_code', '统一社会信用代码', 49, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (127, 'license_plate_cn', '中国车牌号', 50, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field (id, name, description, biz_field_type_id, create_time, update_time, workspace_id) VALUES (128, 'bank_card_no', '银行卡号', 51, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);

INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (124, -1, 52, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (125, -1, 48, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (126, -1, 49, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (127, -1, 50, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);
INSERT INTO jaksho_new.sr_biz_field_domain (biz_field_id, biz_domain_id, biz_field_type_id, create_time, update_time, workspace_id) VALUES (128, -1, 51, '2025-09-23 12:13:03', '2025-09-23 12:13:03', 1);


