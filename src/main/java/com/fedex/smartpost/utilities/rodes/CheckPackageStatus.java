package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CheckPackageStatus {
	private static final Log logger = LogFactory.getLog(CheckPackageStatus.class);
	private BillingPackageDao billingPackageDao;

	public CheckPackageStatus() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
	}

	public static void main(String[] args) {
		CheckPackageStatus checkPackageStatus = new CheckPackageStatus();
		if (args.length != 1) {
			args = new String[1];
			args[0] = "/Support/2020-03-09/pkgIds.txt";
		}
		checkPackageStatus.process(args[0]);
	}

	private void process(String filename) {
		List<String> packageIds = MiscUtil.retrievePackageIdRecordsFromFile(filename);
		Map<String, Set<String>> statusMap = billingPackageDao.retrieveStatus(packageIds);
		for (String status : statusMap.keySet()) {
			logger.info(status + " -> " + statusMap.get(status).size() + " package ids.");
		}
//		dumpRecords(statusMap);
		dumpMissingRecords(packageIds, statusMap);
	}

	private void dumpMissingRecords(List<String> packageIds, Map<String, Set<String>> statusMap) {
		Set<String> foundPackages = new TreeSet<>();
		for (String key : statusMap.keySet()) {
			foundPackages.addAll(statusMap.get(key));
		}
		for (String packageId : packageIds) {
			if (!foundPackages.contains(packageId)) {
				logger.info(packageId);
			}
		}
	}

	private void dumpRecords(Map<String, Set<String>> statusMap) {
		Set<String> errorPackages = statusMap.get("BP: 2, BG: 4");
//		Set<String> errorPackages = statusMap.get("BP: 5, BG: null");
		if (errorPackages != null) {
			for (String packageId : errorPackages) {
				logger.info(packageId);
			}
		}
	}
}
