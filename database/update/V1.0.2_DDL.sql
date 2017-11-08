
Drop View mmv_allusers;

Drop View mmv_qbuser;

Drop View mmv_qquser;



CREATE VIEW mmv_qbuser AS
select account.id as user_id, -- �û�ID
         account.DISPLAY_NAME as display_name, -- �û���ʾ��
    company.id as company_id,          -- ����id
         company.shortname_cn as company_name, -- �û�����������ʾ��         
         account.TELEPHONE as telephone, -- �绰
         account.MOBILE as mobile, -- �ƶ��绰
         region.Province as province, -- ��������ʡ�ݣ� ֱϽ�и��ֶ�Ϊnull���
         company.CITY_NAME as city_name,  -- �����������ڳ��� ������ֱϽ��û�������Ϣ����˲�ѯֱϽ��ʱ��Ҫʹ�ø��ֶβ�ѯ��
         company.bank_nature as bank_nature,  -- �������� ��ֵ��idb_center.idb_bank_nature ���壬��ѯʱ��ID��ѯ��ϵͳ��Ҫ����idb_bank_nature��С��30�����ݣ�
    prime.institution_id as prime_company_id,                           -- ��Ʒ�������Ǿ�Ʒ����Ϊnull
         company.custodian_qualification as qualification,  -- �����Ƿ��й�
    account.USERNAME as user_name,    -- �û���
    account.password as user_password,  -- �û�����
    account.email as email,   -- email 
    account.qq as qq_code,   -- qq 
    'QB' as source
from idb_account account
    left join idb_financial_company company on account.company_id = company.id
    left join idb_region region on company.city = region.code
    left join mm_prime_institution prime on prime.institution_id = company.id
    where account.status = '1' ;



CREATE VIEW `mmv_qquser` AS
select qq as user_id, -- QQ ��
         nickname as display_name, -- �û���ʾ��������������
         '' as company_id,
         '' as company_name,
         phone as telephone, -- �绰
         mobile as mobile,  -- �ƶ��绰
         province as province, -- �������򣬰���ʡ�ݺ�ֱϽ�У���Ϊ����
    province as city_name, -- same with mmv_qbuser!
         orgtype as bank_nature, -- �������ͣ���ֵ��idb_center.idb_bank_nature ����
    null as prime_company_id, -- �Ǿ�Ʒ����Ϊnull
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
