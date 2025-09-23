INSERT INTO `sr_biz_field_type`
(`name`, `description`, `minimum`, `maximum`, `collection_type`, `basic_field_type`, `create_time`, `update_time`, `workspace_id`)
VALUES

-- ========== NONE / BOOLEAN ==========
('通用布尔值', '通用布尔型：true 或 false', NULL, NULL, 'NONE', 'BOOLEAN', CURTIME(), CURTIME(), 1),
('是否启用', '标记资源是否启用', NULL, NULL, 'NONE', 'BOOLEAN', CURTIME(), CURTIME(), 1),
('是否删除', '逻辑删除标记', NULL, NULL, 'NONE', 'BOOLEAN', CURTIME(), CURTIME(), 1),
('是否必填', '是否为必填项', NULL, NULL, 'NONE', 'BOOLEAN', CURTIME(), CURTIME(), 1),

-- ========== NONE / STRING（标识/账号/认证） ==========
('Username 用户名', '用户名，长度 5-50 字符', 5, 50, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Password 密码（加密后的）', '密码密文，最大长度 255 字符', NULL, 255, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('ULID', 'ULID 唯一标识，固定 26 字符', 26, 26, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('UUID', 'UUID，固定 36 字符', 36, 36, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Email 邮箱', '电子邮箱地址，5-254 字符', 5, 254, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Phone 电话号码（中国）', '中国大陆手机号，固定 13 位（含国家码）', 13, 13, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Phone 电话号码（国际）', '国际 E.164 电话格式，1-15 位数字', 1, 15, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Access Token 访问令牌', '访问令牌，20-64 字符', 20, 64, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Slug 短标识', 'URL 友好短标识，1-50 字符', 1, 50, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Tag 标签', '标签，1-30 字符', 1, 30, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Code 短代码', '短代码，1-10 字符', 1, 10, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== NONE / STRING（人名/展示类） ==========
('Nickname 昵称', '昵称，1-20 字符', 1, 20, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Title 标题', '标题，1-100 字符', 1, 100, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Summary 摘要', '摘要，0-200 字符', 0, 200, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Description 描述', '描述信息，0-255 字符', 0, 255, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('First Name 名', '名，1-50 字符', 1, 50, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Last Name 姓', '姓，1-50 字符', 1, 50, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Full Name 姓名', '姓名，1-100 字符', 1, 100, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== NONE / STRING（地址/组织） ==========
('Address Line 地址行', '地址行，1-200 字符', 1, 200, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('City 城市', '城市名，1-50 字符', 1, 50, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Province 省州', '省/州，1-50 字符', 1, 50, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Zip Code 邮编', '邮政编码，3-10 字符', 3, 10, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Company 公司名称', '公司名称，1-100 字符', 1, 100, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Department 部门', '部门名称，1-50 字符', 1, 50, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Position 职位', '职位名称，1-50 字符', 1, 50, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== NONE / STRING（网络/链接） ==========
('URL 链接', 'URL 地址，1-200 字符', 1, 200, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('IPv4 地址', 'IPv4 地址字符串，7-15 字符', 7, 15, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('IPv6 地址', 'IPv6 地址字符串，15-39 字符', 15, 39, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('域名', '域名，1-253 字符', 1, 253, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('子域名', '子域名，1-63 字符', 1, 63, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('URL 路径', 'URL 路径，1-200 字符', 1, 200, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== NONE / STRING（编码/文件/校验） ==========
('文件名', '文件名，1-100 字符', 1, 100, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('文件扩展名', '文件扩展名，1-10 字符', 1, 10, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('MIME 类型', 'MIME 类型，3-100 字符', 3, 100, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('颜色十六进制', '颜色十六进制字符串（#RRGGBB），固定 7 字符', 7, 7, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('MD5', 'MD5 哈希，固定 32 字符', 32, 32, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('SHA1', 'SHA1 哈希，固定 40 字符', 40, 40, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('SHA256', 'SHA256 哈希，固定 64 字符', 64, 64, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('文件路径', '文件路径，1-255 字符', 1, 255, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== NONE / STRING（标准代码） ==========
('Country Code 国家代码', 'ISO 3166-1 alpha-2，固定 2 字符', 2, 2, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Language Code 语言代码', 'ISO 639-1，固定 2 字符', 2, 2, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Currency Code 货币代码', 'ISO 4217，固定 3 字符', 3, 3, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('时区 ID', '时区标识，1-32 字符', 1, 32, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('中国身份证号', '中国居民身份证号，固定 18 字符', 18, 18, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('社会统一信用代码', '统一社会信用代码，固定 18 字符', 18, 18, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('车牌号（中国）', '中国大陆机动车号牌，7-8 字符', 7, 8, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('银行卡号', '银行卡号，12-19 位', 12, 19, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('微信号', '微信帐号，6-20 字符', 6, 20, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== NONE / STRING（日期时间字符串） ==========
('Date 日期字符串', 'ISO 日期格式 yyyy-MM-dd，固定 10 字符', 10, 10, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Time 时间字符串', '时间格式 HH:mm:ss，固定 8 字符', 8, 8, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),
('Datetime 日期时间字符串', '日期时间 yyyy-MM-dd HH:mm:ss，固定 19 字符', 19, 19, 'NONE', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== NONE / INT8 ==========
('百分比（整数）', '百分比（整数），范围 0-100', 0, 100, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),
('评分（五级）', '评分（五级），范围 1-5', 1, 5, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),
('评分（十分）', '评分（十分），范围 1-10', 1, 10, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),
('月份', '月份，范围 1-12', 1, 12, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),
('日', '日期中的日，范围 1-31', 1, 31, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),
('小时', '小时，范围 0-23', 0, 23, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),
('分钟', '分钟，范围 0-59', 0, 59, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),
('秒', '秒，范围 0-59', 0, 59, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),
('年龄（整岁）', '年龄（整岁），范围 0-120', 0, 120, 'NONE', 'INT8', CURTIME(), CURTIME(), 1),

-- ========== NONE / INT16 ==========
('年份', '年份数字，范围 1900-2100', 1900, 2100, 'NONE', 'INT16', CURTIME(), CURTIME(), 1),
('分值（千分制）', '分值（0-1000）', 0, 1000, 'NONE', 'INT16', CURTIME(), CURTIME(), 1),
('温度（整数）', '温度（整数），范围 -100 至 100', -100, 100, 'NONE', 'INT16', CURTIME(), CURTIME(), 1),
('距离（米，整数）', '距离（米，整数），范围 0-10000', 0, 10000, 'NONE', 'INT16', CURTIME(), CURTIME(), 1),

-- ========== NONE / INT32 ==========
('页码', '页码，1-1000', 1, 1000, 'NONE', 'INT32', CURTIME(), CURTIME(), 1),
('分页大小', '分页大小，1-1000', 1, 1000, 'NONE', 'INT32', CURTIME(), CURTIME(), 1),
('重试次数', '重试次数，0-10', 0, 10, 'NONE', 'INT32', CURTIME(), CURTIME(), 1),
('超时时间（毫秒）', '超时时间（毫秒），0-600000', 0, 600000, 'NONE', 'INT32', CURTIME(), CURTIME(), 1),
('文件数量', '文件数量，0-1000', 0, 1000, 'NONE', 'INT32', CURTIME(), CURTIME(), 1),
('子项数量', '子项数量，0-50', 0, 50, 'NONE', 'INT32', CURTIME(), CURTIME(), 1),
('版本号', '版本号，0-10000', 0, 10000, 'NONE', 'INT32', CURTIME(), CURTIME(), 1),
('端口号', '网络端口，0-65535', 0, 65535, 'NONE', 'INT32', CURTIME(), CURTIME(), 1),

-- ========== NONE / INT64 ==========
('时间戳（秒）', 'Unix 时间戳（秒），0-2147483647', 0, NULL, 'NONE', 'INT64', CURTIME(), CURTIME(), 1),
('时间戳（毫秒）', 'Unix 时间戳（毫秒），非负', 0, NULL, 'NONE', 'INT64', CURTIME(), CURTIME(), 1),
('雪花ID', '雪花算法生成的 ID，正整数', 1, NULL, 'NONE', 'INT64', CURTIME(), CURTIME(), 1),

-- ========== NONE / FLOAT ==========
('比例（浮点）', '比例（浮点），0-1', 0, 1, 'NONE', 'FLOAT', CURTIME(), CURTIME(), 1),
('得分（浮点）', '得分（浮点），0-100', 0, 100, 'NONE', 'FLOAT', CURTIME(), CURTIME(), 1),
('温度（浮点）', '温度（浮点），-100 至 100', -100, 100, 'NONE', 'FLOAT', CURTIME(), CURTIME(), 1),
('重量（浮点）', '重量（浮点），0-1000', 0, 1000, 'NONE', 'FLOAT', CURTIME(), CURTIME(), 1),
('速度（浮点）', '速度（浮点），0-300', 0, 300, 'NONE', 'FLOAT', CURTIME(), CURTIME(), 1),
('进度（浮点）', '进度（浮点），0-100', 0, 100, 'NONE', 'FLOAT', CURTIME(), CURTIME(), 1),
('身高（厘米）', '身高（厘米），0-300', 0, 300, 'NONE', 'FLOAT', CURTIME(), CURTIME(), 1),
('宽度（厘米）', '宽度（厘米），0-300', 0, 300, 'NONE', 'FLOAT', CURTIME(), CURTIME(), 1),

-- ========== NONE / DOUBLE ==========
('比例（双精度）', '比例（双精度），0-1', 0, 1, 'NONE', 'DOUBLE', CURTIME(), CURTIME(), 1),
('得分（双精度）', '得分（双精度），0-100', 0, 100, 'NONE', 'DOUBLE', CURTIME(), CURTIME(), 1),
('温度（双精度）', '温度（双精度），-273 至 1000', -273, 1000, 'NONE', 'DOUBLE', CURTIME(), CURTIME(), 1),
('纬度', '纬度，-90 至 90', -90, 90, 'NONE', 'DOUBLE', CURTIME(), CURTIME(), 1),
('经度', '经度，-180 至 180', -180, 180, 'NONE', 'DOUBLE', CURTIME(), CURTIME(), 1),
('重量（双精度）', '重量（双精度），0-100000', 0, 100000, 'NONE', 'DOUBLE', CURTIME(), CURTIME(), 1),
('百分比（双精度）', '百分比（双精度），0-100', 0, 100, 'NONE', 'DOUBLE', CURTIME(), CURTIME(), 1),
('汇率（双精度）', '汇率（双精度），0-1000', 0, 1000, 'NONE', 'DOUBLE', CURTIME(), CURTIME(), 1),

-- ========== NONE / FILE ==========
('任意文件（小）', '任意文件，大小 0KB-512KB', 0, 512, 'NONE', 'FILE', CURTIME(), CURTIME(), 1),
('任意文件（中）', '任意文件，大小 0KB-1024KB', 0, 1024, 'NONE', 'FILE', CURTIME(), CURTIME(), 1),
('任意文件（较大）', '任意文件，大小 0KB-5120KB', 0, 5120, 'NONE', 'FILE', CURTIME(), CURTIME(), 1),
('任意文件（大）', '任意文件，大小 0KB-10240KB', 0, 10240, 'NONE', 'FILE', CURTIME(), CURTIME(), 1),
('图片文件（小）', '图片文件，大小 0KB-1024KB', 0, 1024, 'NONE', 'FILE', CURTIME(), CURTIME(), 1),
('文档文件（中）', '文档文件，大小 0KB-2048KB', 0, 2048, 'NONE', 'FILE', CURTIME(), CURTIME(), 1),

-- ========== NONE / OBJECT ==========
('通用对象', '通用对象（非集合）', NULL, NULL, 'NONE', 'OBJECT', CURTIME(), CURTIME(), 1),

-- ========== LIST / STRING ==========
('标签列表', '标签的列表（可重复），0-100 项', 0, 100, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('关键字列表', '关键字的列表（可重复），0-50 项', 0, 50, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('角色列表', '角色名称列表（可重复），0-50 项', 0, 50, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('权限列表', '权限标识列表（可重复），0-200 项', 0, 200, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('URL 列表', 'URL 列表（可重复），0-50 项', 0, 50, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('文件名列表', '文件名列表（可重复），0-100 项', 0, 100, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('邮箱列表', '邮箱地址列表（可重复），0-100 项', 0, 100, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('手机号列表', '手机号列表（可重复），0-100 项', 0, 100, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('UUID 列表', 'UUID 列表（可重复），0-100 项', 0, 100, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),
('ULID 列表', 'ULID 列表（可重复），0-100 项', 0, 100, 'LIST', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== SET / STRING ==========
('标签集合（唯一）', '标签的集合（唯一、无序），0-100 项', 0, 100, 'SET', 'STRING', CURTIME(), CURTIME(), 1),
('角色集合（唯一）', '角色名称集合（唯一、无序），0-50 项', 0, 50, 'SET', 'STRING', CURTIME(), CURTIME(), 1),
('权限集合（唯一）', '权限标识集合（唯一、无序），0-200 项', 0, 200, 'SET', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== ARRAY / STRING ==========
('字符串数组（通用）', '通用字符串数组（有序），0-50 项', 0, 50, 'ARRAY', 'STRING', CURTIME(), CURTIME(), 1),
('URL 数组', 'URL 数组（有序），0-50 项', 0, 50, 'ARRAY', 'STRING', CURTIME(), CURTIME(), 1),

-- ========== LIST / INT32 ==========
('ID 列表', '32位整型 ID 列表（可重复），0-100 项', 0, 100, 'LIST', 'INT32', CURTIME(), CURTIME(), 1),
('数量列表', '数量列表（可重复），0-100 项', 0, 100, 'LIST', 'INT32', CURTIME(), CURTIME(), 1),
('版本号列表', '版本号列表（可重复），0-100 项', 0, 100, 'LIST', 'INT32', CURTIME(), CURTIME(), 1),

-- ========== SET / INT32 ==========
('ID 集合（唯一）', '32位整型 ID 集合（唯一、无序），0-100 项', 0, 100, 'SET', 'INT32', CURTIME(), CURTIME(), 1),
('数量集合（唯一）', '数量集合（唯一、无序），0-100 项', 0, 100, 'SET', 'INT32', CURTIME(), CURTIME(), 1),

-- ========== ARRAY / INT32 ==========
('ID 数组', '32位整型 ID 数组（有序），0-100 项', 0, 100, 'ARRAY', 'INT32', CURTIME(), CURTIME(), 1),

-- ========== LIST / INT64 ==========
('资源ID 列表', '64位资源 ID 列表（可重复），0-100 项', 0, 100, 'LIST', 'INT64', CURTIME(), CURTIME(), 1),
('时间戳列表（秒）', '时间戳（秒）列表（可重复），0-100 项', 0, 100, 'LIST', 'INT64', CURTIME(), CURTIME(), 1),
('文件大小列表（字节）', '文件大小（字节）列表（可重复），0-100 项', 0, 100, 'LIST', 'INT64', CURTIME(), CURTIME(), 1),

-- ========== SET / INT64 ==========
('资源ID 集合（唯一）', '64位资源 ID 集合（唯一、无序），0-100 项', 0, 100, 'SET', 'INT64', CURTIME(), CURTIME(), 1),

-- ========== ARRAY / INT64 ==========
('资源ID 数组', '64位资源 ID 数组（有序），0-100 项', 0, 100, 'ARRAY', 'INT64', CURTIME(), CURTIME(), 1),

-- ========== LIST / FLOAT ==========
('测量值列表（浮点）', '测量值（浮点）列表（可重复），0-50 项', 0, 50, 'LIST', 'FLOAT', CURTIME(), CURTIME(), 1),
('进度列表（浮点）', '进度（浮点）列表（可重复），0-50 项', 0, 50, 'LIST', 'FLOAT', CURTIME(), CURTIME(), 1),

-- ========== ARRAY / FLOAT ==========
('测量值数组（浮点）', '测量值（浮点）数组（有序），0-50 项', 0, 50, 'ARRAY', 'FLOAT', CURTIME(), CURTIME(), 1),

-- ========== LIST / DOUBLE ==========
('测量值列表（双精度）', '测量值（双精度）列表（可重复），0-50 项', 0, 50, 'LIST', 'DOUBLE', CURTIME(), CURTIME(), 1),
('百分比列表（双精度）', '百分比（双精度）列表（可重复），0-50 项', 0, 50, 'LIST', 'DOUBLE', CURTIME(), CURTIME(), 1),

-- ========== ARRAY / DOUBLE ==========
('测量值数组（双精度）', '测量值（双精度）数组（有序），0-50 项', 0, 50, 'ARRAY', 'DOUBLE', CURTIME(), CURTIME(), 1),

-- ========== LIST / FILE ==========
('文件列表', '文件列表（可重复），0-50 项', 0, 50, 'LIST', 'FILE', CURTIME(), CURTIME(), 1),
('图片文件列表', '图片文件列表（可重复），0-50 项', 0, 50, 'LIST', 'FILE', CURTIME(), CURTIME(), 1),

-- ========== ARRAY / FILE ==========
('文件数组', '文件数组（有序），0-50 项', 0, 50, 'ARRAY', 'FILE', CURTIME(), CURTIME(), 1),

-- ========== LIST / OBJECT ==========
('对象列表', '对象列表（可重复），0-100 项', 0, 100, 'LIST', 'OBJECT', CURTIME(), CURTIME(), 1),
('子对象列表', '子对象列表（可重复），0-50 项', 0, 50, 'LIST', 'OBJECT', CURTIME(), CURTIME(), 1),

-- ========== SET / OBJECT ==========
('对象集合（唯一）', '对象集合（唯一、无序），0-50 项', 0, 50, 'SET', 'OBJECT', CURTIME(), CURTIME(), 1),

-- ========== ARRAY / OBJECT ==========
('对象数组', '对象数组（有序），0-50 项', 0, 50, 'ARRAY', 'OBJECT', CURTIME(), CURTIME(), 1);


