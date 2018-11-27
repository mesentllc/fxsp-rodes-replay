select bp.status as bp_stat, bg.status as bg_stat, bp.FEDEX_PKG_ID from sprods_schema.billing_package bp
left join sprods_schema.billing_group bg on bp.bg_seq = bg.bg_seq
where bp.FEDEX_PKG_ID in (:pkgList)