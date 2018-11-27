package com.fedex.smartpost.utilities.rodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.CollectionUtils;

public class UpdateMasterReplayFile {
	private static final Logger logger = LogManager.getLogger(UpdateMasterReplayFile.class);
	private EDWDao edwDao;
	private BillingPackageDao billingPackageDao;

	public UpdateMasterReplayFile() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
	}

	public UpdateMasterReplayFile(EDWDao edwDao, String filename) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		this.edwDao = edwDao;
		try {
			process(filename);
		}
		catch (ParseException | IOException pe) {
			logger.error(pe);
		}
	}

	private void process(String filename) throws ParseException, IOException {
		Map<Long, Message> masterMap;

		if (StringUtils.isNotEmpty(filename)) {
			logger.info("Reading file: " + filename);
			List<String> packageIds = MiscUtil.retreivePackageIdRecordsFromFile(filename);
			logger.info(packageIds.size() + " records read from " + filename);
//			List<BillingPackage> dups = billingPackageDao.retrieveDups(packageIds);
//			removeDups(packageIds, dups);
			masterMap = addToMasterRecords(edwDao.retrieveUnreleasedPackageIdsAndUPNs(packageIds));
		}
		else {
			masterMap = addToMasterRecords(null);
		}
		MiscUtil.storeMasterFile(MiscUtil.SS_MASTER_FILE, masterMap);
	}

	private void removeDups(List<String> packageIds, List<BillingPackage> dups) {
		Set<String> duplicatePackageIds = new HashSet<>();
		if (packageIds == null || dups == null) {
			return;
		}
		logger.info("Starting package id count: " + packageIds.size());
		for (BillingPackage bp : dups) {
			duplicatePackageIds.add(bp.getFedexPkgId());
		}
		logger.info(duplicatePackageIds.size() + " duplicate records found.");
		packageIds.removeAll(duplicatePackageIds);
		logger.info("Reamining package id count: " + packageIds.size());
	}

	private Map<Long, Message> addToMasterRecords(EDWResults edwResults) throws ParseException, IOException {
		Map<Long, Message> masterMap = MiscUtil.retrieveMasterReplayFileRecords(MiscUtil.SS_MASTER_FILE);

		if (edwResults != null) {
			masterMap = removeReleasedPackages(masterMap, edwDao.retrieveUnreleasedUPNs(masterMap.keySet()));
			for (Date date : edwResults.getScanDates()) {
				for (Message message : edwResults.getMessages(date)) {
					masterMap.put(message.getUpn(), message);
				}
			}
		}
		masterMap = removeMarkedPackages(masterMap);
		return masterMap;
	}

	private static Map<Long, Message> removeMarkedPackages(Map<Long, Message> masterMap) throws IOException {
		Set<Long> upnSet = new TreeSet<>();
		Map<Long, Message> messageMap = new TreeMap<>();
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(MiscUtil.EXTRACTED));
		}
		catch (IOException ioe) {
			// If the file does not exists, then we don't need to remove anything.
//			logger.info("No records marked for replay [file marker file does not exist.]");
			return masterMap;
		}
		logger.info("Initial Master Map Size: " + masterMap.size());
		while (br.ready()) {
			upnSet.add(Long.parseLong(br.readLine().trim()));
		}
		br.close();
		for (Long upn : masterMap.keySet()) {
			if (!upnSet.contains(upn)) {
				messageMap.put(upn, masterMap.get(upn));
			}
		}
		File file = new File(MiscUtil.EXTRACTED);
		file.delete();
		logger.info("Master Map Size After Replayed Messages removed: " + messageMap.size());
		return messageMap;
	}

	private Map<Long, Message> removeReleasedPackages(Map<Long, Message> masterMap, Set<Long> unreleasedSet) {
		Map<Long, Message> map = new TreeMap<>();

		for (Long unreleased : unreleasedSet) {
			map.put(unreleased, masterMap.get(unreleased));
		}
		return map;
	}

	public static void main(String[] args) throws ParseException, IOException {
		// You should run this application to update the Master Replay List of package ids/UPNs
		// which could be used to execute replays against.  This will add/remove items to maintain
		// only the packages to be replayed.
		if (args.length != 1) {
			args = new String[1];
//			args[0] = "P:\\Support\\SortVsRated\\09.30-10.06\\09.30-10.06.txt";
			args[0] = null;
		}
		UpdateMasterReplayFile updateMasterReplayFile = new UpdateMasterReplayFile();
		updateMasterReplayFile.process(args[0]);
	}
}
