update mm_quote_details
set days_low = 0,
	days_high = 7
where limit_type = 'T7D';

update mm_quote_details
set days_low = 8,
	days_high = 14
where limit_type = 'T14D';

update mm_quote_details
set days_low = 15,
	days_high = 30
where limit_type = 'T1M';

update mm_quote_details
set days_low = 31,
	days_high = 60
where limit_type = 'T2M';

update mm_quote_details
set days_low = 61,
	days_high = 90
where limit_type = 'T3M';

update mm_quote_details
set days_low = 91,
	days_high = 180
where limit_type = 'T6M';

update mm_quote_details
set days_low = 181,
	days_high = 360
where limit_type = 'T1Y';

