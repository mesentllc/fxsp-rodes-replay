select pf.unvsl_pkg_nbr, pkg_barcd_nbr,
	   sp_mail_class_cd as mail_class_cd, sp_mail_sub_class_cd as mail_sub_class_cd,
       case when prcs_size_cd= '' or prcs_size_cd is null then 'N' else prcs_size_cd end,
       case when prcs_ctgy_cd = '' or prcs_ctgy_cd is null  then 'M' else prcs_ctgy_cd end,
       case when dest_sort_cd is null then dest_5_digit_pstl_cd else dest_sort_cd end as dest_sort_cd,
	   loc_num_cd as hub_cd, blng_svc_cd, dest_loc_nbr,
	   case when actl_lb_wgt is null then cust_lb_wgt else  actl_lb_wgt end as pkg_wgt,
	   case when actl_lth_in_qty is null then cust_lth_in_qty else actl_lth_in_qty end as pkg_lth_qty,
	   case when actl_width_in_qty is null then cust_width_in_qty else actl_width_in_qty end as pkg_width_qty,
       case when actl_hgt_in_qty is null then cust_hgt_in_qty else actl_hgt_in_qty end as pkg_hgt_qty,
	   'WP999999999999'  as cntnr_nm,
	   case when ord_cr_tz_tmstp is null then pf.db_load_dt_tmstp else ord_cr_tz_tmstp end as mindate,
	   'G' as wgt_src_cd,
       ord_crt_mnfst_grp_id_nbr, ord_cr_cust_mtnfst_nm, ord_cr_meter_nbr, ord_cr_blng_ref_nbr, ord_cr_po_nbr, shpr_acct_nbr
from smartpost_prod_view_db.fxsp_f_package pf
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON pf.unvsl_pkg_nbr = k.unvsl_pkg_nbr AND alter_pkg_key_cd like 'POSTALDC9%'
join smartpost_prod_view_db.fxsp_usps_mail_class u on u.mail_class_id_nbr = pf.mail_class_id_nbr
join smartpost_prod_view_db.fxsp_d_location l on l.loc_id_nbr = pf.ord_cr_exp_orig_hub_id_nbr
WHERE k.unvsl_pkg_nbr in (SELECT * FROM upnTable)
QUALIFY ROW_NUMBER() OVER(PARTITION BY k.pkg_barcd_nbr ORDER BY ord_cr_blng_ref_nbr DESC, pf.db_load_dt_tmstp desc)=1
