package com.fedex.smartpost.utilities.rodes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;

public class CheckPackageStatus {
	private static final Logger logger = Logger.getLogger(CheckPackageStatus.class);
	private BillingPackageDao billingPackageDao;

	public CheckPackageStatus() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
	}

	public static void main(String[] args) {
		CheckPackageStatus checkPackageStatus = new CheckPackageStatus();
		if (args.length != 1) {
			args = new String[1];
			args[0] = "D:/Support/2018-03-05/toReplay.txt";
		}
		checkPackageStatus.process(args[0]);
	}

	private void process(String filename) {
		List<String> packageIds = MiscUtil.retreivePackageIdRecordsFromFile(filename);
		Map<String, Set<String>> statusMap = billingPackageDao.retrieveStatus(packageIds);
		for (String status : statusMap.keySet()) {
			logger.info(status + " -> " + statusMap.get(status).size() + " package ids.");
		}
		dumpRecords(statusMap);
	}

	private void dumpRecords(Map<String, Set<String>> statusMap) {
		Set<String> errorPackages = statusMap.get("BP: 2, BG: 4");
		if (errorPackages != null) {
			for (String packageId : errorPackages) {
				logger.info(packageId);
			}
		}
	}
}