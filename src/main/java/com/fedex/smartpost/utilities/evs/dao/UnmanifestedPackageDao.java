package com.fedex.smartpost.utilities.evs.dao;

import com.fedex.smartpost.utilities.evs.model.Unmanifested;

import java.util.List;

public interface UnmanifestedPackageDao {
	List<Unmanifested> retrievePackages(List<String> packageIds);
}
