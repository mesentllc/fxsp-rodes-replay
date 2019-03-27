package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.utilities.rodes.model.EPDIRecord;

import java.sql.SQLException;
import java.util.List;

public interface BillingPackageHistoryGateway {
	List<EPDIRecord> retrieveBillingPackageHistoryRecordsByPackageIds(List<String> packageIds);
	void close() throws SQLException;
}
