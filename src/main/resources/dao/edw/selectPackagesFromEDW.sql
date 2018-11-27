select k.pkg_barcd_nbr,pp.mail_class_cd,pp.mail_sub_class_cd,pp.prcs_ctgy_cd,pp.dest_sort_cd,
       pkg_event_tmstp,hub_cd,pkg_wgt,pkg_lth_qty,pkg_width_qty,pkg_hgt_qty,
       pp.wgt_src_cd,mindate,pp.prcs_size_cd, pp.cntnr_nm
from smartpost_eds_prod_view_db.fxsp_package_detail pp
join smartpost_prod_view_db.fxsp_alternate_pkg_key k on pp.unvsl_pkg_nbr = k.unvsl_pkg_nbr and
     alter_pkg_key_cd like 'POSTALDC9%'
join (select minscandate.unvsl_pkg_nbr, min(pkg_event_tmstp) as mindate from
      smartpost_eds_prod_view_db.fxsp_package_detail minscandate
      join smartpost_prod_view_db.fxsp_alternate_pkg_key k on
      minscandate.unvsl_pkg_nbr = k.unvsl_pkg_nbr and alter_pkg_key_cd like 'POSTALDC9%'
      where pkg_event_type_cd = 'SRTSC' and cntnr_nm not like 'RJ%' and k.pkg_barcd_nbr in
      (select * from packages)
      group by minscandate.unvsl_pkg_nbr) as tmp on tmp.unvsl_pkg_nbr = pp.unvsl_pkg_nbr
join smartpost_prod_view_db.fxsp_f_package pf on pp.unvsl_pkg_nbr = pf.unvsl_pkg_nbr and
     pf.first_fxsp_scan_tz_tmstp >= '2013-01-08 00:00:00' and
     pf.first_fxsp_scan_tz_tmstp <= '2013-01-14 23:59:59'
where pkg_event_type_cd = 'SRTSC' and cntnr_nm not like 'RJ%' and k.pkg_barcd_nbr in
	  (select * from packages)
QUALIFY ROW_NUMBER() OVER(PARTITION BY k.pkg_barcd_nbr ORDER BY pp.wgt_src_cd, pp.dim_src_cd,
                          pkg_event_tmstp)=1
