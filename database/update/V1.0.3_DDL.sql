-- 报价明细表增加字段用于QQ信息的记录
ALTER TABLE mm_quote_details ADD COLUMN qq_message varchar(1024) null AFTER last_update_time ;

