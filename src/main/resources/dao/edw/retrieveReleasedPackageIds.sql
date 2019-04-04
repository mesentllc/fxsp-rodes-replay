SELECT rrr.unvsl_pkg_nbr, pstl_barcd_nbr, sort_start_dt as mindate
FROM smartpost_eds_prod_view_db.fxsp_rodes_rating_release rrr
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON rrr.unvsl_pkg_nbr = k.unvsl_pkg_nbr AND alter_pkg_key_cd like 'POSTALDC9%'
WHERE k.pkg_barcd_nbr in (SELECT * FROM PACKAGES)
