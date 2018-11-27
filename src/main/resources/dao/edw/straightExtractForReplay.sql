SELECT k.pkg_barcd_nbr, CASE  usps_mail_class_cd WHEN 'SA' THEN 'A' ELSE 'B' END as mail_class_cd,
       CASE  usps_mail_class_cd WHEN 'BS' THEN 'M' WHEN 'BB' THEN 'B' ELSE 'P' END as mail_sub_class_cd, 'M' AS prcs_ctgy_cd,
       SUBSTR(pp.dest_pstl_cd, 1, 5) as dest_sort_cd, umfst_usps_scan_dt, hub_cd, pstl_wgt as pkg_wgt,
       cust_lth_in_qty as pkg_lth_qty, cust_width_in_qty as pkg_width_qty, cust_hgt_in_qty as pkg_hgt_qty,
       'C' AS wgt_src_cd, umfst_usps_scan_dt AS mindate, 'N' AS prcs_size_cd, 'WP999999999999' AS cntnr_nm, umfst_usps_prcs_dt,
       k.unvsl_pkg_nbr as unvsl_pkg_nbr
FROM SMARTPOST_PROD_VIEW_DB.fxsp_package_postal_payment pp
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON pp.unvsl_pkg_nbr = k.unvsl_pkg_nbr AND k.alter_pkg_key_cd LIKE 'POSTALDC9%'
JOIN SMARTPOST_PROD_VIEW_DB.fxsp_f_package p ON p.unvsl_pkg_nbr = pp.unvsl_pkg_nbr
WHERE umfst_stat_cd = 'US' AND p.blng_grp_nbr IS NULL
AND pstl_pymt_dt BETWEEN '2016-03-01' AND '2016-06-05'
AND NOT EXISTS (SELECT sp_pkg_trk_nbr FROM  smartpost_prod_view_db.smartpost_package_dtl
                WHERE SUBSTR(sp_pkg_trk_nbr, 1, 20) = k.pkg_barcd_nbr AND srt_end_dt > '2016-03-01')
AND shpr_acct_nbr NOT IN (371918612, 675834962, 242242440)
AND p.ord_cr_tz_tmstp IS NOT NULL AND first_fxsp_scan_tz_tmstp IS NULL
