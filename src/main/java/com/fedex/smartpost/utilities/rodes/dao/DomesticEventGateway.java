package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.utilities.rodes.model.EventRecord;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface DomesticEventGateway {
	List<EventRecord> retrieveHubIds(List<String> packageIds);
	void close() throws SQLException;
	Set<String> lookForLC(Set<String> packageIds);
}
