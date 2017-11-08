CREATE TABLE IF NOT EXISTS `mm_dict_tag` (
  `tag_code` CHAR(3) NOT NULL COMMENT '标签code。使用标签的数据需应用标签对应的code并记录。',
  `tag_name` VARCHAR(45) NULL COMMENT '标签显示名称。标签的中文显示内容。',
  `seq` INT(2) NULL COMMENT '用于排序的字段，前端显示需要按一定顺序进行。',
  `active` INT(1) NULL COMMENT '1：有效 0：无效',
  PRIMARY KEY (`tag_code`))
ENGINE = InnoDB
COMMENT = '标签定义表，所有报价使用的标签在此定义。';

CREATE TABLE IF NOT EXISTS `mm_quote_tags_x` (
  `quote_id` VARCHAR(32) NOT NULL COMMENT '引用标签的报价单主表id',
  `tag_code` CHAR(3) NOT NULL COMMENT '对应的标签code',
  PRIMARY KEY (`quote_id`, `tag_code`))
ENGINE = InnoDB
COMMENT = '报价单标签关系表。该表维护某报价单及其对应标签的关系。';

ALTER TABLE `mm_quote` 
ADD COLUMN `last_update_time` DATETIME NULL AFTER `expired_date`;

ALTER TABLE `mm_quote` 
ADD INDEX `IDX_INSTITUTION` (`institution_id` ASC);

ALTER TABLE `mm_quote` 
ADD INDEX `IDX_CREATETIME` (`create_time` ASC);

ALTER TABLE `mm_quote` 
ADD INDEX `IDX_EXPIREDTIME` (`expired_date` ASC);

