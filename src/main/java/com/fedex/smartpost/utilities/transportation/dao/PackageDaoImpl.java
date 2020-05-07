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
import java.util.Set;
import java.util.TreeSet;

public class PackageDaoImpl extends NamedParameterJdbcTemplate implements PackageDao {
	private static final Log log = LogFactory.getLog(PackageDao.class);
	private static final String PACKAGES_IN_TRANS = ClassPathResourceUtil.getString("/dao/transportation/retrievePackages.sql");
	private static final String PACKAGES_WITH_LC = ClassPathResourceUtil.getString("/dao/transportation/packagesWithLCs.sql");
	private DataSource dataSource;

	public PackageDaoImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	private static RowMapper<BillingPackage> BP_LIST_MAPPER = (rs, rowNum) -> {
		BillingPackage billingPackage = new BillingPackage();
		billingPackage.setFedexPkgId(rs.getString("fedex_pkg_id"));
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

		log.info("Total package ids to check in PACKAGE [TRANS]: " + packageList.size());
		while (startPos < packageList.size()) {
			length = Math.min(packageList.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("pkgList", packageList.subList(startPos, startPos + length));
			existingPackages.addAll(query(PACKAGES_IN_TRANS, parameters, BP_LIST_MAPPER));
			startPos += length;
		}
		log.info("Total package ids found in PACKAGE [TRANS]: " + existingPackages.size());
		return existingPackages;
	}

	@Override
	public Set<String> findPackageWithLC(Set<String> packageSet) {
		MapSqlParameterSource parameters;
		Set<String> packageWithLCs = new TreeSet<>();
		List<String> packageList = new ArrayList<>(packageSet);
		int startPos = 0;
		int length;

		log.info("Total package ids to check in PACKAGE for LC [TRANS]: " + packageList.size());
		while (startPos < packageList.size()) {
			length = Math.min(packageList.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("pkgList", packageList.subList(startPos, startPos + length));
			packageWithLCs.addAll(queryForList(PACKAGES_WITH_LC, parameters, String.class));
			startPos += length;
			log.info("Processing records " + startPos + " of " + packageList.size());
		}
		log.info("Processed " + packageList.size() + " records.");
		log.info("Total package ids found in PACKAGE with LC [TRANS]: " + packageWithLCs.size());
		return packageWithLCs;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
