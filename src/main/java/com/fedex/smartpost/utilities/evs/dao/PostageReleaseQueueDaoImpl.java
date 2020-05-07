package com.fedex.smartpost.utilities.evs.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PostageReleaseQueueDaoImpl extends NamedParameterJdbcTemplate implements PostageReleaseQueueDao {
	private static final Log log = LogFactory.getLog(PostageReleaseQueueDao.class);
	private static final String RETRIEVE_PACKAGES = ClassPathResourceUtil.getString("dao/evs/retrievePackageFromQueue.sql");

	public PostageReleaseQueueDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Set<String> getPackageIds(Set<String> packageIds) {
		List<String> packageList = new ArrayList<>(packageIds);
		Set<String> packageSet = new TreeSet<>();
		int startPos = 0;
		int length;

		log.info("Number of packages to check in POSTAGE_RELEASE_QUEUE [EVS]: " + packageIds.size());
		while (startPos < packageIds.size()) {
			length = Math.min(packageIds.size() - startPos, 1000);
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			List<String> batch = packageList.subList(startPos, startPos + length);
			parameters.addValue("packageIds", batch);
			packageSet.addAll(queryForList(RETRIEVE_PACKAGES, parameters, String.class));
			startPos += length;
		}
		log.info("Number of packages found: " + packageList.size());
		return packageSet;
	}
}
