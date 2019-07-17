package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.rodes.model.EventRecord;
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

public class DomesticEventGatewayImpl extends NamedParameterJdbcTemplate implements DomesticEventGateway {
	private static final Log logger = LogFactory.getLog(DomesticEventGateway.class);
	private static final String RETRIEVE_DOMESTIC_EVENTS_FOR_PACKAGE_ID_SQL = ClassPathResourceUtil.getString("/dao/rodes/retrieveDomesticHubsForPackageId.sql");
	private DataSource dataSource;

	public DomesticEventGatewayImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	private RowMapper<EventRecord> HUB_ROW_MAPPER = (rs, i) -> {
		EventRecord eventRecord = new EventRecord();
		eventRecord.setFedexPackageId(rs.getString("FEDEX_PKG_ID_NM"));
		eventRecord.setHubCode(rs.getString("HUB_CD"));
		return eventRecord;
	};

	@Override
	public List<EventRecord> retrieveHubIds(List<String> packageIds) {
		MapSqlParameterSource parameters;
		List<EventRecord> eventRecords = new ArrayList<>();
		int startPos = 0;
		int length;

		logger.info("Attempting to load hub codes for " + packageIds.size() + " passed package ids.");
		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("packageIds", packageIds.subList(startPos, startPos + length));
			eventRecords.addAll(query(RETRIEVE_DOMESTIC_EVENTS_FOR_PACKAGE_ID_SQL, parameters, HUB_ROW_MAPPER));
			startPos += length;
		}
		logger.info(eventRecords.size() + " event records found.");
		return eventRecords;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
