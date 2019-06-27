package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VerifyReplay {
	private static final Log logger = LogFactory.getLog(VerifyReplay.class);
	private EDWDao edwDao;

	private VerifyReplay() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void process(List<String> filenames) throws SQLException {
		for (String filename : filenames) {
			logger.info("Filename: " + filename);
			List<String> packageIds = MiscUtil.runThroughBusinessCommon(MiscUtil.retreivePackageIdRecordsFromFile(filename));
			edwDao.retrieveReleasedPackages(packageIds);
		}
		closeConnections();
	}

	private void closeConnections() throws SQLException {
		edwDao.close();
	}

	public static void main(String[] args) throws SQLException {
		List<String> filenames = new ArrayList<>();

		filenames.add("/Support/2019-Feb-Replay/2019-04-11/packageIds.txt");
		VerifyReplay verifyReplay = new VerifyReplay();
		verifyReplay.process(filenames);
	}
}