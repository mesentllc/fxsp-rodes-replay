SELECT K.PKG_BARCD_NBR, PM.ELEC_FILE_NBR FROM SMARTPOST_EDS_PROD_VIEW_DB.FXSP_POSTAL_MANIFEST PM
JOIN SMARTPOST_PROD_VIEW_DB.FXSP_ALTERNATE_PKG_KEY AS K ON PM.UNVSL_PKG_NBR = K.UNVSL_PKG_NBR AND K.PRIM_BARCD_FLG = 'Y'
WHERE K.PKG_BARCD_NBR IN (SELECT * FROM PACKAGES)