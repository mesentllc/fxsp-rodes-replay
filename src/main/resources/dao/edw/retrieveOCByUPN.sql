select shpr_cust_pkg_ref_nbr as ord_cr_blng_ref_nbr, pkg_barcd_nbr, oc.unvsl_pkg_nbr, mail_class_cd,mail_sub_class_cd,
       case when prcs_size_cd= '' then 'N' else prcs_size_cd end,
       case when prcs_ctgy_cd = '' then 'M' else prcs_ctgy_cd end,
       blng_svc_cd, dest_sort_cd, hub_cd, pkg_lb_wgt as pkg_wgt, pkg_lth_qty, pkg_width_qty,
       pkg_hgt_qty,'WP999999999999'  as cntnr_nm, event_tz_tmstp as mindate, 'G' as wgt_src_cd,
       cust_mnfst_cd as ord_crt_mnfst_grp_id_nbr, cust_mnfst_grp_desc as ord_cr_cust_mtnfst_nm,
       meter_nbr as ord_cr_meter_nbr, po_nbr as ord_cr_po_nbr
from smartpost_eds_prod_view_db.fxsp_order_create oc
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON oc.unvsl_pkg_nbr = k.unvsl_pkg_nbr AND alter_pkg_key_cd like 'POSTALDC9%'
WHERE k.unvsl_pkg_nbr in (SELECT * FROM upnTable)
QUALIFY ROW_NUMBER() OVER(PARTITION BY k.pkg_barcd_nbr ORDER BY ord_cr_blng_ref_nbr DESC, db_load_dt_tmstp desc)=1
