CREATE TABLE IF NOT EXISTS `mm_prime_contact_x` (
  `institution_id` VARCHAR(32) NOT NULL COMMENT '����ID��������Ʒ����',
  `contact_id` VARCHAR(32) NOT NULL COMMENT '��ϵ��ID����Ϊ��Ӧ��QB�û�ID��',
  PRIMARY KEY (`institution_id`, `contact_id`))
ENGINE = InnoDB
COMMENT = '��Ʒ������ϵ�˹�ϵ��ά����Ʒ����������' /* comment truncated */ /*����ϵ�˹�ϵ��*/