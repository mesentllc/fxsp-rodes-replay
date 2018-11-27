package com.fedex.smartpost.utilities.rodes.deprecated;

import java.io.IOException;
import java.util.List;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BuildMessageFileFromPackageIds {
	private static final Logger logger = LogManager.getLogger(BuildMessageFileFromPackageIds.class);
	private EDWDao edwDao;

	public BuildMessageFileFromPackageIds() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void processPackageIdsUsingSPEEDS(String filename) throws IOException {
		List<String> pkgList = MiscUtil.retreivePackageIdRecordsFromFile(filename);
		logger.info("Package Count: " + pkgList.size());
		logger.info("Message File Built: " + edwDao.buildFileUsingPackageIdsAndSPEEDS(pkgList, false));
	}

	private void processPackageIdsUsingPackageFact(String filename) throws IOException {
		List<String> pkgList = MiscUtil.retreivePackageIdRecordsFromFile(filename);
		logger.info("Package Count: " + pkgList.size());
		logger.info("Message File Built: " + edwDao.buildFileUsingPackageIdsAndPackageFact(pkgList, true));
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			args = new String[2];
			args[0] = "/Support/rfs319572/rfs319572.txt";
		}
		BuildMessageFileFromPackageIds buildMessageFileFromPackageIds = new BuildMessageFileFromPackageIds();
		buildMessageFileFromPackageIds.processPackageIdsUsingPackageFact(args[0]);
	}


}