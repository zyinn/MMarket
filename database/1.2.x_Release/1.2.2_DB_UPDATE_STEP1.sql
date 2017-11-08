ALTER TABLE `mm_quote` 
ADD COLUMN `quote_operator_id` VARCHAR(32) NULL COMMENT '操作人ID\n当联盟或者代报价时，操作人替报价人进行报价单输入操作，因此需要该字段记录操作人信息。普通报价时，操作人就是报价人。' AFTER `quote_user_id`;

ALTER TABLE `user_preference` 
CHANGE COLUMN `preference_value` `preference_value` VARCHAR(50000) NULL DEFAULT NULL COMMENT '偏好数值：可能是Json数值，或者任何应用定义的可持久化数据' ;

ALTER TABLE mm_quote
DROP INDEX `IDX_PROV`,
DROP INDEX `IDX_BANK_NATURE`;

ALTER TABLE `mm_quote` 
ADD INDEX `IDX_PROV` (`direction` ASC, `province` ASC, `last_update_time` DESC),
ADD INDEX `IDX_BANKN` (`direction` ASC, `bank_nature` ASC,`last_update_time` DESC),
ADD INDEX `IDX_FUNDS` (`direction` ASC, `fund_size` ASC,`last_update_time` DESC),
ADD INDEX `IDX_DIR` (`direction` ASC, `last_update_time` DESC)
;