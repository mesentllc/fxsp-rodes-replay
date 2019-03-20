package com.fedex.smartpost.utilities.transportation.dao;

import com.fedex.smartpost.utilities.rodes.model.BillingPackage;

import java.util.List;

public interface PackageDao {
	List<BillingPackage> retrievePackages(List<String> packageList);
}
