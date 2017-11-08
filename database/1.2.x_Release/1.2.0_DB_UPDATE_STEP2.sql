CREATE TABLE IF NOT EXISTS `mm_dict_tag` (
  `tag_code` CHAR(3) NOT NULL COMMENT '��ǩcode��ʹ�ñ�ǩ��������Ӧ�ñ�ǩ��Ӧ��code����¼��',
  `tag_name` VARCHAR(45) NULL COMMENT '��ǩ��ʾ���ơ���ǩ��������ʾ���ݡ�',
  `seq` INT(2) NULL COMMENT '����������ֶΣ�ǰ����ʾ��Ҫ��һ��˳����С�',
  `active` INT(1) NULL COMMENT '1����Ч 0����Ч',
  PRIMARY KEY (`tag_code`))
ENGINE = InnoDB
COMMENT = '��ǩ��������б���ʹ�õı�ǩ�ڴ˶��塣';

CREATE TABLE IF NOT EXISTS `mm_quote_tags_x` (
  `quote_id` VARCHAR(32) NOT NULL COMMENT '���ñ�ǩ�ı��۵�����id',
  `tag_code` CHAR(3) NOT NULL COMMENT '��Ӧ�ı�ǩcode',
  PRIMARY KEY (`quote_id`, `tag_code`))
ENGINE = InnoDB
COMMENT = '���۵���ǩ��ϵ���ñ�ά��ĳ���۵������Ӧ��ǩ�Ĺ�ϵ��';

ALTER TABLE `mm_quote` 
ADD COLUMN `last_update_time` DATETIME NULL AFTER `expired_date`;

ALTER TABLE `mm_quote` 
ADD INDEX `IDX_INSTITUTION` (`institution_id` ASC);

ALTER TABLE `mm_quote` 
ADD INDEX `IDX_CREATETIME` (`create_time` ASC);

ALTER TABLE `mm_quote` 
ADD INDEX `IDX_EXPIREDTIME` (`expired_date` ASC);

