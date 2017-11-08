ALTER TABLE `mm_quote` 
ADD COLUMN `quote_operator_id` VARCHAR(32) NULL COMMENT '������ID\n�����˻��ߴ�����ʱ���������汨���˽��б��۵���������������Ҫ���ֶμ�¼��������Ϣ����ͨ����ʱ�������˾��Ǳ����ˡ�' AFTER `quote_user_id`;

ALTER TABLE `user_preference` 
CHANGE COLUMN `preference_value` `preference_value` VARCHAR(50000) NULL DEFAULT NULL COMMENT 'ƫ����ֵ��������Json��ֵ�������κ�Ӧ�ö���Ŀɳ־û�����' ;

ALTER TABLE mm_quote
DROP INDEX `IDX_PROV`,
DROP INDEX `IDX_BANK_NATURE`;

ALTER TABLE `mm_quote` 
ADD INDEX `IDX_PROV` (`direction` ASC, `province` ASC, `last_update_time` DESC),
ADD INDEX `IDX_BANKN` (`direction` ASC, `bank_nature` ASC,`last_update_time` DESC),
ADD INDEX `IDX_FUNDS` (`direction` ASC, `fund_size` ASC,`last_update_time` DESC),
ADD INDEX `IDX_DIR` (`direction` ASC, `last_update_time` DESC)
;