package com.fedex.smartpost.utilities.evs.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.evs.model.UspsPackageEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class UspsPackageEventDaoImpl extends NamedParameterJdbcTemplate implements UspsPackageEventDao {
	private static final Log log = LogFactory.getLog(UspsPackageEventDao.class);
	private static final String RETRIEVE_EVENTS = ClassPathResourceUtil.getString("dao/evs/retrieveEvents.sql");

	public UspsPackageEventDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	private RowMapper<UspsPackageEvent> EVENT_RM = (rs, i) -> {
		UspsPackageEvent event = new UspsPackageEvent();
		event.setPkgId(rs.getString("PKG_ID"));
		event.setEventCd(rs.getString("EVENT_CD"));
		event.setEventDt(rs.getDate("EVENT_DT"));
		event.setScanFacZip(rs.getString("SCAN_FAC_ZIP"));
		event.setScanFacName(rs.getString("SCAN_FAC_NAME"));
		event.setEventName(rs.getString("EVENT_NAME"));
		event.setDestCntryCode(rs.getString("DEST_CNTRY_CODE"));
		event.setDestZipCd4(rs.getString("DEST_ZIP_CD_4"));
		event.setMailerId(rs.getString("MAILER_ID"));
		event.setClientMailerId(rs.getString("CLIENT_MAILER_ID"));
		event.setDestZip(rs.getString("DEST_ZIP"));
		event.setMailerName(rs.getString("MAILER_NAME"));
		event.setRecipientName(rs.getString("RECIPIENT_NAME"));
		event.setEvsManifestSeq(rs.getBigDecimal("EVS_MANIFEST_SEQ"));
		event.setCustRefNbr(rs.getString("CUST_REF_NBR"));
		event.setChanlAppId(rs.getString("CHANL_APP_ID"));
		return event;
	};

	@Override
	public List<UspsPackageEvent> retrieveEvents(List<String> packageIds) {
		List<UspsPackageEvent> packageList = new ArrayList<>();
		int startPos = 0;
		int length;

		log.info("Number of packages to check in USPS_PACKAGE_EVENT [EVS]: " + packageIds.size());
		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			List<String> batch = packageIds.subList(startPos, startPos + length);
			parameters.addValue("packageIds", batch);
			packageList.addAll(query(RETRIEVE_EVENTS, parameters, EVENT_RM));
			startPos += length;
		}
		log.info("Number of records found: " + packageList.size());
		return packageList;
	}
}
