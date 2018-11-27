package com.fedex.smartpost.utilities.rodes.dao;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import java.awt.dnd.DropTarget;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class PackageDetailXferImpl extends NamedParameterJdbcTemplate implements PackageDetailXfer {
	private static final String PACKAGE_ID_SQL = ClassPathResourceUtil.getString("/dao/rodes/xferRatingPackageId.sql");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private DataSource dataSource;

	private static RowMapper<BillingPackage> BP_LIST_MAPPER = (rs, rowNum) -> {
		BillingPackage billingPackage = new BillingPackage();
		billingPackage.setFedexPkgId(rs.getString("sp_package_num").trim());
		billingPackage.setOriginHubCd(rs.getString("sp_sort_location").trim());
		billingPackage.setFxspOriginLocCd(rs.getString("sp_fxsp_origin_location"));
		try {
			billingPackage.setOriginHubScanDt(sdf.parse(rs.getString("sp_sort_start_date")));
		}
		catch (ParseException e) {
		}
		return billingPackage;
	};

	private List<String> padList(List<String> strings, int len) {
		List<String> paddedList = new ArrayList<>(strings.size());
		for (String string : strings) {
			paddedList.add(StringUtils.rightPad(string, len));
		}
		return paddedList;
	}

	public PackageDetailXferImpl(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	@Override
	public List<BillingPackage> getReleasedPackages(List<String> packageIds, List<Integer> racIds) {
		MapSqlParameterSource parameters;
		List<BillingPackage> existingPackages = new ArrayList<>();
		int startPos = 0;
		int length;

		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			parameters = new MapSqlParameterSource();
			parameters.addValue("packageIds", padList(packageIds.subList(startPos, startPos + length), 24));
			parameters.addValue("racIds", racIds);
			existingPackages.addAll(query(PACKAGE_ID_SQL, parameters, BP_LIST_MAPPER));
			startPos += length;
		}
		return existingPackages;
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}
}

