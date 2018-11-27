select bgs.BG_SCAN_DATE_TXT from sprods_schema.billing_group_summary bgs
join sprods_schema.billing_group bg on bg.bg_seq = bgs.bg_seq and bg.status = 1
where bgs.package_cnt > 0
