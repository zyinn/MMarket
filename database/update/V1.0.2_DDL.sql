
Drop View mmv_allusers;

Drop View mmv_qbuser;

Drop View mmv_qquser;



CREATE VIEW mmv_qbuser AS
select account.id as user_id, -- 用户ID
         account.DISPLAY_NAME as display_name, -- 用户显示名
    company.id as company_id,          -- 机构id
         company.shortname_cn as company_name, -- 用户所属机构显示名         
         account.TELEPHONE as telephone, -- 电话
         account.MOBILE as mobile, -- 移动电话
         region.Province as province, -- 机构所属省份， 直辖市该字段为null或空
         company.CITY_NAME as city_name,  -- 所属机构所在城市 （由于直辖市没有身份信息，因此查询直辖市时需要使用该字段查询）
         company.bank_nature as bank_nature,  -- 机构类型 数值在idb_center.idb_bank_nature 定义，查询时按ID查询，系统需要缓存idb_bank_nature表（小于30行数据）
    prime.institution_id as prime_company_id,                           -- 精品机构，非精品机构为null
         company.custodian_qualification as qualification,  -- 机构是否托管
    account.USERNAME as user_name,    -- 用户名
    account.password as user_password,  -- 用户密码
    account.email as email,   -- email 
    account.qq as qq_code,   -- qq 
    'QB' as source
from idb_account account
    left join idb_financial_company company on account.company_id = company.id
    left join idb_region region on company.city = region.code
    left join mm_prime_institution prime on prime.institution_id = company.id
    where account.status = '1' ;



CREATE VIEW `mmv_qquser` AS
select qq as user_id, -- QQ 号
         nickname as display_name, -- 用户显示名，带机构名称
         '' as company_id,
         '' as company_name,
         phone as telephone, -- 电话
         mobile as mobile,  -- 移动电话
         province as province, -- 所属区域，包括省份和直辖市，均为汉字
    province as city_name, -- same with mmv_qbuser!
         orgtype as bank_nature, -- 机构类型，数值在idb_center.idb_bank_nature 定义
    null as prime_company_id, -- 非精品机构为null
    '0' as qualification,     
         null as user_name,      
    null as user_password,
    '' as email,    -- email
    qq as qq_code,   -- qq 
    'QQ' as source
from QQAccountInfo;


CREATE VIEW `mmv_allusers` AS
select * from mmv_qbuser
union
select * from mmv_qquser
