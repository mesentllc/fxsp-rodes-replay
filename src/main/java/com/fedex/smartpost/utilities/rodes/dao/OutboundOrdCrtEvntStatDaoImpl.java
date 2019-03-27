package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OutboundOrdCrtEvntStatDaoImpl extends NamedParameterJdbcTemplate implements OutboundOrdCrtEvntStatDao {
	private static final Log logger = LogFactory.getLog(OutboundOrdCrtEvntStatDao.class);
	private static final String EXISTING_OC_EVENTS = ClassPathResourceUtil.getString("dao/rodes/ExistingOCEvents.sql");
	private DataSource dataSource;

	public OutboundOrdCrtEvntStatDaoImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	@Override
	public List<String> retrievePackages(List<String> packageIds) {
		List<String> ocRecords = new ArrayList<>();
		int curPtr = 0;
		int batchSize;

		logger.info("Total package ids to check in OUTBOUND_ORD_CRT_EVNT_STAT [RODeS]: " + packageIds.size());
		while (packageIds.size() > curPtr) {
			batchSize = Math.min(1000, packageIds.size() - curPtr);
			MapSqlParameterSource params = new MapSqlParameterSource().addValue("packageIds", packageIds.subList(curPtr, curPtr + batchSize));
			ocRecords.addAll(queryForList(EXISTING_OC_EVENTS, params, String.class));
			curPtr += batchSize;
		}
		logger.info("Total package ids found in OUTBOUND_ORD_CRT_EVNT_STAT [RODeS]: " + ocRecords.size());
		return ocRecords;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
