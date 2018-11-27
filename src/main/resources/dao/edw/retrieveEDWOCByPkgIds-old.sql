select k.unvsl_pkg_nbr, k.pkg_barcd_nbr, fppp.umfst_usps_scan_dt as mindate, fppp.pstl_wgt as pkg_wgt, fcpe.cust_width_qty as pkg_width_qty,
	   fcpe.cust_lth_qty as pkg_lth_qty, fcpe.cust_hgt_qty as pkg_hgt_qty, 'N' as prcs_size_cd, 'M' as prcs_ctgy_cd,
	   case when wgt_src_cd is null then 'G' else wgt_src_cd end as wgt_src_cd, 'WP999999999999' as cntnr_nm,
	   fcpe.hub_cd as hub_cd, fppp.dest_pstl_cd as dest_sort_cd
from smartpost_prod_view_db.fxsp_package_postal_payment fppp
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON k.unvsl_pkg_nbr = fppp.unvsl_pkg_nbr AND alter_pkg_key_cd like 'POSTALDC9%'
left JOIN smartpost_prod_view_db.FXSP_CUST_PACKAGES_EXPECTED fcpe ON k.unvsl_pkg_nbr = fcpe.unvsl_pkg_nbr
left JOIN smartpost_prod_view_db.fxsp_f_package ffp ON k.unvsl_pkg_nbr = ffp.unvsl_pkg_nbr
WHERE k.pkg_barcd_nbr in (SELECT * FROM PACKAGES) and mindate >= '2015-12-01'
QUALIFY ROW_NUMBER() OVER(PARTITION BY k.pkg_barcd_nbr ORDER BY mindate DESC)=1