SELECT k.pkg_barcd_nbr
FROM smartpost_prod_view_db.fxsp_alternate_pkg_key k
JOIN smartpost_eds_prod_view_db.fxsp_rodes_rating_release rrr ON k.unvsl_pkg_nbr = rrr.unvsl_pkg_nbr
WHERE k.pkg_barcd_nbr in (SELECT * from PACKAGES)