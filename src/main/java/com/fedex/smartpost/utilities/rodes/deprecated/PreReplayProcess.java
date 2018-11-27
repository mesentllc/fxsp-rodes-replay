package com.fedex.smartpost.utilities.rodes.deprecated;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingGroupDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageEpdiEventGateway;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageHistoryGateway;
import com.fedex.smartpost.utilities.rodes.dao.ReturnsEventStatGateway;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.EventRecord;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PreReplayProcess {
	private static final Logger logger = LogManager.getLogger(PreReplayProcess.class);
	private BillingPackageDao billingPackageDao;
	private BillingGroupDao billingGroupDao;
	private BillingPackageEpdiEventGateway billingPackageEpdiEventGateway;
	private BillingPackageHistoryGateway billingPackageHistoryGateway;
	private ReturnsEventStatGateway returnsEventStatGateway;
	private EDWDao edwDao;
	private String releasedCount;

	public PreReplayProcess() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		billingGroupDao = (BillingGroupDao)context.getBean("billingGroupDao");
		billingPackageEpdiEventGateway = (BillingPackageEpdiEventGateway)context.getBean("billingPackageEpdiEventGateway");
		billingPackageHistoryGateway = (BillingPackageHistoryGateway)context.getBean("billingPackageHistoryGateway");
		returnsEventStatGateway = (ReturnsEventStatGateway)context.getBean("returnsEventStatGateway");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void getCounts(String filename, boolean buildFile) {
		List<String> packageIds = MiscUtil.retreivePackageIdRecordsFromFile(filename);
		List<BillingPackage> existingPackages;
		List<EventRecord> eventRecords;
		Set<String> unreleasedSet = new HashSet<>();
		Set<String> returnsEventPackageSet = new HashSet<>();

		logger.info("Number of unique packages in file: " + packageIds.size());
		logger.info("Number of package ids found in BILLING_PACKAGE_EPDI_EVENT: " + billingPackageEpdiEventGateway.retrieveEPDIRecordsByPackageIds(packageIds).size());
		logger.info("Number of package ids found in BILLING_PACKAGE_HISTORY: " + billingPackageHistoryGateway.retrieveEPDIRecordsByPackageIds(packageIds).size());
		existingPackages = billingPackageDao.retrieveDups(packageIds);
		logger.info("Number of package ids found in BILLING_PACKAGE: " + existingPackages.size());
		discoverReleasedBPs(existingPackages);
		logger.info("Number of package ids marked as RELEASED: " + MiscUtil.getReleasedCount(existingPackages));
		logger.info("Number of package ids marked as GROUPED: " + MiscUtil.getGroupedCount(existingPackages));
		Set<Date> scanDates = billingPackageDao.retrieveScanDates(packageIds);
		logger.info("Number of Scan Dates for package ids in replay list: " + scanDates.size());
		StringBuilder sb = new StringBuilder();
		for (Date date : scanDates) {
			sb.append(MiscUtil.SDF.format(date) + ", ");
		}
		logger.info(sb.toString());
		logger.info("Number of package ids NOT FOUND in BILLING_PACKAGE: " + (packageIds.size() - existingPackages.size()));
		dumpToFile(buildFile, packageIds, existingPackages);
		eventRecords = returnsEventStatGateway.retrieveEventRecords(packageIds);
		for (EventRecord eventRecord : eventRecords) {
			returnsEventPackageSet.add(eventRecord.getFedexPackageId());
		}
		logger.info("Number of package ids found in PACKAGE_RETURNS_EVENT: " + returnsEventPackageSet.size());
		EDWResults edwResults = edwDao.retrieveUnreleasedPackageIdsAndUPNs(packageIds);
		for (Date date : edwResults.getScanDates()) {
			for (Message message : edwResults.getMessages(date)) {
				unreleasedSet.add(message.getPackageId());
			}
		}
		logger.info("Number of unique UNRELEASED package ids: " + unreleasedSet.size());
		logger.info("Number of scan dates in replay file: " + edwResults.getScanDates().size());
		edwResults.getScanDates().removeAll(scanDates);
		logger.info("Number of scan dates that are not in BILLING_PACKAGE: " + edwResults.getScanDates().size());
		sb = new StringBuilder();
		for (Date date : edwResults.getScanDates()) {
			sb.append(MiscUtil.SDF.format(date) + ", ");
		}
		logger.info(sb.toString());
	}

	private void discoverReleasedBPs(List<BillingPackage> existingPackages) {
		List<Long> billingGroups = new ArrayList<>();

		for (BillingPackage bp : existingPackages) {
			if (bp.getBillingGroup() != null) {
				billingGroups.add(bp.getBillingGroup());
			}
		}
		billingGroups = billingGroupDao.getReleased(billingGroups);
		for (BillingPackage bp : existingPackages) {
			if (billingGroups.contains(bp.getBillingGroup())) {
				bp.setReleased(true);
			}
		}
	}

	private void dumpToFile(boolean buildFile, List<String> packageIds, List<BillingPackage> existingPackages) {
		BufferedWriter bw;
		List<String> bpPackageIds = MiscUtil.extractPackageIdsFromBP(existingPackages);

		if (existingPackages.size() != packageIds.size()) {
			try {
				if (buildFile) {
					bw = new BufferedWriter(new FileWriter("/missing.txt"));
					for (String string : packageIds) {
						if (!bpPackageIds.contains(string)) {
							bw.write(string + "\r\n");
						}
					}
					bw.close();
				}
			}
			catch (IOException ioe) {
				logger.info("Can't open file for writing.");
			}
		}
	}

	public static void main(String[] args) {
		// You should run this application to see what the state of the RODeS system in regards
		// to the proposed replay request.  This application will give a plethora of details that
		// the user can assess what steps to be done next.  If there are packages that exist in
		// BILLING_PACKAGE, they will need to be pushed into BILLING_PACKAGE_HISTORY before the
		// duplicate packages can be replayed.  Also, the user should look at the number of
		// scan dates that will be introduced into the RODeS system, so that they don't blast the
		// business person assigned to release the packages - general rule of thumb is to only have
		// 30 (or there abouts) scan dates per release.
		if (args.length != 1) {
			args = new String[1];
			args[0] = "P:\\Support\\SortVsRated\\rfs319572\\rfs319572.txt";
		}
		PreReplayProcess preReplayProcess = new PreReplayProcess();
		preReplayProcess.getCounts(args[0], false);
	}
}
