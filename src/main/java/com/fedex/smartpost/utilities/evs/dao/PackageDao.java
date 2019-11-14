package com.fedex.smartpost.utilities.evs.dao;

import com.fedex.smartpost.utilities.evs.model.Package;

import java.util.List;

public interface PackageDao {
	List<Package> retrievePackages(List<String> packageIds);
}
