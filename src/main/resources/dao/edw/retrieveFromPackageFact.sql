SELECT k.pkg_barcd_nbr, pf.mail_class_cd, pf.mail_sub_class_cd, pf.prcs_ctgy_cd, pf.dest_sort_cd,
       pf.first_fxsp_scan_tz_tmstp as pkg_event_tmstp, pf.orig_hub_cd as hub_cd,  pf.actl_wgt as pkg_wgt,
       pf.actl_lth_qty as pkg_lth_qty, pf.actl_width_qty as pkg_width_qty, pf.actl_hgt_qty as pkg_hgt_qty,
       pf.wgt_src_cd, pf.first_fxsp_scan_tz_tmstp as mindate, pf.prcs_size_cd, 'WP999999999999' as cntnr_nm,
       pf.pkg_last_scan_tmstp as maxdate, k.unvsl_pkg_nbr as unvsl_pkg_nbr
FROM smartpost_prod_view_db.fxsp_f_package pf
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON pf.unvsl_pkg_nbr = k.unvsl_pkg_nbr AND alter_pkg_key_cd like 'POSTALDC9%'
WHERE mindate >= '2015-09-02 00:00:00' and
--      mindate <= '2015-09-22 23:59:59' and
      not exists (select 1 from smartpost_eds_prod_view_db.fxsp_rodes_rating_release rrr where rrr.unvsl_pkg_nbr = k.unvsl_pkg_nbr) and
--      mindate <= '2015-09-04 23:55:59' and
--      maxdate >= '2015-08-24 00:00:00' and
      k.pkg_barcd_nbr IN (SELECT * FROM PACKAGES)
ORDER BY k.pkg_barcd_nbr
