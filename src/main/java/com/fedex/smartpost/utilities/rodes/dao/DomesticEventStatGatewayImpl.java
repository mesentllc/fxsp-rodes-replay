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

public class DomesticEventStatGatewayImpl extends NamedParameterJdbcTemplate implements DomesticEventStatGateway {
	private static final Log logger = LogFactory.getLog(DomesticEventStatGateway.class);
	private static final String RETRIEVE_DOMESTIC_EVENTS_FOR_PACKAGE_ID_SQL = ClassPathResourceUtil.getString("/dao/rodes/retrieveDomesticEventsForPackageId.sql");
	private DataSource dataSource;

	public DomesticEventStatGatewayImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	private static RowMapper<EventRecord> DOMESTIC_EVENT_ROW_MAPPER = (rs, rowNum) -> {
		EventRecord eventRecord = new EventRecord();
		eventRecord.setPackageReturnEventId(rs.getLong("PKG_DOM_EVENT_ID"));
		eventRecord.setFedexPackageId(rs.getString("FEDEX_PKG_ID_NM"));
		eventRecord.setFedexCustomerAccountNumber(rs.getString("FEDEX_CUST_ACCT_NBR"));
		eventRecord.setPackageEventStatus(rs.getString("PKG_EVENT_STATUS_CD"));
		eventRecord.setPackageEventReason(rs.getString("PKG_EVENT_REASON_DESC"));
		eventRecord.setBillingPackageSeq(rs.getLong("BP_SEQ_ID"));
		return eventRecord;
	};

	@Override
	public List<EventRecord> retrieveEventRecords(List<String> packageIds) {
		MapSqlParameterSource parameters;
		List<EventRecord> eventRecords = new ArrayList<>();
		int startPos = 0;
		int length;

		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("packageIds", packageIds.subList(startPos, startPos + length));
			eventRecords.addAll(query(RETRIEVE_DOMESTIC_EVENTS_FOR_PACKAGE_ID_SQL, parameters, DOMESTIC_EVENT_ROW_MAPPER));
			startPos += length;
		}
		return eventRecords;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
