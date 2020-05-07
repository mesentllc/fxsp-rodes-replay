package com.fedex.smartpost.utilities.rodes.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ECustomerMailerIdDaoImpl extends NamedParameterJdbcTemplate implements ECustomerMailerIdDao {
	private static final Log log = LogFactory.getLog(ECustomerMailerIdDao.class);
	private static final String POSTAL_CODE_BY_MID_SQL = ClassPathResourceUtil.getString("/dao/rodes/getPostalCodeByMid.sql");
	private static final String DEFAULT_HUB_BY_MID_SQL = ClassPathResourceUtil.getString("/dao/rodes/getDefaultHubByMID.sql");

	public ECustomerMailerIdDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Map<String, String> retrievePostalCodes(List<String> mailerIds) {
		Map<String, String> map = new HashMap<>();
		log.info("Trying to find " + mailerIds.size() + " postal codes in E_CUSTOMER [RODeS]");
		for (String mailerId : mailerIds) {
			map.put(mailerId, queryForObject(POSTAL_CODE_BY_MID_SQL, new MapSqlParameterSource("mid", mailerId), String.class));
		}
		log.info(map.size() + " postal codes found. [RODeS]");
		return map;
	}

	@Override
	public String retrieveHubByMID(String mailerId) {
		log.info("Attempting to read default hub for: " + mailerId);
		return queryForObject(DEFAULT_HUB_BY_MID_SQL, new MapSqlParameterSource("mid", mailerId), String.class);
	}
}
