package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CheckBillingPackages {
	private static final Log log = LogFactory.getLog(CheckBillingPackages.class);
	private BillingPackageDao billingPackageDao;

	public CheckBillingPackages() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
	}

	private void process(String[] args) throws IOException {
		Set<String> packageIds = MiscUtil.retrievePackageIdsFromFile(args[0]);
		log.info("Total package ids retrieved from source file: " + packageIds.size());
		List<BillingPackage> bpList = billingPackageDao.retrieveDups(new ArrayList<>(packageIds));
		Set<String> bpPackageIds = new TreeSet<>();
		for (BillingPackage bp : bpList) {
			bpPackageIds.add(bp.getFedexPkgId());
		}
		log.info("Total billing packages found: " + bpPackageIds.size());
	}

	public static void main(String[] args) throws IOException {
		// This application is to verify that Billing Packages exist, without all the "all-to-do" of VerifyRun.
		if (args.length != 1) {
			args = new String[1];
			args[0] = "d:/Support/2018-02-15/replayList.txt";
			CheckBillingPackages checkBillingPackages = new CheckBillingPackages();
			checkBillingPackages.process(args);
		}
	}
}
