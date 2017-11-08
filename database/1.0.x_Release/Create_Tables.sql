CREATE TABLE IF NOT EXISTS `mm_quote` (
  `id` VARCHAR(32) NOT NULL,
  `quote_type` CHAR(3) NULL COMMENT '报价类型:\n\nGTF: 理财-保本 : Guaranteed Fund\nUR2：理财-非保本R2: Unguaranteed R2\nUR3：理财-非保本R3: Unguaranteed R3\n\nIBD：同业存款 Inter-Bank Deposit',
  `direction` CHAR(3) NULL COMMENT 'OUT\nIN',
  `institution_id` VARCHAR(32) NULL COMMENT '报价机构id，用于支持用户为联盟机构报价',
  `quote_user_id` VARCHAR(32) NULL COMMENT '报价人QB ID 或者 QQ code\n',
  `memo` VARCHAR(512) NULL COMMENT '备注',
  `source` CHAR(3) NULL COMMENT '来源： QQ 或者QB报价\nQQ\nQB',
  `create_time` DATETIME NULL COMMENT '报价日期',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_UNIQUE` (`quote_type` ASC, `institution_id` ASC, `quote_user_id` ASC, `create_time` ASC))
ENGINE = InnoDB
COMMENT = '报价单表，记录了报价单详细信息\n';

CREATE TABLE IF NOT EXISTS `mm_quote_details` (
  `id` VARCHAR(32) NOT NULL,
  `quote_id` VARCHAR(32) NULL,
  `days_low` INT NULL COMMENT '期限下区间，按天记录',
  `days_high` INT NULL COMMENT '期限上区间，按天记录',
  `price` DECIMAL(10,4) NULL COMMENT '价格上区间',
  `price_low` DECIMAL(10,4) NULL COMMENT '价格下区间',
  `quantity` DECIMAL(16,4) NULL COMMENT '数量上区间',
  `quantity_low` DECIMAL(16,4) NULL COMMENT '数量下区间',
  `last_update_time` DATETIME NULL COMMENT '最后一次修改时间',
  `qq_message` VARCHAR(1024) NULL COMMENT 'Original message from QQ',
  `active` INT(1) NULL DEFAULT 1 COMMENT '1 active\n0 not_active',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_UNIQUE` (`quote_id` ASC, `days_low` ASC, `days_high` ASC))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `mm_prime_institution` (
  `id` VARCHAR(32) NOT NULL,
  `institution_id` VARCHAR(32) NULL,
  `telephone` VARCHAR(45) NULL,
  `active` INT(1) NULL COMMENT '0: not active\n1: active',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_UNIQUE` (`institution_id` ASC))
ENGINE = InnoDB
COMMENT = '精品机构表，记录了精品机构的联系电话信息';

CREATE TABLE IF NOT EXISTS `user_preference` (
  `id` VARCHAR(32) NOT NULL,
  `user_id` VARCHAR(32) NULL COMMENT '用户ID',
  `preference_type` CHAR(20) NULL COMMENT '偏好类型：偏好类型\n	\nAREA: 地域偏好\nLASTMMQUOTE：上次报价',
  `preference_value` VARCHAR(2048) NULL COMMENT '偏好数值：可能是Json数值，或者任何应用定义的可持久化数据',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_UNIQUE` (`user_id` ASC, `preference_type` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '用户偏好表，根据用户记录使用偏好。偏好的' /* comment truncated */ /*表现根据应用服务决定，可能是一个Json代表的Java类，或者任意业务服务确定的可持久化对象。
目前有1个偏好记录：
1.报价查询地域偏好：记录用户用于页面显示的5个地域*/;
