SELECT PKG_ID, CHANL_APP_ID, UNMAN_STAT, FEDEX_ACCT_NBR, PTS_XMT_DT, SCAN_DT, PSTG_AMT,
       CLIENT_MAILER_ID, CLIENT_MAILER_NM, SCAN_FAC_PSTL_CD, MAIL_CLASS
FROM sprods_schema.UNMANIFESTED_PACKAGE WHERE PKG_ID in (:packageIds)