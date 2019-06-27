package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ProcessOC {
	private static final Log logger = LogFactory.getLog(ProcessOC.class);
	private EDWDao edwDao;
	private BillingPackageDao billingPackageDao;

	public ProcessOC() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
	}

	private void process(List<String> filenames, boolean useSPEeDSTable) throws IOException {
		EDWResults edwResults = null;
//		Map<Long, String> shareMap = buildShareMap();

		for (String filename : filenames) {
			logger.info("Filename: " + filename);
//			List<Long> upnList = MiscUtil.retreiveUPNsFromFile(filename);
			List<String> packageList = MiscUtil.runThroughBusinessCommon(MiscUtil.retreivePackageIdRecordsFromFile(filename));
			List<BillingPackage> dups = billingPackageDao.retrieveDups(packageList);
//			MiscUtil.removeDups(packageList, dups);
			if (useSPEeDSTable) {
				edwResults = edwDao.retrieveOCByPackageIds(packageList);
//				edwResults = edwDao.retrieveOCByUPNs(upnList);
			}
//			else {
//				edwResults = edwDao.retrieveEDWResultOCByPackageIds(packageList);
//			}
			logger.info("Total order create records extracted: " + edwResults.totalRecords());
			logger.info("Total scan dates: " + edwResults.getScanDates().size());
			logger.info("Scan Dates: " + buildScanDateString(edwResults));
			buildFile(edwResults);
//			billingPackageDao.retrieveDups(packageList);
		}
	}

	private static Map<Long, String> buildShareMap() {
		Map<Long, String> shareMap = new HashMap<>();
		boolean headerRead = false;

		try {
			BufferedReader br = new BufferedReader(new FileReader("/Support/SortVsRated/upn-share.csv"));
			while (br.ready()) {
				String[] split = br.readLine().trim().split(",");
				if ((headerRead) && split.length == 2) {
					shareMap.put(Long.parseLong(split[0]), split[1]);
				}
				else {
					headerRead = true;
				}
			}
		}
		catch (IOException e) {
			logger.error(e.getMessage());
		}
		return shareMap;
	}

	private String buildScanDateString(EDWResults edwResults) {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		for (Date scanDate : edwResults.getScanDates()) {
			sb.append(sdf.format(scanDate)).append(" [").append(edwResults.getMessages(scanDate).size()).append("], ");
		}
		return sb.toString();
	}

	private static void buildFile(EDWResults edwResults) throws IOException {
		int processed = 0;

		String filename = "/Support/Kienast/onePkgId.rec";
//		String filename = "/Support/SortVsRated/OC-without.rec";
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true));
		for (Date date : edwResults.getScanDates()) {
			List<Message> messages = edwResults.getMessages(date);
			if (messages != null) {
				for (Message message : messages) {
					MiscUtil.writeMessageFormat(bw, message);
					processed++;
				}
			}
		}
		bw.close();
		logger.info("Total records written: " + processed);
	}

	private void buildFileToPublish(String filename, int scanDatesToExtract) throws ParseException, IOException {
		List<Date> scanDatesProcessed = new ArrayList<>();
		List<Message> messages = MiscUtil.retreiveMessagesFromFile(filename);
		List<Message> toBePublished = new ArrayList<>();
		List<Message> notPublished = new ArrayList<>();

		logger.info("Total package ids in file: " + messages.size());
		for (Message message : messages) {
			if (scanDatesProcessed.contains(message.getScanDate())) {
				toBePublished.add(message);
			}
			else {
				if (scanDatesProcessed.size() < scanDatesToExtract) {
					scanDatesProcessed.add(message.getScanDate());
					toBePublished.add(message);
				}
				else {
					notPublished.add(message);
				}
			}
		}
		logger.info("Total package ids fitting criteria: " + toBePublished.size());
		dumpMessages(toBePublished, true);
		dumpMessages(notPublished, false);
	}

	private void dumpMessages(List<Message> toBePublished, boolean publish) throws IOException {
		Calendar cal = Calendar.getInstance();
		String filename;

		if (publish) {
			filename = "/Support/SortVsRated/OCsToBeReplayed-" + MiscUtil.SDF.format(cal.getTime()) + ".rec";
		}
		else {
			filename = "/Support/SortVsRated/OCsNotPublished-" + MiscUtil.SDF.format(cal.getTime()) + ".rec";
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true));
		for (Message message : toBePublished) {
			MiscUtil.writeMessageFormat(bw, message);
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		// Run this process to build the .rec file that will be used below...  We want to build the .rec file
		// since they are old, and probably will be removed from SPEeDS soon.
		List<String> filenames;

		if (args.length == 0) {
			filenames = new ArrayList<>();
			filenames.add("/Support/Kienast/onePkgId.txt");
//			filenames.add("/Support/EVS_Unmanifested/02.2016/replay-2016-02.txt");
		}
		else {
			filenames = Arrays.asList(args);
		}
		ProcessOC processOC = new ProcessOC();
		processOC.process(filenames, true);
	}

	// Run this process to rip a set out of the original record file to be sent to the publisher
//	public static void main(String[] args) throws ParseException, IOException {
//		String filename = null;
//		int scanDatesToExtract = 0;
//
//		if (args.length != 1) {
//			filename = "/Support/SortVsRated/OC-without.rec";
//			scanDatesToExtract = 10;
//		}
//		ProcessOC processOC = new ProcessOC();
//		processOC.buildFileToPublish(filename, scanDatesToExtract);
//	}
}