package com.fedex.smartpost.utilities.transportation.dao;

import com.fedex.smartpost.utilities.rodes.model.BillingPackage;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface PackageDao {
	List<BillingPackage> retrievePackages(List<String> packageList);
	Set<String> findPackageWithLC(Set<String> packageSet);
	void close() throws SQLException;
}
