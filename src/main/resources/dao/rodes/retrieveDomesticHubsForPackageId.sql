select FEDEX_PKG_ID_NM, HUB_CD
from sprods_schema.package_domestic_event
where FEDEX_PKG_ID_NM IN (:packageIds)