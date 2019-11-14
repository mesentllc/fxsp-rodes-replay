select bp.fedex_pkg_id
from SPRODS_SCHEMA.billing_package bp
join sprods_schema.billing_group bg on bg.bg_seq = bp.bg_seq
where bp.fedex_pkg_id in (:pkgList) and bg.status != 3