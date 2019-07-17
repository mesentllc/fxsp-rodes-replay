SELECT sp_pkg_trk_nbr
FROM smartpost_prod_view_db.smartpost_package_dtl
WHERE sp_pkg_trk_nbr in (SELECT * from PACKAGES)