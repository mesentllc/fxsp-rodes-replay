package com.fedex.smartpost.utilities.evs.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.evs.model.Package;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class PackageDaoImpl extends NamedParameterJdbcTemplate implements PackageDao {
	private static final Log log = LogFactory.getLog(PackageDao.class);
	private static final String RETRIEVE_PACKAGES = ClassPathResourceUtil.getString("dao/evs/retrievePackages.sql");

	public PackageDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	private RowMapper<Package> PACKAGE_RM = (rs, i) -> {
		Package pkg = new Package();
		pkg.setPkgId(rs.getString("PKG_ID"));
		pkg.setEvsManifestSeq(rs.getBigDecimal("EVS_MANIFEST_SEQ"));
		pkg.setErrorCd(rs.getBigDecimal("ERROR_CD"));
		pkg.setMailClass(rs.getString("MAIL_CLASS"));
		pkg.setActlWgt(rs.getBigDecimal("ACTL_WGT"));
		pkg.setDestZip(rs.getString("DEST_ZIP"));
		pkg.setCntryCode(rs.getString("CNTRY_CODE"));
		pkg.setPrcsCtg(rs.getString("PRCS_CTG"));
		pkg.setDestRateInd(rs.getString("DEST_RATE_IND"));
		pkg.setRateInd(rs.getString("RATE_IND"));
		pkg.setZone(rs.getString("ZONE"));
		pkg.setBarcodeCd(rs.getString("BARCODE_CD"));
		pkg.setProdCd(rs.getString("PROD_CD"));
		pkg.setArticleValue(rs.getBigDecimal("ARTICLE_VALUE"));
		pkg.setLength(rs.getBigDecimal("LENGTH"));
		pkg.setWidth(rs.getBigDecimal("WIDTH"));
		pkg.setHeight(rs.getBigDecimal("HEIGHT"));
		pkg.setDimWgt(rs.getBigDecimal("DIM_WGT"));
		pkg.setClientMailerId(rs.getString("CLIENT_MAILER_ID"));
		pkg.setCustRefNbr(rs.getString("CUST_REF_NBR"));
		pkg.setEntryFacTypeCd(rs.getString("ENTRY_FAC_ZIP"));
		pkg.setTrlrCloseDt(rs.getDate("TRLR_CLOSE_DT"));
		pkg.setHubId(rs.getString("HUB_ID"));
		pkg.setSentEvsFlag(rs.getString("SENT_EVS_FLAG"));
		pkg.setCurDtTmstamp(rs.getDate("CUR_DT_TM_STAMP"));
		pkg.setOvszFlag(rs.getString("OVSZ_FLAG"));
		pkg.setBlnFlag(rs.getString("BLN_FLAG"));
		pkg.setDcFlag(rs.getString("DC_FLAG"));
		pkg.setCodFlag(rs.getString("COD_FLAG"));
		pkg.setErrMsg(rs.getString("ERR_MSG"));
		pkg.setFieldErrMsg(rs.getString("FIELD_ERR_MSG"));
		pkg.setErrLineNbr(rs.getString("ERR_LINE_NBR"));
		pkg.setUserLoadNbr(rs.getString("USER_LOAD_NBR"));
		pkg.setEvsReleaseTypeCd(rs.getString("EVS_RELEASE_TYPE_CD"));
		pkg.setTripSeq(rs.getString("TRIP_SEQ"));
		pkg.setChanlAppId(rs.getString("CHANL_APP_ID"));
		pkg.setServiceTypeCd(rs.getString("SERVICE_TYPE_CD"));
		pkg.setEntryFacTypeCd(rs.getString("ENTRY_FAC_TYPE_CD"));
		pkg.setFastRsvntNbr(rs.getString("FAST_RSVTN_NBR"));
		pkg.setFastScheduledDt(rs.getDate("FAST_SCHEDULED_DT"));
		pkg.setContainerId(rs.getString("CONTAINER_ID"));
		pkg.setContainerTypeCd(rs.getString("CONTAINER_TYPE_CD"));
		pkg.setMailDateTmstp(rs.getTimestamp("MAIL_DATE_TMSTP"));
		pkg.setSupressPstgFlag(rs.getString("SUPPRESS_PSTG_FLAG"));
		pkg.setSupressPstgRsnCd(rs.getString("SUPPRESS_PSTG_RSN_CD"));
		pkg.setMainElecFileNbr(rs.getBigDecimal("MAIN_ELEC_FILE_NBR"));
		pkg.setRateSheetLabelTxt(rs.getString("RATE_SHEET_LABEL_TXT"));
		pkg.setZipListLabelTxt(rs.getString("ZIP_LIST_LABEL_TXT"));
		pkg.setAllocateHubId(rs.getString("ALLOCATE_HUB_ID"));
		pkg.setPaymentAcctNbr(rs.getString("PAYMENT_ACCT_NBR"));
		pkg.setRatineRlsRcvdDt(rs.getDate("RATING_RLS_RCVD_DT"));
		return pkg;
	};

	@Override
	public List<Package> retrievePackages(List<String> packageIds) {
		List<Package> packageList = new ArrayList<>();
		int startPos = 0;
		int length;

		log.info("Number of packages to check in PACKAGE [EVS]: " + packageIds.size());
		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			List<String> batch = packageIds.subList(startPos, startPos + length);
			parameters.addValue("packageIds", batch);
			packageList.addAll(query(RETRIEVE_PACKAGES, parameters, PACKAGE_RM));
			startPos += length;
		}
		log.info("Number of packages found: " + packageList.size());
		return packageList;
	}
}
