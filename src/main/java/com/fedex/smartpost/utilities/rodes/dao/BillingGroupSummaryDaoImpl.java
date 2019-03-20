package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BillingGroupSummaryDaoImpl extends NamedParameterJdbcTemplate implements BillingGroupSummaryDao {
	private static final Log logger = LogFactory.getLog(BillingGroupSummaryDao.class);
	private static final String RETRIEVE_SCAN_DATES_SQL = ClassPathResourceUtil.getString("dao/rodes/retrieveScanDates.sql");
	private DataSource dataSource;

	public BillingGroupSummaryDaoImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	private RowMapper<String> SCAN_DATE_ROW_MAPPER = (resultSet, i) -> resultSet.getString("BG_SCAN_DATE_TXT");

	@Override
	public Set<Date> getOutstandingScanDates() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		List<String> scanDates = query(RETRIEVE_SCAN_DATES_SQL, new MapSqlParameterSource(), SCAN_DATE_ROW_MAPPER);
		Set<Date> scanDateSet = new TreeSet<>();
		for (String scans : scanDates) {
			String[] dates = scans.split(",");
			for (String item : dates) {
				try {
					scanDateSet.add(sdf.parse(item.trim()));
				}
				catch (ParseException e) {
					logger.info(item + " threw an exception", e);
				}
			}
		}
		return scanDateSet;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
