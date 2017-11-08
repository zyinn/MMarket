CREATE TABLE IF NOT EXISTS mm_alliance_institution_x (
  primary_institution_id VARCHAR(32) NOT NULL COMMENT '������ID',
  alliance_institution_id VARCHAR(32) NOT NULL COMMENT '���˻���ID',
  PRIMARY KEY (primary_institution_id, alliance_institution_id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '���˻���ӳ����������˻������������Ĵ�';


CREATE TABLE IF NOT EXISTS mm_user_rights (
  id VARCHAR(32) NOT NULL,
  user_id VARCHAR(32) NULL DEFAULT NULL,
  usecase VARCHAR(64) NULL DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX IDX_UNIQUE (user_id ASC, usecase ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS mm_quote;
CREATE TABLE IF NOT EXISTS `mm_quote` (
  `id` VARCHAR(32) NOT NULL COMMENT 'UUID��׼���߼�����',
  `quote_type` CHAR(3) NULL DEFAULT NULL COMMENT '��������:\n\nGTF: ���-���� : Guaranteed Fund\nUR2�����-�Ǳ���R2: Unguaranteed R2\nUR3�����-�Ǳ���R3: Unguaranteed R3\n\nIBD��ͬҵ��� Inter-Bank Deposit',
  `institution_id` VARCHAR(32) NULL DEFAULT NULL COMMENT '���ۻ���id����һ������û�����˻���ʱ����id���Ǳ�������������ID���������˻�������id��ǰ��ҳ��ָ����',
  `quote_user_id` VARCHAR(32) NULL DEFAULT NULL COMMENT '������QB ID ���� QQ code\n',
  `sequence` INT(5) NULL DEFAULT NULL COMMENT '���ֶα����û��ı���˳��\n�ȱ��۵�����ʱ��˳�򡣸�˳��һ�����ɲ��ٸ��£���ҳ���ϲ��ṩ�ı�˳��Ĺ���֧�֡�',
  `direction` CHAR(3) NULL DEFAULT NULL COMMENT 'OUT\nIN',
  `method_type` CHAR(3) NULL DEFAULT NULL COMMENT '���۷�����\nSEF: ��ͨ���ۣ�����Ա���б��ۡ����б��б��ۻ�����ʾ���û��������� ALC: ���˱��ۣ��������������˻������б��ۡ�������۲�֮ͬ�������б����ʾ��ʽ������μ������ĵ���BRK: �����ۣ���ĳ������ɭ�֣��ĳ�Ա������һЩ�������б��ۡ������۵Ļ�����Ӧ��ϵ��mm_alliance_institution_xά����'
  `memo` VARCHAR(512) NULL DEFAULT NULL COMMENT '��ע',
  `source` CHAR(3) NULL DEFAULT NULL COMMENT '��Դ�� QQ ����QB����\nQQ\nQB',
  `create_time` DATETIME NULL DEFAULT NULL COMMENT '��������',
  `expired_date` DATETIME NULL DEFAULT NULL COMMENT '����ʱ�䣬�����û���ǰ��ҳ��ѡ�����Ч�ڣ����Σ�Ĭ�ϣ��죩����ó���',
  `province` VARCHAR(32) NULL DEFAULT NULL COMMENT 'ʡ����Ϣ���ɱ��۵�д��ʱ���ã���¼ʡ��ֱϽ�м��۰�̨���Ƶĺ��֡�\n���ֶ��Ƿ���ʽ����ֶΣ������б��ѯ���Ż����ܹ����������Ӧ�ٶȡ�',
  `bank_nature` CHAR(1) NULL DEFAULT NULL COMMENT '����������Ϣ���ɱ��۵�д��ʱ���ã���¼��������codeֵ��\n���ֶ��Ƿ���ʽ����ֶΣ������б��ѯ���Ż����ܹ����������Ӧ�ٶȡ�',
  `custodian_qualification` CHAR(1) NULL COMMENT '�����й�����\n1 �й�\n0 ���й�\nnull ���й�',
  `fund_size` DECIMAL(12,2) NULL COMMENT '������ģ���ڱ��۵�д�����ݿ�ʱ�ӻ���������ģ���ݱ��ȡ���ݲ���¼�ڸ��ֶΡ����ֶ����ڱ��۲�ѯ����λ����',
  PRIMARY KEY (`id`),
  INDEX `IDX_BUSINESS_KEY` (`quote_type` ASC, `institution_id` ASC, `quote_user_id` ASC, `direction` ASC, `create_time` ASC)  COMMENT  /* comment truncated */ /*��ҵ������������������Ψһ��������Ϊһ���û�һ�����Ϊһ����������ظ����ۡ�*/,
  INDEX `IDX_PROV` (`province` ASC),
  INDEX `IDX_BANK_NATURE` (`bank_nature` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '���۵�����¼�˱��۵���ϸ��Ϣ\n'
