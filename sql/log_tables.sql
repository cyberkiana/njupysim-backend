-- ============================================================
-- 日志系统相关表结构（在 njupt_physim 库执行）
-- 说明：原参考DDL中的 eiwis_upi 前缀已去除，id 改为自增主键便于程序写入
-- ============================================================

-- 管理员事件管理（管理员操作 + 定时任务）
CREATE TABLE IF NOT EXISTS sys_log_root_event (
  id int NOT NULL AUTO_INCREMENT COMMENT 'id',
  begin_date datetime DEFAULT NULL COMMENT '开始时间',
  end_date datetime DEFAULT NULL COMMENT '结束时间',
  event_detail varchar(200) DEFAULT NULL COMMENT '事件内容',
  create_date datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_create_date (create_date)
) DEFAULT CHARSET=utf8mb4 COMMENT='管理员事件管理';

-- 系统异常日志表
CREATE TABLE IF NOT EXISTS sys_log_error (
  id bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  error_info text COMMENT '异常信息',
  creator bigint DEFAULT NULL COMMENT '创建者',
  create_date datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_create_date (create_date)
) DEFAULT CHARSET=utf8mb4 COMMENT='系统异常日志表';

-- 系统登录日志
CREATE TABLE IF NOT EXISTS sys_log_login (
  id bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  operation tinyint(3) unsigned DEFAULT NULL COMMENT '用户操作(0:用户登录; 1:用户退出)',
  status tinyint(3) unsigned NOT NULL COMMENT '状态(0:失败; 1:成功; 2:账号已锁定)',
  creator_name varchar(50) DEFAULT NULL COMMENT '用户名',
  create_date datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (id) USING BTREE,
  KEY idx_status (status) USING BTREE,
  KEY idx_create_date (create_date) USING BTREE
) DEFAULT CHARSET=utf8mb4 COMMENT='系统登录日志';

-- 系统操作日志（@OperationLog 注解写入）
CREATE TABLE IF NOT EXISTS sys_log_operation (
  id bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  operation varchar(50) DEFAULT NULL COMMENT '用户操作',
  request_uri varchar(200) DEFAULT NULL COMMENT '请求URI',
  request_method varchar(20) DEFAULT NULL COMMENT '请求方式',
  request_params text COMMENT '请求参数',
  request_time int(10) NOT NULL COMMENT '请求时长(毫秒)',
  ip varchar(32) DEFAULT NULL COMMENT '操作IP',
  status tinyint(3) unsigned NOT NULL COMMENT '状态(0:失败; 1:成功)',
  creator_name varchar(50) DEFAULT NULL COMMENT '用户名',
  creator bigint(20) DEFAULT NULL COMMENT '创建者',
  create_date datetime DEFAULT NULL COMMENT '创建时间',
  type varchar(1) DEFAULT NULL COMMENT '日志类型(0:系统事件 1:业务事件)',
  PRIMARY KEY (id),
  KEY idx_create_date (create_date)
) DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志';

-- IP黑名单表（替代 yml 固定黑名单，管理员可查看/解封）
CREATE TABLE IF NOT EXISTS sys_ip_blacklist (
  id bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  ip varchar(32) NOT NULL COMMENT 'IP地址(支持精确/通配符/CIDR)',
  reason varchar(200) DEFAULT NULL COMMENT '封禁原因',
  status tinyint(3) unsigned NOT NULL DEFAULT 1 COMMENT '状态(1:封禁中 0:已解封)',
  ban_time datetime DEFAULT NULL COMMENT '封禁时间',
  unban_time datetime DEFAULT NULL COMMENT '解封时间',
  operator varchar(50) DEFAULT NULL COMMENT '操作人(系统自动/管理员)',
  create_date datetime DEFAULT NULL COMMENT '创建时间',
  update_date datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_ip (ip),
  KEY idx_status (status)
) DEFAULT CHARSET=utf8mb4 COMMENT='IP黑名单';

-- reservations 表增加 isActive 列（当日此时段是否允许预约）
ALTER TABLE reservations ADD COLUMN is_active int NOT NULL DEFAULT 1 COMMENT '当日此时段是否允许预约(1:允许 0:禁止)';

-- 初始化未来15天（今天~今天+14）的预约时段数据，每24条/天，slot=0..23
-- 相当于把定时任务"欠下"的历史数据补齐，保证当前即可预约
INSERT INTO reservations (day, slot)
WITH RECURSIVE days AS (
  SELECT CURDATE() AS d
  UNION ALL
  SELECT d + INTERVAL 1 DAY FROM days WHERE d < CURDATE() + INTERVAL 14 DAY
), seq AS (
  SELECT 0 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 23
)
SELECT days.d, seq.n FROM days CROSS JOIN seq;
