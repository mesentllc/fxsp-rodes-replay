package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.utilities.rodes.model.BillingPackage;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BillingPackageDao {
    List<BillingPackage> retrieveDups(List<String> packageList);
	List<String> retrieveReleased(List<String> packageList);
	List<String> retrieveStaged(List<String> packageList);
    Set<Date> retrieveScanDates(List<String> packageList);
    Map<String, Set<String>> retrieveStatus(List<String> packageList);
    void close() throws SQLException;
}
