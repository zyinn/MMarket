update mm_quote set custodian_qualification = '0' where custodian_qualification is null;

update mm_quote set method_type = 'SEF' where method_type is null;

update mm_quote set institution_id = '-1' where institution_id is null;

update mm_quote set expired_date = date_add(create_time, INTERVAL 1 DAY) where expired_date is null;


update mm_quote_details set days_low = 0 where days_low is null;

update mm_quote_details set days_high = 1 where days_high is null;