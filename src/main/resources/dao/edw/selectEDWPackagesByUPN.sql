select pkg_barcd_nbr, mail_class_cd, mail_sub_class_cd, prcs_ctgy_cd, dest_sort_cd, first_fxsp_scan_tz_tmstp,
       orig_hub_cd as hub_cd, actl_wgt as pkg_wgt, actl_lth_qty as pkg_lth_qty, actl_width_qty as pkg_width_qty,
       actl_hgt_qty as pkg_hgt_qty, wgt_src_cd as wgt_src_cd, prcs_size_cd, cntnr_nm,
       first_fxsp_scan_tz_tmstp as mindate, pf.unvsl_pkg_nbr
from smartpost_prod_view_db.fxsp_f_package pf
join smartpost_prod_view_db.fxsp_alternate_pkg_key k on pf.unvsl_pkg_nbr = k.unvsl_pkg_nbr and alter_pkg_key_cd like 'POSTALDC9%'
join smartpost_prod_view_db.fxsp_package_transit ps on pf.unvsl_pkg_nbr = ps.unvsl_pkg_nbr
where pf.unvsl_pkg_nbr in (SELECT * FROM upnTable) and pf.blng_grp_relse_tz_tmstp is null
qualify row_number() over (partition by pkg_barcd_nbr order by ps.db_load_dt_tmstp desc) = 1