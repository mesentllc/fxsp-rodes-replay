package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VerifyReplay {
	private static final Log logger = LogFactory.getLog(VerifyReplay.class);
	private EDWDao edwDao;

	public VerifyReplay() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void process(List<String> filenames, boolean saveIt) throws SQLException, IOException {
		for (String filename : filenames) {
			logger.info("Filename: " + filename);
			if (saveIt) {
				new UpdateMasterReplayFile(edwDao, filename);
			}
			else {
				List<String> packageIds = MiscUtil.runThroughBusinessCommon(MiscUtil.retreivePackageIdRecordsFromFile(filename));
				edwDao.retrieveReleasedPackages(packageIds);
			}
		}
		closeConnections();
	}

	private void closeConnections() throws SQLException {
		edwDao.close();
	}

	public static void main(String[] args) throws SQLException, IOException {
		List<String> filenames;

		if (args.length != 1) {
			filenames = new ArrayList<>();
			filenames.add("/Support/2019-Jan-Replay/2019-04-04/packageIds.txt");
		}
		else {
			filenames = Arrays.asList(args);
		}
		VerifyReplay verifyReplay = new VerifyReplay();
		verifyReplay.process(filenames, false);
	}
}