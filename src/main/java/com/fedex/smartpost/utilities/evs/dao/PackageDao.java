package com.fedex.smartpost.utilities.evs.dao;

import com.fedex.smartpost.utilities.evs.model.Package;

import java.util.List;
import java.util.Set;

public interface PackageDao {
	List<Package> retrievePackages(Set<String> packageIds);
}
