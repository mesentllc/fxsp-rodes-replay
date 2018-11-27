SELECT pf.unvsl_pkg_nbr, k.pkg_barcd_nbr, pf.first_fxsp_scan_tz_tmstp as mindate
FROM smartpost_prod_view_db.fxsp_f_package pf
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON pf.unvsl_pkg_nbr = k.unvsl_pkg_nbr AND
     alter_pkg_key_cd like 'POSTALDC9%'
WHERE k.pkg_barcd_nbr in (SELECT * from PACKAGES) AND
      not exists (SELECT 1 FROM smartpost_eds_prod_view_db.fxsp_rodes_rating_release rrr
                  WHERE rrr.unvsl_pkg_nbr = k.unvsl_pkg_nbr) and pf.blng_grp_relse_tz_tmstp is null
QUALIFY ROW_NUMBER() OVER(PARTITION BY k.pkg_barcd_nbr ORDER BY pf.db_load_dt_tmstp DESC)=1
