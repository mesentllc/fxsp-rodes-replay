package com.fedex.smartpost.utilities.rodes.dao;

import java.sql.SQLException;
import java.util.List;

public interface BillingGroupDao {
	List<Long> getReleased(List<Long> billingGroups);
	List<Integer> getRacXferCntlSeqs(List<String> packageIds);
	void close() throws SQLException;
}
