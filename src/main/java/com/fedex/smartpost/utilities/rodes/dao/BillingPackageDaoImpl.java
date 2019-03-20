package com.fedex.smartpost.utilities.rodes.dao;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BillingPackageDaoImpl extends NamedParameterJdbcTemplate implements BillingPackageDao {
	private static final Log logger = LogFactory.getLog(BillingPackageDao.class);
	private static final SimpleDateFormat timeStampSDF = new SimpleDateFormat("MM/dd/yyyy");
	private static final String RETRIEVE_DUPS_SQL = ClassPathResourceUtil.getString("/dao/rodes/retrieveDups.sql");
    private static final String RETRIEVE_SCAN_DATES_SQL = ClassPathResourceUtil.getString("/dao/rodes/retrievePackageScanDates.sql");
	private static final String RETRIEVE_STATUS_SQL = ClassPathResourceUtil.getString("/dao/rodes/retrievePackageStatus.sql");
	private static final String PACKAGE_ID_COUNT = ClassPathResourceUtil.getString("/dao/rodes/retrievePackageIdCount.sql");
	private DataSource dataSource;

	public BillingPackageDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    private class StatusClass {
		String status;
		String packageId;
    }

    private RowMapper<StatusClass> STATUS_ROW_MAPPER = (rs, rowNum) -> {
		StatusClass sc = new StatusClass();
		sc.packageId = rs.getString("FEDEX_PKG_ID");
		sc.status = "BP: " + rs.getLong("BP_STAT") + ", BG: " + rs.getString("BG_STAT");
		return sc;
    };

	private static RowMapper<Date> DATE_LIST_MAPPER = (rs, rowNum) -> {
		try {
			return timeStampSDF.parse(rs.getString(1));
		}
		catch (ParseException e) {
			logger.error(e);
		}
		return null;
	};

	private static RowMapper<BillingPackage> BP_LIST_MAPPER = (rs, rowNum) -> {
		BillingPackage billingPackage = new BillingPackage();
		billingPackage.setFedexPkgId(rs.getString("fedex_pkg_id"));
		billingPackage.setDeliveryType(rs.getString("delivery_type"));
		billingPackage.setStatus(rs.getString("status"));
		billingPackage.setDeliveryDt(rs.getTimestamp("delivery_dt"));
		billingPackage.setStatusDt(rs.getTimestamp("status_dt"));
		billingPackage.setOriginHubScanDt(rs.getTimestamp("origin_hub_scan_dt"));
		billingPackage.setCreatedDt(rs.getTimestamp("created_dt"));
		billingPackage.setBillingGroup(rs.getLong("bg_seq"));
		billingPackage.setFxspOriginLocCd("fxsp_orig_loc_cd");
		billingPackage.setOriginHubCd(rs.getString("origin_hub_cd"));
		return billingPackage;
	};

	@Override
	public List<BillingPackage> retrieveDups(List<String> packageList) {
        MapSqlParameterSource parameters;
		List<String> deliveryTypes = new ArrayList<>(3);
		List<BillingPackage> existingPackages = new ArrayList<>();
		int startPos = 0;
		int length;

		deliveryTypes.add("U");
		deliveryTypes.add("C");
		deliveryTypes.add("F");
		logger.info("Total package ids to check in BILLING_PACKAGE: " + packageList.size());
		while (startPos < packageList.size()) {
			length = Math.min(packageList.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
	        parameters.addValue("pkgList", packageList.subList(startPos, startPos + length));
			parameters.addValue("deliveryTypes", deliveryTypes);
			existingPackages.addAll(query(RETRIEVE_DUPS_SQL, parameters, BP_LIST_MAPPER));
			startPos += length;
		}
		logger.info("Total package ids found in BILLING_PACKAGE: " + existingPackages.size());
        return existingPackages;
    }

	@Override
	public Set<Date> retrieveScanDates(List<String> packageList) {
        MapSqlParameterSource parameters;
        List<String> deliveryTypes = new ArrayList<>(1);
        Set<Date> scanDateSet = new TreeSet<>();
        int startPos = 0;
        int length;

        deliveryTypes.add("U");
        deliveryTypes.add("C");
        deliveryTypes.add("F");
        while (startPos < packageList.size()) {
            length = Math.min(packageList.size() - startPos, 1000);
            parameters = new MapSqlParameterSource();
            parameters.addValue("pkgList", packageList.subList(startPos, startPos + length));
            parameters.addValue("deliveryTypes", deliveryTypes);
            scanDateSet.addAll(query(RETRIEVE_SCAN_DATES_SQL, parameters, DATE_LIST_MAPPER));
            startPos += length;
        }
        return scanDateSet;
    }

	@Override
	public Map<String, Set<String>> retrieveStatus(List<String> packageList) {
		Map<String, Set<String>> returnMap = new TreeMap<>();
		MapSqlParameterSource parameters;
		List<StatusClass> statusClassList = new ArrayList<>();
		int startPos = 0;
		int length;

		while (startPos < packageList.size()) {
			length = Math.min(packageList.size() - startPos, 1000);
			logger.info("Processing " + (startPos + length) + " of " + packageList.size());
			parameters = new MapSqlParameterSource();
			parameters.addValue("pkgList", packageList.subList(startPos, startPos + length));
			statusClassList.addAll(query(RETRIEVE_STATUS_SQL, parameters, STATUS_ROW_MAPPER));
			startPos += length;
		}
		logger.info("Sorting " + statusClassList.size() + " status records.");
		for (StatusClass statusClass : statusClassList) {
			Set<String> packageIdSet = returnMap.computeIfAbsent(statusClass.status, k -> new TreeSet<>());
			packageIdSet.add(statusClass.packageId);
		}
		return returnMap;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}
