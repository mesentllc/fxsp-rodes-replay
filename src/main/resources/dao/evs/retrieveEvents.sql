SELECT PKG_ID, EVENT_CD, EVENT_DT, SCAN_FAC_ZIP, SCAN_FAC_NAME, EVENT_NAME, DEST_CNTRY_CODE,
       DEST_ZIP_CD_4, MAILER_ID, CLIENT_MAILER_ID, DEST_ZIP, MAILER_NAME, RECIPIENT_NAME,
       EVS_MANIFEST_SEQ, CUST_REF_NBR, CHANL_APP_ID
FROM SPEVS_SCHEMA.USPS_PACKAGE_EVENT
WHERE PKG_ID IN (:packageIds)