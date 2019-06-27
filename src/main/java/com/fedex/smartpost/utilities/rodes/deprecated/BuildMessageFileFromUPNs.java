package com.fedex.smartpost.utilities.rodes.deprecated;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class BuildMessageFileFromUPNs {
	private static final Log logger = LogFactory.getLog(BuildMessageFileFromUPNs.class);
	private EDWDao edwDao;

	public BuildMessageFileFromUPNs() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void processUPNsUsingPackageFact(String filename) throws IOException, ParseException {
//		List<Long> pkgList = MiscUtil.retreiveUPNsFromFile(filename, buildScanDates(scanDates));
//		List<Long> pkgList = MiscUtil.retreiveUPNsFromFile(filename, new TreeSet<>());
		List<Long> pkgList = MiscUtil.retreiveUPNsFromFile(filename);
		logger.info("Package Count: " + pkgList.size());
		logger.info("Message File Built: " + edwDao.buildFileUsingUPNsAndPackageFact(pkgList, true));
	}

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length != 1) {
			args = new String[1];
			// This file was created from the CreateUPNList method
			// args[0] = "/unreleased.txt";
			args[0] = "/Support/Kienast/upns.txt";
		}
		BuildMessageFileFromUPNs buildMessageFileFromUPNs = new BuildMessageFileFromUPNs();
		buildMessageFileFromUPNs.processUPNsUsingPackageFact(args[0]);
	}
}
