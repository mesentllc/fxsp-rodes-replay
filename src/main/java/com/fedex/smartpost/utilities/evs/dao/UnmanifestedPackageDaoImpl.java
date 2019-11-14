package com.fedex.smartpost.utilities.evs.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.evs.model.Unmanifested;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class UnmanifestedPackageDaoImpl extends NamedParameterJdbcTemplate implements UnmanifestedPackageDao {
	private static final Log log = LogFactory.getLog(UnmanifestedPackageDao.class);
	private static final String UNMANIFEST_PACKAGES = ClassPathResourceUtil.getString("dao/evs/retrieveUnmanPackages.sql");

	public UnmanifestedPackageDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	private RowMapper<Unmanifested> UNMANIFESTED_RM = (rs, row) -> {
		Unmanifested unmanifested = new Unmanifested();
		unmanifested.setPkgId(rs.getString("PKG_ID"));
		unmanifested.setChanlAppId(rs.getString("CHANL_APP_ID"));
		unmanifested.setUnmanStat(rs.getString("UNMAN_STAT"));
		unmanifested.setPtsXmtDt(rs.getDate("PTS_XMT_DT"));
		unmanifested.setScanDt(rs.getDate("SCAN_DT"));
		unmanifested.setPstgAmt(rs.getBigDecimal("PSTG_AMT"));
		unmanifested.setClientMailerId(rs.getString("CLIENT_MAILER_ID"));
		unmanifested.setClientMailerNm(rs.getString("CLIENT_MAILER_NM"));
		unmanifested.setScanFacPstlCd(rs.getString("SCAN_FAC_PSTL_CD"));
		unmanifested.setRectSrc(rs.getString("RECT_SRC"));
		unmanifested.setMailClass(rs.getString("MAIL_CLASS"));
		return unmanifested;
	};

	@Override
	public List<Unmanifested> retrievePackages(List<String> packageIds) {
		List<Unmanifested> unmanifestedList = new ArrayList<>();
		int startPos = 0;
		int length;

		log.info("Number of packages to check in UNMANIFESTED_PACKAGE [EVS]: " + packageIds.size());
		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			List<String> batch = packageIds.subList(startPos, startPos + length);
			parameters.addValue("packageIds", batch);
			unmanifestedList.addAll(query(UNMANIFEST_PACKAGES, parameters, UNMANIFESTED_RM));
			startPos += length;
		}
		log.info("Number of package ids found: " + unmanifestedList.size());
		return unmanifestedList;
	}
}
