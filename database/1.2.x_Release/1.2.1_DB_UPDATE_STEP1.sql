CREATE TABLE IF NOT EXISTS `mm_quote_price_trends` (
  `id` VARCHAR(32) NOT NULL,
  `quote_type` CHAR(3) NOT NULL COMMENT '�������ͣ������mm_quote���Ӧ�ֶν���',
  `direction` CHAR(3) NOT NULL COMMENT '���������mm_quote���Ӧ�ֶν���',
  `matrix_bank_nature` CHAR(20) NOT NULL COMMENT '���ھ������Ļ�������ö�٣����硰�����С��������Ӧcode��CITY_COMMERCIAL_BANK������ö����ֵ��������ϵͳ�������б仯',
  `matrix_fund_size` CHAR(20) NOT NULL COMMENT '��Ӧ��Ӧ�ó���涨�ľ�����������ģö�٣����硰>5ǧ�ڡ����洢��Ӧ��code������ö�ٿ�������ϵͳ�����仯��',
  `time_period` CHAR(20) NOT NULL COMMENT '��Ӧ��Ӧ�ó���������ޣ�����T1D��������Ϊ1��ı��ۡ������޶����������Ӧ��ϵͳ�����������޸ġ�',
  `price_low` DECIMAL(10,4) NULL COMMENT '�۸��ֵ',
  `price_high` DECIMAL(10,4) NULL COMMENT '�۸��ֵ',
  `create_time` DATETIME NOT NULL COMMENT '����¼����������ʱ��',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_UNIQUE` (`quote_type` ASC, `direction` ASC, `matrix_fund_size` ASC, `matrix_bank_nature` ASC, `time_period` ASC, `create_time` ASC))
ENGINE = InnoDB
COMMENT = '�������ڱ��۾���Ŀ�����֧�ּ۸������ʷ���ݵĲ�ѯ��չʾ';
