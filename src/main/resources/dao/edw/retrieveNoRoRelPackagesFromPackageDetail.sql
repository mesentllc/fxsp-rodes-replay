SELECT pd.unvsl_pkg_nbr, k.pkg_barcd_nbr
FROM smartpost_eds_prod_view_db.fxsp_package_detail pd
JOIN smartpost_prod_view_db.fxsp_alternate_pkg_key k ON pd.unvsl_pkg_nbr = k.unvsl_pkg_nbr AND alter_pkg_key_cd like 'POSTALDC9%'
WHERE k.pkg_barcd_nbr in (SELECT * from PACKAGES) AND
      not exists (SELECT 1 from smartpost_eds_prod_view_db.fxsp_package_detail pdi
                  WHERE pd.unvsl_pkg_nbr = pdi.unvsl_pkg_nbr AND pdi.pkg_event_type_cd = 'ROREL')