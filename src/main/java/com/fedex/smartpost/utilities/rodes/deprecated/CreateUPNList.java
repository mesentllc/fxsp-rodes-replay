package com.fedex.smartpost.utilities.rodes.deprecated;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CreateUPNList {
	private static final Logger logger = LogManager.getLogger(CreateUPNList.class);
	private EDWDao edwDao;

	public CreateUPNList() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void getCounts(String filename, boolean buildFile) {
		List<String> packageIds = MiscUtil.retreivePackageIdRecordsFromFile(filename);
		BufferedWriter bw = null;

		try {
			if (buildFile) {
				bw = new BufferedWriter(new FileWriter("/unreleased.txt"));
			}
			EDWResults edwResults = edwDao.retrieveUnreleasedPackageIdsAndUPNs(packageIds);
			for (Date date : edwResults.getScanDates()) {
				for (Message message : edwResults.getMessages(date)) {
					if (buildFile) {
						bw.write(message.getUpn() + "\r\n");
					}
				}
			}
			if (buildFile) {
				bw.close();
			}
		}
		catch (IOException ioe) {
			logger.error(ioe);
		}
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			args = new String[1];
			args[0] = "/rfs319572.txt";
		}
		CreateUPNList createUPNList = new CreateUPNList();
		createUPNList.getCounts(args[0], true);
	}
}
