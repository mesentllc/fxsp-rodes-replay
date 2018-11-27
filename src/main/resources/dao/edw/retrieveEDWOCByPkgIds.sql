select k.unvsl_pkg_nbr, k.pkg_barcd_nbr, fcpe.order_crte_tmstp as mindate,
       'N' as prcs_size_cd, 'M' as prcs_ctgy_cd, 'G' as wgt_src_cd
from smartpost_prod_view_db.fxsp_alternate_pkg_key k
JOIN smartpost_prod_view_db.FXSP_CUST_PACKAGES_EXPECTED fcpe ON k.unvsl_pkg_nbr = fcpe.unvsl_pkg_nbr
WHERE k.pkg_barcd_nbr in (SELECT * FROM PACKAGES) and alter_pkg_key_cd like 'POSTALDC9%' and mindate >= '2015-12-31 00:00:00'