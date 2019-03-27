package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.rodes.model.UnmanifestedModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class UnmanifestedPackageDaoImpl extends NamedParameterJdbcTemplate implements UnmanifestedPackageDao {
	private static final Log log = LogFactory.getLog(UnmanifestedPackageDao.class);
	private static final String RETRIEVE_UNMANIFESTED_BY_PACKAGE_ID = ClassPathResourceUtil.getString("dao/rodes/retrieveUnmanifestedByPackageId.sql");

	private DataSource dataSource;

	private static RowMapper<UnmanifestedModel> UNMANIFESTED_ROW_MAPPER = (resultSet, i) -> {
		UnmanifestedModel model = new UnmanifestedModel();
		model.setPkgId(resultSet.getString("PKG_ID"));
		model.setChanlAppId(resultSet.getString("CHANL_APP_ID"));
		model.setUnmanStat(resultSet.getString("UNMAN_STAT"));
		model.setFedexAcctNbr(resultSet.getLong("FEDEX_ACCT_NBR"));
		model.setPtsXmtDt(resultSet.getDate("PTS_XMT_DT"));
		model.setScanDt(resultSet.getDate("SCAN_DT"));
		model.setPstgAmt(resultSet.getBigDecimal("PSTG_AMT"));
		model.setClientMailerId(resultSet.getString("CLIENT_MAILER_ID"));
		model.setClientMailerNm(resultSet.getString("CLIENT_MAILER_NM"));
		model.setScanFacPstlCd(resultSet.getString("SCAN_FAC_PSTL_CD"));
		model.setMailClass(resultSet.getString("MAIL_CLASS"));
		return model;
	};

	public UnmanifestedPackageDaoImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	@Override
	public Map<String, Set<String>> getUnmanifestedStatusByPackageId(List<String> packageIds) {
		MapSqlParameterSource parameters;
		List<UnmanifestedModel> eventRecords = new ArrayList<>();
		Map<String, Set<String>> map = new TreeMap<>();
		Set<String> packageSet;
		int startPos = 0;
		int length;

		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("packageIds", packageIds.subList(startPos, startPos + length));
			eventRecords.addAll(query(RETRIEVE_UNMANIFESTED_BY_PACKAGE_ID, parameters, UNMANIFESTED_ROW_MAPPER));
			startPos += length;
		}
		for (UnmanifestedModel model : eventRecords) {
			if (map.containsKey(model.getUnmanStat())) {
				packageSet = map.get(model.getUnmanStat());
			}
			else {
				packageSet = new TreeSet<>();
				map.put(model.getUnmanStat(), packageSet);
			}
			packageSet.add(model.getPkgId());
		}
		return map;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
