CREATE TABLE IF NOT EXISTS `mm_quote_price_trends` (
  `id` VARCHAR(32) NOT NULL,
  `quote_type` CHAR(3) NOT NULL COMMENT '报价类型，详情见mm_quote表对应字段解释',
  `direction` CHAR(3) NOT NULL COMMENT '方向，详情见mm_quote表对应字段解释',
  `matrix_bank_nature` CHAR(20) NOT NULL COMMENT '用于矩阵计算的机构类型枚举，例如“城商行”（保存对应code：CITY_COMMERCIAL_BANK）。该枚举数值可能随着系统升级进行变化',
  `matrix_fund_size` CHAR(20) NOT NULL COMMENT '对应于应用程序规定的矩阵计算机构规模枚举，例如“>5千亿”（存储对应的code），该枚举可能随着系统升级变化。',
  `time_period` CHAR(20) NOT NULL COMMENT '对应于应用程序定义的期限，例如T1D代表期限为1天的报价。该期限定义可能随着应用系统的升级进行修改。',
  `price_low` DECIMAL(10,4) NULL COMMENT '价格低值',
  `price_high` DECIMAL(10,4) NULL COMMENT '价格高值',
  `create_time` DATETIME NOT NULL COMMENT '本记录创建的日期时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_UNIQUE` (`quote_type` ASC, `direction` ASC, `matrix_fund_size` ASC, `matrix_bank_nature` ASC, `time_period` ASC, `create_time` ASC))
ENGINE = InnoDB
COMMENT = '保存用于报价矩阵的快照以支持价格矩阵历史数据的查询及展示';
