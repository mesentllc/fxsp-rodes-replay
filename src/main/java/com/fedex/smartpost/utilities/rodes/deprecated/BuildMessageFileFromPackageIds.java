package com.fedex.smartpost.utilities.rodes.deprecated;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.List;

public class BuildMessageFileFromPackageIds {
	private static final Log logger = LogFactory.getLog(BuildMessageFileFromPackageIds.class);
	private EDWDao edwDao;

	public BuildMessageFileFromPackageIds() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void processPackageIdsUsingSPEEDS(String filename) throws IOException {
		List<String> pkgList = MiscUtil.retreivePackageIdRecordsFromFile(filename);
		logger.info("Package Count: " + pkgList.size());
		logger.info("Message File Built: " + edwDao.buildFileUsingPackageIdsAndSPEEDS(pkgList, true));
	}

	private void processPackageIdsUsingPackageFact(String filename) throws IOException {
		List<String> pkgList = MiscUtil.retreivePackageIdRecordsFromFile(filename);
		logger.info("Package Count: " + pkgList.size());
		logger.info("Message File Built: " + edwDao.buildFileUsingPackageIdsAndPackageFact(pkgList, true));
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			args = new String[2];
			args[0] = "/Support/2020-03-09/pkgIds.txt";
		}
		BuildMessageFileFromPackageIds buildMessageFileFromPackageIds = new BuildMessageFileFromPackageIds();
		buildMessageFileFromPackageIds.processPackageIdsUsingPackageFact(args[0]);
	}


}
