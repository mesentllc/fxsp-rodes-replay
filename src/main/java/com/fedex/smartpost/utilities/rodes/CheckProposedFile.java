package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageHistoryGateway;
import com.fedex.smartpost.utilities.rodes.dao.OutboundOrdCrtEvntStatDao;
import com.fedex.smartpost.utilities.rodes.dao.UnmanifestedPackageDao;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Instance;
import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.transportation.dao.PackageDao;
import com.fedex.smartpost.utilities.transportation.dao.PackageHistoryDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CheckProposedFile {
	private static final Log logger = LogFactory.getLog(CheckProposedFile.class);
	private EDWDao edwDao;
	private BillingPackageDao billingPackageDao;
	private OutboundOrdCrtEvntStatDao outboundOrdCrtEvntStatDao;
	private PackageDao packageDao;
	private PackageHistoryDao packageHistoryDao;
	private BillingPackageHistoryGateway billingPackageHistoryGateway;
	private UnmanifestedPackageDao unmanifestedPackageDao;

	public CheckProposedFile() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		outboundOrdCrtEvntStatDao = (OutboundOrdCrtEvntStatDao)context.getBean("outboundOrdCrtEvntStatDao");
		packageDao = (PackageDao)context.getBean("transPackageDao");
		packageHistoryDao = (PackageHistoryDao)context.getBean("transPackageHistDao");
		billingPackageHistoryGateway = (BillingPackageHistoryGateway)context.getBean("billingPackageHistoryGateway");
		unmanifestedPackageDao = (UnmanifestedPackageDao)context.getBean("unmanifestedPackageDao");
	}

	private void process(List<String> filenames, boolean saveIt) throws SQLException, IOException {
		for (String filename : filenames) {
			logger.info("Filename: " + filename);
			if (saveIt) {
				new UpdateMasterReplayFile(edwDao, filename);
			}
			else {
				List<String> packageIds = MiscUtil.runThroughBusinessCommon(MiscUtil.retrievePackageIdRecordsFromFile(filename));
				List<BillingPackage> dups = billingPackageDao.retrieveDups(packageIds);
/*
				List<String> released = billingPackageDao.retrieveReleased(packageIds);
				List<String> staged = billingPackageDao.retrieveStaged(packageIds);
				packageIds.removeAll(released);
				packageIds.removeAll(staged);
				logger.info("Number of package ids before EDW Check: " + packageIds.size());
				removeDuplicates(packageIds, dups);
//				dumpUnreleased(edwDao.retrieveUnreleasedPackageIdsAndUPNs(packageIds));
				List<Instance> instances = edwDao.retrieveReleasedPackages(packageIds);
				packageIds = removeDups(packageIds, instances);
				logger.info("Number of outstanding package ids: " + packageIds.size());
				dumpPackageIds(packageIds);
*/
				dumpIds(dups);
//				outboundOrdCrtEvntStatDao.retrievePackages(packageIds);
//				billingPackageHistoryGateway.retrieveBillingPackageHistoryRecordsByPackageIds(packageIds);
//				packageDao.retrievePackages(packageIds);
//				packageHistoryDao.retrievePackages(packageIds);
//				dumpStatuses(unmanifestedPackageDao.getUnmanifestedStatusByPackageId(packageIds));
			}
		}
		closeConnections();
	}

	private void removeDuplicates(List<String> packageIds, List<BillingPackage> dups) {
		List<String> duplicates = new ArrayList<>();
		for (BillingPackage billingPackage : dups) {
			duplicates.add(billingPackage.getFedexPkgId());
		}
		packageIds.removeAll(duplicates);
	}

	private void dumpPackageIds(List<String> packageIds) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BufferedWriter bw = new BufferedWriter(new FileWriter("/Support/missingPackageIds" + sdf.format(new Date()) + ".txt"));
		for (String packageId : packageIds) {
			bw.write(packageId + "\n");
		}
		bw.close();
	}

	private List<String> removeDups(List<String> current, List<Instance> instances) {
		Set<String> fromEDW = new TreeSet<>();
		List<String> notFoundPackageId = new ArrayList<>();
		for (Instance instance : instances) {
			fromEDW.add(instance.getPackageId());
		}
		for (String packageId : current) {
			if (!fromEDW.contains(packageId)) {
				notFoundPackageId.add(packageId);
			}
		}
		return notFoundPackageId;
	}

	private void dumpStatuses(Map<String, Set<String>> unmanifestedStatus) {
		for (String key : unmanifestedStatus.keySet()) {
			logger.info("Package ids found in UNMANIFESTED_PACKAGE with status code of " + key + " [RODeS]: " + unmanifestedStatus.get(key).size());
		}
	}

	private void closeConnections() throws SQLException {
		edwDao.close();
		billingPackageDao.close();
		outboundOrdCrtEvntStatDao.close();
		billingPackageHistoryGateway.close();
		packageDao.close();
		packageHistoryDao.close();
		unmanifestedPackageDao.close();
	}

	private void dumpUnreleased(EDWResults edwResults) throws IOException {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		BufferedWriter bw = new BufferedWriter(new FileWriter("/Support/unreleased-" + sdf.format(now) + ".txt"));
		for (Date scanDate : edwResults.getScanDates()) {
			List<Message> messageList = edwResults.getMessages(scanDate);
			for (Message message : messageList) {
				bw.write(message.getPackageId() + "\r\n");
			}
		}
		bw.close();
	}

	private void dumpIds(List<BillingPackage> billingPackages) {
		Map<String, List<String>> statusSet = new TreeMap<>();
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("/Support/staged-" + sdf.format(now) + ".txt"))) {
			for (BillingPackage billingPackage : billingPackages) {
				bw.write(billingPackage.getFedexPkgId() + "\n");
				List<String> packages;
				if (!statusSet.containsKey(billingPackage.getStatus())) {
					packages = new ArrayList<>();
					statusSet.put(billingPackage.getStatus(), packages);
				}
				else {
					packages = statusSet.get(billingPackage.getStatus());
				}
				packages.add(billingPackage.getFedexPkgId());

				logger.info(billingPackage.getFedexPkgId() + " -> Status: " + billingPackage.getStatus());
			}
		}
		catch (IOException ioe) {
			logger.error("Unable to write to file: ", ioe);
		}
		for (String status : statusSet.keySet()) {
			logger.info("Status: " + status + " has " + statusSet.size() + " package ids.");
			writeFile(status, statusSet.get(status));
		}
	}

	private void writeFile(String status, List<String> strings) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("/Support/Kienast/%s-Status-%s.txt",
				sdf.format(new Date()), status)));
			for (String packageId : strings) {
				bw.write(packageId + "\n");
			}
			bw.close();
		}
		catch(IOException ioe) {
			logger.error("Exception: ", ioe);
		}
	}

	public static void main(String[] args) throws SQLException, IOException {
		// Run this process to check how many package ids are not existing currently in the EDW Database.
		//  It might prove that the file doesn't need to be replayed.
		List<String> filenames;

		if (args.length != 1) {
			filenames = new ArrayList<>();
//			filenames.add("/Support/02.2016/replay-2016-02.txt");
//			filenames.add(MiscUtil.SS_MASTER_FILE);
			filenames.add("/Support/2020-04-01-UNRELEASE/ungroup.txt");
		}
		else {
			filenames = Arrays.asList(args);
		}
		CheckProposedFile checkProposedFile = new CheckProposedFile();
		checkProposedFile.process(filenames, false);
	}
}