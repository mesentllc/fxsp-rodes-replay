SELECT unvsl_pkg_nbr, pstl_barcd_nbr, sort_start_dt as mindate
FROM smartpost_eds_prod_view_db.fxsp_rodes_rating_release
WHERE pstl_barcd_nbr in (SELECT * from PACKAGES)