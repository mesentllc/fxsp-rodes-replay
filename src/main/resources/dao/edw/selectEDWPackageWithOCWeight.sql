select pkg_barcd_nbr, mail_class_cd, mail_sub_class_cd, prcs_ctgy_cd, dest_sort_cd, first_fxsp_scan_tz_tmstp,
       orig_hub_cd as hub_cd, fppp.pstl_wgt as pkg_wgt, fcpe.cust_lth_qty as pkg_lth_qty,
       fcpe.cust_width_qty as pkg_width_qty, fcpe.cust_hgt_qty as pkg_hgt_qty,
       wgt_src_cd as wgt_src_cd, prcs_size_cd, cntnr_nm,
       first_fxsp_scan_tz_tmstp as mindate, pf.unvsl_pkg_nbr
from smartpost_prod_view_db.fxsp_f_package pf
join smartpost_prod_view_db.fxsp_package_postal_payment fppp on pf.unvsl_pkg_nbr = fppp.unvsl_pkg_nbr
join smartpost_prod_view_db.FXSP_CUST_PACKAGES_EXPECTED fcpe ON pf.unvsl_pkg_nbr = fcpe.unvsl_pkg_nbr
join smartpost_prod_view_db.fxsp_alternate_pkg_key k on pf.unvsl_pkg_nbr = k.unvsl_pkg_nbr and
     alter_pkg_key_cd like 'POSTALDC9%'
join smartpost_prod_view_db.fxsp_package_transit ps on pf.unvsl_pkg_nbr = ps.unvsl_pkg_nbr
where k.pkg_barcd_nbr in (SELECT * from PACKAGES) AND
      not exists (SELECT 1 FROM smartpost_eds_prod_view_db.fxsp_rodes_rating_release rrr
                  WHERE rrr.unvsl_pkg_nbr = k.unvsl_pkg_nbr) and pf.blng_grp_relse_tz_tmstp is null
QUALIFY ROW_NUMBER() OVER(PARTITION BY k.pkg_barcd_nbr ORDER BY pf.db_load_dt_tmstp DESC)=1