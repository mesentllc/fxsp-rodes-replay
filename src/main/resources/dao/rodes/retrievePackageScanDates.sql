SELECT distinct to_char(origin_hub_scan_dt, 'MM/DD/YYYY')
from sprods_schema.billing_package
where fedex_pkg_id in (:pkgList) and
	  delivery_type in (:deliveryTypes)