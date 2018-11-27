select oc.unvsl_pkg_nbr, pkg_barcd_nbr,mail_class_cd,mail_sub_class_cd,
       case when prcs_size_cd= '' then 'N' else prcs_size_cd end,
       case when prcs_ctgy_cd = '' then 'M' else prcs_ctgy_cd end,
       dest_sort_cd, hub_cd,pkg_lb_wgt as pkg_wgt, pkg_lth_qty, pkg_width_qty,
       pkg_hgt_qty,'WP999999999999'  as cntnr_nm, event_tz_tmstp as mindate,'G' as wgt_src_cd,
from smartpost_eds_prod_view_db.fxsp_order_create oc
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON oc.unvsl_pkg_nbr = k.unvsl_pkg_nbr AND alter_pkg_key_cd like 'POSTALDC9%'
WHERE k.pkg_barcd_nbr in (SELECT * FROM PACKAGES)