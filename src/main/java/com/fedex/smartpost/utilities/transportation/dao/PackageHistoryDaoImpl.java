package com.fedex.smartpost.utilities.transportation.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
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

public class PackageHistoryDaoImpl extends NamedParameterJdbcTemplate implements PackageHistoryDao {
	private static final Log log = LogFactory.getLog(PackageHistoryDao.class);
	private static final String PACKAGE_HIST_IN_TRANS = ClassPathResourceUtil.getString("/dao/transportation/retrievePackageHistories.sql");
	private DataSource dataSource;

	public PackageHistoryDaoImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	private static RowMapper<BillingPackage> BP_LIST_MAPPER = (rs, rowNum) -> {
		BillingPackage billingPackage = new BillingPackage();
		billingPackage.setFedexPkgId(rs.getString("package_id"));
/*
		billingPackage.setDeliveryType(rs.getString("delivery_type"));
		billingPackage.setStatus(rs.getString("status"));
		billingPackage.setDeliveryDt(rs.getTimestamp("delivery_dt"));
		billingPackage.setStatusDt(rs.getTimestamp("status_dt"));
		billingPackage.setOriginHubScanDt(rs.getTimestamp("origin_hub_scan_dt"));
		billingPackage.setCreatedDt(rs.getTimestamp("created_dt"));
		billingPackage.setBillingGroup(rs.getLong("bg_seq"));
		billingPackage.setFxspOriginLocCd("fxsp_orig_loc_cd");
		billingPackage.setOriginHubCd(rs.getString("origin_hub_cd"));
*/
		return billingPackage;
	};

	@Override
	public List<BillingPackage> retrievePackages(List<String> packageList) {
		MapSqlParameterSource parameters;
		List<BillingPackage> existingPackages = new ArrayList<>();
		int startPos = 0;
		int length;

		log.info("Total package ids to check in PACKAGE_HISTORY [TRANS]: " + packageList.size());
		while (startPos < packageList.size()) {
			length = Math.min(packageList.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("pkgList", packageList.subList(startPos, startPos + length));
			existingPackages.addAll(query(PACKAGE_HIST_IN_TRANS, parameters, BP_LIST_MAPPER));
			startPos += length;
		}
		log.info("Total package ids found in PACKAGE_HISTORY [TRANS]: " + existingPackages.size());
		return existingPackages;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
