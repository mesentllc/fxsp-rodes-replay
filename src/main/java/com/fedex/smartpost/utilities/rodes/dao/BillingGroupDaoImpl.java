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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BillingGroupDaoImpl extends NamedParameterJdbcTemplate implements BillingGroupDao {
	private static final Log logger = LogFactory.getLog(BillingGroupDao.class);
	private static final String GET_RELEASED_SQL = ClassPathResourceUtil.getString("/dao/rodes/getReleasedBGs.sql");
	private static final String GET_RAC_XFER_CTRL_SEQ_SQL = ClassPathResourceUtil.getString("/dao/rodes/getRacXferCtrlSeqs.sql");
	private DataSource dataSource;

	public BillingGroupDaoImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	private static RowMapper<Long> BG_SEQ_MAPPER = (rs, rowNum) -> rs.getLong("bg_seq");

	private static RowMapper<Integer> RAC_XFER_CTRL_SEQ_MAPPER = (rs, rowNum) -> rs.getInt("RAC_XFER_CTRL_SEQ");

	@Override
	public List<Long> getReleased(List<Long> billingGroups) {
		MapSqlParameterSource parameters;
		List<Long> releasedBGs = new ArrayList<>();
		int startPos = 0;
		int length;

		while (startPos < billingGroups.size()) {
			length = Math.min(billingGroups.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("bgSeqs", billingGroups.subList(startPos, startPos + length));
			releasedBGs.addAll(query(GET_RELEASED_SQL, parameters, BG_SEQ_MAPPER));
			startPos += length;
		}
		logger.info("Found " + releasedBGs.size() + " released billing groups out of " + billingGroups.size());
		return releasedBGs;
	}

	@Override
	public List<Integer> getRacXferCntlSeqs(List<String> packageIds) {
		MapSqlParameterSource parameters;
		Set<Integer> racIds = new TreeSet<>();
		int startPos = 0;
		int length;

		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("pkgIds", packageIds.subList(startPos, startPos + length));
			racIds.addAll(query(GET_RAC_XFER_CTRL_SEQ_SQL, parameters, RAC_XFER_CTRL_SEQ_MAPPER));
			startPos += length;
		}
		logger.info("Found " + racIds.size() + " RAC_XFER_CTRL_SEQs for the " + packageIds.size() + " package ids.");
		return new ArrayList<>(racIds);
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
