-- 由于MemSQL的特点更改表的主键为业务主键，在原主键ID字段建立索引
drop table `mm_quote` ;

CREATE TABLE `mm_quote` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `quote_type` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '报价类型:\n\nGTF: 理财-保本 : Guaranteed Fund\nUR2：理财-非保本R2: Unguaranteed R2\nUR3：理财-非保本R3: Unguaranteed R3\n\nIBD：同业存款 Inter-Bank Deposit' ,
  `direction` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'OUT\nIN' ,
  `quote_user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '报价人QB ID 或者 QQ code\n' ,
  `memo` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注' ,
  `source` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源： QQ 或者QB报价\nQQ\nQB' ,
  `create_time` datetime DEFAULT NULL COMMENT '报价日期' ,
  PRIMARY KEY (`quote_type`,`quote_user_id`,`create_time`),
  KEY `IDX_ID` (`id`),
  KEY `IDX_QUOTE_TYPE` (`quote_type`),
  KEY `IDX_USER_INS` (`quote_user_id`),
  KEY `IDX_TIME` (`create_time`)
);

-- 由于MemSQL的特点更改表的主键为业务主键，在原主键ID字段建立索引
drop table  `mm_quote_details` ;

CREATE TABLE `mm_quote_details` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `quote_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `limit_type` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'SEVD: 7天\nFTND：14天\nONEM：1月\nTWOM：2月\nTHRM：3月\nSIXM：6月\nONEY：1年' ,
  `price` decimal(10,4) DEFAULT NULL COMMENT '价格' ,
  `price_low` decimal(10,4) DEFAULT NULL COMMENT '价格上区间' ,
  `quantity` decimal(12,4) DEFAULT NULL COMMENT '数量' ,
  `quantity_low` decimal(12,4) DEFAULT NULL COMMENT '数量上区间' ,
  `last_update_time` datetime DEFAULT NULL,
  `active` int(1) DEFAULT 1,
  `days_low` int(5) DEFAULT NULL COMMENT '期限上区间' ,
  `days_high` int(5) DEFAULT NULL COMMENT '期限下区间，按天记录' ,
  `qq_message` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'Original message from QQ' ,
  PRIMARY KEY (`quote_id`,`days_low`,`days_high`),
  KEY `IDX_ID` (`id`),
  KEY `IDX_QUTID` (`quote_id`),
  KEY `IDX_LIMIT` (`limit_type`)
);
