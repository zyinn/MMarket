-- ����MemSQL���ص���ı������Ϊҵ����������ԭ����ID�ֶν�������
drop table `mm_quote` ;

CREATE TABLE `mm_quote` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `quote_type` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '��������:\n\nGTF: ���-���� : Guaranteed Fund\nUR2�����-�Ǳ���R2: Unguaranteed R2\nUR3�����-�Ǳ���R3: Unguaranteed R3\n\nIBD��ͬҵ��� Inter-Bank Deposit' ,
  `direction` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'OUT\nIN' ,
  `quote_user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '������QB ID ���� QQ code\n' ,
  `memo` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '��ע' ,
  `source` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '��Դ�� QQ ����QB����\nQQ\nQB' ,
  `create_time` datetime DEFAULT NULL COMMENT '��������' ,
  PRIMARY KEY (`quote_type`,`quote_user_id`,`create_time`),
  KEY `IDX_ID` (`id`),
  KEY `IDX_QUOTE_TYPE` (`quote_type`),
  KEY `IDX_USER_INS` (`quote_user_id`),
  KEY `IDX_TIME` (`create_time`)
);

-- ����MemSQL���ص���ı������Ϊҵ����������ԭ����ID�ֶν�������
drop table  `mm_quote_details` ;

CREATE TABLE `mm_quote_details` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `quote_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `limit_type` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'SEVD: 7��\nFTND��14��\nONEM��1��\nTWOM��2��\nTHRM��3��\nSIXM��6��\nONEY��1��' ,
  `price` decimal(10,4) DEFAULT NULL COMMENT '�۸�' ,
  `price_low` decimal(10,4) DEFAULT NULL COMMENT '�۸�������' ,
  `quantity` decimal(12,4) DEFAULT NULL COMMENT '����' ,
  `quantity_low` decimal(12,4) DEFAULT NULL COMMENT '����������' ,
  `last_update_time` datetime DEFAULT NULL,
  `active` int(1) DEFAULT 1,
  `days_low` int(5) DEFAULT NULL COMMENT '����������' ,
  `days_high` int(5) DEFAULT NULL COMMENT '���������䣬�����¼' ,
  `qq_message` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'Original message from QQ' ,
  PRIMARY KEY (`quote_id`,`days_low`,`days_high`),
  KEY `IDX_ID` (`id`),
  KEY `IDX_QUTID` (`quote_id`),
  KEY `IDX_LIMIT` (`limit_type`)
);
