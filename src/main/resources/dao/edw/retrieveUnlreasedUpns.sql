SELECT k.unvsl_pkg_nbr
FROM smartpost_prod_view_db.fxsp_alternate_pkg_key k
WHERE k.unvsl_pkg_nbr in (SELECT * from upnTable) AND
      not exists (SELECT 1 FROM smartpost_eds_prod_view_db.fxsp_rodes_rating_release rrr
                  WHERE rrr.unvsl_pkg_nbr = k.unvsl_pkg_nbr)