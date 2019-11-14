package com.fedex.smartpost.utilities.evs.dao;

import com.fedex.smartpost.utilities.evs.model.UspsPackageEvent;

import java.util.List;

public interface UspsPackageEventDao {
	List<UspsPackageEvent> retrieveEvents(List<String> packageIds);
}
