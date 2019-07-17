select PKG_DOM_EVENT_ID, EVENT_HASH_ID, FEDEX_PKG_ID_NM, FEDEX_CUST_ACCT_NBR, PKG_EVENT_STATUS_CD, PKG_EVENT_REASON_DESC, BP_SEQ_ID
from sprods_schema.package_domestic_event_stat
where FEDEX_PKG_ID_NM IN (:packageIds)