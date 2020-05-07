package com.fedex.smartpost.utilities.rodes.dao;

import java.util.List;
import java.util.Map;

public interface ECustomerMailerIdDao {
	Map<String, String> retrievePostalCodes(List<String> mailerIds);
	String retrieveHubByMID(String mailerId);
}
