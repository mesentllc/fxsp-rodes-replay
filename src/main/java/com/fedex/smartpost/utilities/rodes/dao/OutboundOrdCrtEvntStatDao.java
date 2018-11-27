package com.fedex.smartpost.utilities.rodes.dao;

import java.sql.SQLException;
import java.util.List;

public interface OutboundOrdCrtEvntStatDao {
	List<String> retrievePackages(List<String> packageIds);
	void close() throws SQLException;
}
