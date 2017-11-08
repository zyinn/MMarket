-- 将MM_Quote_Old 拷贝至 MM_Quote
insert into mm_quote (id,quote_type,direction,institution_id,quote_user_id,memo,source,CREATE_TIME) select * from mm_quote_old;
-- 设置sequence
update mm_quote set sequence=1 where sequence is null;
-- 设置method_type
update mm_quote set method_type='SEF' where method_type is null;
-- 设置expired_date
update mm_quote quote set expired_date= CREATE_TIME;

-- 设置province
update mm_quote quote set quote.province =
(SELECT region.province FROM money_market.idb_financial_company company join idb_region region where company.city=region.code and company.id=quote.institution_id); 
-- 设置bank_nature
update mm_quote quote set quote.bank_nature =(select company.bank_nature from idb_financial_company company where  company.id=quote.institution_id); 
-- 设置custodian_qualification
update mm_quote quote set quote.custodian_qualification =(select company.custodian_qualification from idb_financial_company company where  company.id=quote.institution_id); 
--设置机构规模 fund_size
update mm_quote quote set quote.fund_size =
(SELECT company.fund_size FROM idb_head_financial_company company where company.id=quote.institution_id); 