package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.utilities.rodes.model.BillingPackage;

import java.sql.SQLException;
import java.util.List;

public interface PackageDetailXfer {
	List<BillingPackage> getReleasedPackages(List<String> packageIds, List<Integer> racIds);
	void close() throws SQLException;
}
