package com.fedex.smartpost.utilities.rodes.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

public interface BillingGroupSummaryDao {
	Set<Date> getOutstandingScanDates();
	void close() throws SQLException;
}
