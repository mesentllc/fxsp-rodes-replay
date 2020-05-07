package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageHistoryGateway;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EPDIRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class CheckInHistory {
	private static final Log log = LogFactory.getLog(CheckInHistory.class);
	private BillingPackageDao billingPackageDao;
	private BillingPackageHistoryGateway historyGateway;

	public CheckInHistory() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		historyGateway = (BillingPackageHistoryGateway)context.getBean("billingPackageHistoryGateway");
	}

	public static void main(String[] args) {
		CheckInHistory checkInHistory = new CheckInHistory();
		checkInHistory.process("/Support/2020-01-21/Dups/pkgIds.txt");
	}

	private void process(String inFile) {
		List<String> packageIds = MiscUtil.retrievePackageIdRecordsFromFile(inFile);
		List<String> tempIds = new ArrayList<>();
		List<BillingPackage> billingPackages = billingPackageDao.retrieveDups(packageIds);
		log.info("Number of records found in BILLING_PACKAGE [RODeS]: " + billingPackages.size());
		for (BillingPackage record : billingPackages) {
			tempIds.add(record.getFedexPkgId());
		}
		packageIds.removeAll(tempIds);
		List<EPDIRecord> records = historyGateway.retrieveBillingPackageHistoryRecordsByPackageIds(packageIds);
		log.info("Number of records found in BILLING_PACKAGE_HISTORY [RODeS]: " + records.size());
		tempIds = new ArrayList<>();
		for (EPDIRecord record : records) {
			tempIds.add(record.getFedexPkgId());
		}
		packageIds.removeAll(tempIds);
		for (String pkgId : packageIds) {
			log.info("Missing: " + pkgId);
		}
	}
}
