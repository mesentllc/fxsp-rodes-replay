package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.utilities.rodes.model.EventRecord;

import java.sql.SQLException;
import java.util.List;

public interface DomesticEventStatGateway {
	List<EventRecord> retrieveEventRecords(List<String> packageIds);
	void close() throws SQLException;
}
