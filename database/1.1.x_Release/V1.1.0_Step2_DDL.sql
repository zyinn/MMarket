CREATE TABLE IF NOT EXISTS mm_alliance_institution_x (
  primary_institution_id VARCHAR(32) NOT NULL COMMENT '主机构ID',
  alliance_institution_id VARCHAR(32) NOT NULL COMMENT '联盟机构ID',
  PRIMARY KEY (primary_institution_id, alliance_institution_id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '联盟机构映射表，表明联盟机构与主机构的从';


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
  `id` VARCHAR(32) NOT NULL COMMENT 'UUID标准的逻辑主键',
  `quote_type` CHAR(3) NULL DEFAULT NULL COMMENT '报价类型:\n\nGTF: 理财-保本 : Guaranteed Fund\nUR2：理财-非保本R2: Unguaranteed R2\nUR3：理财-非保本R3: Unguaranteed R3\n\nIBD：同业存款 Inter-Bank Deposit',
  `institution_id` VARCHAR(32) NULL DEFAULT NULL COMMENT '报价机构id，当一个机构没有联盟机构时，该id就是报价人所属机构ID。若有联盟机构，该id由前端页面指定。',
  `quote_user_id` VARCHAR(32) NULL DEFAULT NULL COMMENT '报价人QB ID 或者 QQ code\n',
  `sequence` INT(5) NULL DEFAULT NULL COMMENT '本字段保留用户的报价顺序。\n既报价单输入时的顺序。该顺序一旦生成不再更新，既页面上不提供改变顺序的功能支持。',
  `direction` CHAR(3) NULL DEFAULT NULL COMMENT 'OUT\nIN',
  `method_type` CHAR(3) NULL DEFAULT NULL COMMENT '报价方法：\nSEF: 普通报价，交易员自行报价。在列表中报价机构显示该用户所属机构 ALC: 联盟报价，由主机构代联盟机构进行报价。与代报价不同之处在于列表的显示方式。具体参见需求文档。BRK: 代报价，既某机构（森浦）的成员替其他一些机构进行报价。代报价的机构对应关系在mm_alliance_institution_x维护。'
  `memo` VARCHAR(512) NULL DEFAULT NULL COMMENT '备注',
  `source` CHAR(3) NULL DEFAULT NULL COMMENT '来源： QQ 或者QB报价\nQQ\nQB',
  `create_time` DATETIME NULL DEFAULT NULL COMMENT '报价日期',
  `expired_date` DATETIME NULL DEFAULT NULL COMMENT '过期时间，根据用户在前端页面选择的有效期（整形，默认７天）计算得出。',
  `province` VARCHAR(32) NULL DEFAULT NULL COMMENT '省份信息，由报价单写入时设置，记录省份直辖市及港澳台名称的汉字。\n该字段是反范式设计字段，用于列表查询的优化。能够大大提升响应速度。',
  `bank_nature` CHAR(1) NULL DEFAULT NULL COMMENT '机构属性信息，由报价单写入时设置，记录机构属性code值。\n该字段是反范式设计字段，用于列表查询的优化。能够大大提升响应速度。',
  `custodian_qualification` CHAR(1) NULL COMMENT '机构托管属性\n1 托管\n0 不托管\nnull 不托管',
  `fund_size` DECIMAL(12,2) NULL COMMENT '机构规模。在报价单写入数据库时从机构机构规模数据表获取数据并记录在该字段。该字段用于报价查询。单位“万”',
  PRIMARY KEY (`id`),
  INDEX `IDX_BUSINESS_KEY` (`quote_type` ASC, `institution_id` ASC, `quote_user_id` ASC, `direction` ASC, `create_time` ASC)  COMMENT  /* comment truncated */ /*主业务索引，该索引不是唯一索引，因为一个用户一天可以为一个机构多次重复报价。*/,
  INDEX `IDX_PROV` (`province` ASC),
  INDEX `IDX_BANK_NATURE` (`bank_nature` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '报价单表，记录了报价单详细信息\n'
