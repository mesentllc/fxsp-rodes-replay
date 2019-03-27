package com.fedex.smartpost.utilities.transportation.dao;

import com.fedex.smartpost.utilities.rodes.model.BillingPackage;

import java.sql.SQLException;
import java.util.List;

public interface PackageHistoryDao {
	List<BillingPackage> retrievePackages(List<String> packageList);
	void close() throws SQLException;
}
