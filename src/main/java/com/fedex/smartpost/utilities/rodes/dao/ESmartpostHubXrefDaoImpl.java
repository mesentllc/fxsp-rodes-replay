package com.fedex.smartpost.utilities.rodes.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class ESmartpostHubXrefDaoImpl extends NamedParameterJdbcTemplate implements ESmartpostHubXrefDao {
	private static final Log log = LogFactory.getLog(ESmartpostHubXrefDao.class);

	public ESmartpostHubXrefDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	private RowMapper<String> HUB_RM = (resultSet, i) -> resultSet.getString("FXG_HUB_ID");

	@Override
	public List<String> retrieveHubIds() {
		List<String> hubList = query("SELECT FXG_HUB_ID FROM SPRODS_SCHEMA.E_SMARTPOST_HUB_XREF", new MapSqlParameterSource(), HUB_RM);
		log.info("Found " + hubList.size() + " valid hubs from E_SMARTPOST_HUB_XREF [RODeS]");
		return hubList;
	}
}
