SELECT fedex_pkg_id, delivery_type, delivery_dt, status, status_dt, origin_hub_scan_dt, created_dt, bg_seq, fxsp_orig_loc_cd, origin_hub_cd
from sprods_schema.billing_package
where fedex_pkg_id in (:pkgList) and
	  delivery_type in (:deliveryTypes)