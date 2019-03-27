package com.fedex.smartpost.utilities.rodes.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UnmanifestedPackageDao {
	Map<String, Set<String>> getUnmanifestedStatusByPackageId(List<String> packageIds);
	void close() throws SQLException;
}
