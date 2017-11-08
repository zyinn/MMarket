Drop Table mm_prime_contact_x;

CREATE TABLE IF NOT EXISTS `mm_prime_contact_x` (
  `institution_id` VARCHAR(32) NOT NULL COMMENT '机构ID，标明精品机构',
  `contact_id` VARCHAR(32) NOT NULL COMMENT '联系人ID，即为相应的QB用户ID。',
  `quote_attribute` CHAR(3) NOT NULL,
  PRIMARY KEY (`institution_id`, `contact_id`, `quote_attribute`))
ENGINE = InnoDB
COMMENT = '精品机构联系人关系表，维护精品机构及其所' /* comment truncated */ /*属联系人关系。*/