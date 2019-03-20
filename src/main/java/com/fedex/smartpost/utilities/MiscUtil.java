package com.fedex.smartpost.utilities;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageException;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import com.fedex.smartpost.common.types.CustomerInformation;
import com.fedex.smartpost.common.types.Shipment;
import com.fedex.smartpost.common.types.Shipping;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.evs.model.EDWDataRecord;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MiscUtil {
	private static final Log logger = LogFactory.getLog(MiscUtil.class);
	public static final String SS_MASTER_FILE = "/Support/masterReplayRequests.txt";
	public static final String SS_MASTER_REC_FILE = "/Support/masterReplayRequests.rec";
	public static final String OC_MASTER_FILE = "/Support/EdwMasterReplayRequests-oc.txt";
	public static final String OC_MASTER_REC_FILE = "/Support/EdwMasterReplayRequests-oc.rec";
	public static final String EXTRACTED = "/Support/masterReplayExtracted.txt";
	public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat SDF2 = new SimpleDateFormat("MM/dd/yyyy");

	public static XMLGregorianCalendar getXmlDate(Date date) {
		if (date != null) {
			try {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(date);
				return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			}
			catch (DatatypeConfigurationException e) {
				return null;
			}
		}
		else {
			return null;
		}
	}

	public static void dumpPackageIds(String filename, List<String> packageIds) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

		for (String packageId : packageIds) {
			bw.write(packageId + "\n");
		}
		logger.info(packageIds.size() + " records written to " + filename);
		bw.close();
	}

	public static List<String> retreivePackageIdRecordsFromFile(String filename) {
		Set<String> packageSet = new HashSet<>();
		List<String> packageIds = new ArrayList<>();
		BufferedReader br;
		int recordsRead = 0;

		try {
			br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				try {
					FxspPackage fxspPackage = FxspPackageFactory.createFromUnknown(br.readLine().trim());
					packageSet.add(fxspPackage.getUspsBarcode().getPackageIdentificationCode().substring(2));
				}
				catch (FxspPackageException e) {
					logger.info(e.getMessage());
				}
				recordsRead++;
			}
			br.close();
		}
		catch (IOException ioe) {
			logger.info("Can't open file: " + filename);
		}
		packageIds.addAll(packageSet);
		logger.info("Total records found in file: " + recordsRead);
		logger.info("Total UNIQUE records found in file: " + packageIds.size());
		return packageIds;
	}

	public static Set<String> retreivePackageIdRecordsFromFiles(List<String> filenames) {
		Set<String> packageSet = new HashSet<>();
		for (String filename : filenames) {
			logger.info("Attempting to read " + filename);
			List<String> packageIds = retreivePackageIdRecordsFromFile(filename);
			logger.info(packageIds.size() + " records returned.");
			long beforePackageSize = packageSet.size();
			packageSet.addAll(packageIds);
			logger.info((packageSet.size() - beforePackageSize) + " UNIQUE records added to set.");
		}
		logger.info("Total record count from all files: " + packageSet.size());
		return packageSet;
	}

	public static Set<Long> retreiveUPNsFromExtracted() {
		Set<Long> upnSet = new HashSet<>();
		BufferedReader br;
		int recordsRead = 0;

		logger.info("Attempting ro read UPNs from " + EXTRACTED);
		try {
			br = new BufferedReader(new FileReader(EXTRACTED));
			while (br.ready()) {
				upnSet.add(Long.parseLong(br.readLine().trim()));
				recordsRead++;
			}
			br.close();
		}
		catch (IOException ioe) {
			logger.info("Can't open file: " + EXTRACTED);
		}
		logger.info("Total records found in file: " + recordsRead);
		return upnSet;
	}

	public static List<Long> retreiveUPNsFromFile(String filename, Set<Date> dates) throws ParseException {
		List<Long> upnList = new ArrayList<>();
		Set<Long> listSet = new HashSet<>();
		BufferedReader br;
		int recordsRead = 0;
		int found = 0;

		try {
			br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String line = br.readLine().trim();
				String[] split = line.split("\\|");
				if (split.length == 4) {
					if (dates == null || dates.contains(SDF.parse(split[1]))) {
						listSet.add(Long.parseLong(split[0]));
						found++;
					}
					recordsRead++;
				}
			}
			br.close();
		}
		catch (IOException ioe) {
			logger.info("Can't open file: " + filename);
		}
		upnList.addAll(listSet);
		logger.info("Total records found in file: " + recordsRead);
		logger.info("Total records fitting criteria: " + found);
		return upnList;
	}

	public static List<Long> retreiveUPNsFromFile(String filename) {
		List<Long> upnList = new ArrayList<>();
		Set<Long> listSet = new HashSet<>();
		BufferedReader br;
		int recordsRead = 0;
		int found = 0;

		try {
			br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String line = br.readLine().trim();
				if (StringUtils.isNotEmpty(line)) {
					listSet.add(Long.parseLong(line));
					found++;
				}
				recordsRead++;
			}
			br.close();
		}
		catch (IOException ioe) {
			logger.info("Can't open file: " + filename);
		}
		upnList.addAll(listSet);
		logger.info("Total records found in file: " + recordsRead);
		logger.info("Total records fitting criteria: " + found);
		return upnList;
	}

	public static List<BillingPackage> retreiveBPsFromFile(String filename) throws ParseException {
		List<BillingPackage> packageIds = new ArrayList<>();
		BufferedReader br;
		int recordsRead = 0;

		try {
			br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String line = br.readLine().trim();
				if (StringUtils.isNotBlank(line)) {
					String[] split = line.split("\\|");
					BillingPackage bp = new BillingPackage();
					bp.setFedexPkgId(split[0]);
					bp.setOriginHubScanDt(SDF.parse(split[1]));
					bp.setCreatedDt(SDF.parse(split[2]));
					packageIds.add(bp);
					recordsRead++;
				}
			}
			br.close();
		}
		catch (IOException ioe) {
			logger.info("Can't open file: " + filename);
		}
		logger.info("Total records found in file: " + recordsRead);
		return packageIds;
	}

	public static void writeMessageFormat(BufferedWriter bw, Message message) throws IOException {
		bw.write(message.getUpn() + "|" + SDF.format(message.getScanDate()) + '|' +
				 message.getPackageId() + "|" + message.getPayload() + "\r\n");
	}

	public static List<Message> retreiveMessagesFromFile(String filename) throws ParseException {
		List<Message> messages = new ArrayList<>();
		BufferedReader br;
		int recordsRead = 0;

		logger.info("Attempting to read file " + filename);
		try {
			br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String line = StringUtils.strip(br.readLine());
				if (StringUtils.isNotBlank(line)) {
					String[] split = line.split("\\|");
					messages.add(new Message(Long.parseLong(split[0]), MiscUtil.SDF.parse(split[1]), split[2], split[3]));
					recordsRead++;
				}
			}
			br.close();
		}
		catch (IOException ioe) {
			logger.info("Can't open file: " + filename);
		}
		logger.info("Total records found in file: " + recordsRead);
		return messages;
	}

	public static Set<String> retreivePackageIdsFromMessages(List<Message> messages) {
		Set<String> packageSet = new TreeSet<>();

		for (Message message : messages) {
			String payload = message.getPayload();
			payload = payload.substring(payload.indexOf("<PackageId>") + 11);
			packageSet.add(payload.substring(0, payload.indexOf("<")));
		}
		logger.info("Total number of package ids found in message list: " + packageSet.size());
		return packageSet;
	}

	public static Map<Long, Message> retrieveMasterReplayFileRecords(String filename) throws ParseException {
		Map<Long, Message> masterMap = new TreeMap<>();
		BufferedReader br;
		int recordsRead = 0;

		try {
			br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String line = br.readLine().trim();
				if (StringUtils.isNotBlank(line)) {
					String[] split = line.split("\\|");
					masterMap.put(Long.parseLong(split[0]), new Message(Long.parseLong(split[0]), MiscUtil.SDF.parse(split[1]), split[2], split[3]));
					recordsRead++;
				}
			}
			br.close();
		}
		catch (IOException ioe) {
			logger.info("Can't open file: " + filename);
		}
		logger.info("Total records found in MASTER file: " + recordsRead);
		return masterMap;
	}

	public static void storeMasterFile(String filename, Map<Long, Message> masterMap) {
		BufferedWriter bw;
		long recordsProcessed = 0;

		try {
			bw = new BufferedWriter(new FileWriter(filename));
			for (Long index : masterMap.keySet()) {
				Message message = masterMap.get(index);
				bw.write(message.getUpn() + "|" + MiscUtil.SDF.format(message.getScanDate()) + '|' +
						 message.getPackageId() + '|' + message.getPayload() + "\r\n");
				recordsProcessed++;
			}
			bw.close();
		}
		catch (IOException ioe) {
			logger.info("Can't open file: " + filename);
		}
		logger.info("Records stored in MASTER file: " + recordsProcessed);
	}

	public static List<String> extractPackageIdsFromBP(List<BillingPackage> billingPackages) {
		List<String> packageIds = new ArrayList<>(billingPackages.size());

		for (BillingPackage billingPackage : billingPackages) {
			packageIds.add(billingPackage.getFedexPkgId());
		}
		return packageIds;
	}

	public static int getReleasedCount(List<BillingPackage> billingPackages) {
		int released = 0;

		for (BillingPackage billingPackage : billingPackages) {
			if (billingPackage.isReleased()) {
				released++;
			}
		}
		return released;
	}

	public static int getGroupedCount(List<BillingPackage> billingPackages) {
		int grouped = 0;

		for (BillingPackage billingPackage : billingPackages) {
			if (("2".equals(billingPackage.getStatus())) && !billingPackage.isReleased()) {
				grouped++;
			}
		}
		return grouped;
	}

	public static List<EDWDataRecord> readEDWDataRecordFile(String filename) throws IOException, ParseException {
		List<EDWDataRecord> edwDataRecords = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		logger.info("Reading file: " + filename);
		BufferedReader br = new BufferedReader(new FileReader(filename));
		while (br.ready()) {
			String[] split = br.readLine().split(",");
			EDWDataRecord record = new EDWDataRecord();
			record.setUpn(Long.parseLong(split[0]));
			record.setPackageId(nullifyNULLs(split[1]));
			record.setMailClass(nullifyNULLs(split[2]));
			record.setSizeCategory(nullifyNULLs(split[3]));
			record.setProcessingCategory(nullifyNULLs(split[4]));
			record.setContainerId(nullifyNULLs(split[5]));
			record.setRecipentPostalCode(nullifyNULLs(split[6]));
			record.setDestinationSortCode(nullifyNULLs(split[7]));
			record.setSortDate(new Timestamp(sdf.parse(nullifyNULLs(split[8])).getTime()));
			record.setHubId(safeInt(split[9]));
			record.setWeight(BigDecimal.valueOf(safeDouble(split[10])));
			record.setLength(BigDecimal.valueOf(safeDouble(split[11])));
			record.setWidth(BigDecimal.valueOf(safeDouble(split[12])));
			record.setHeight(BigDecimal.valueOf(safeDouble(split[13])));
			record.setWeightSource(nullifyNULLs(split[14]));
			record.setDimensionSource(nullifyNULLs(split[15]));
			record.setImpb(Boolean.valueOf(nullifyNULLs(split[16])));
			record.setSenderPostalCode(nullifyNULLs(split[17]));
			if (split.length == 19) {
				record.setShareId(nullifyNULLs(split[18]));
			}
			edwDataRecords.add(record);
		}
		logger.info("Records read from file: " + edwDataRecords.size());
		return edwDataRecords;
	}

	private static String nullifyNULLs(String string) {
		if ("null".equals(string)) {
			return null;
		}
		return string;
	}

	private static Integer safeInt(String string) {
		if ("null".equals(string)) {
			return null;
		}
		return Integer.parseInt(string);
	}

	private static Double safeDouble(String string) {
		if ("null".equals(string)) {
			return Double.parseDouble("0");
		}
		return Double.parseDouble(string);
	}

	private static boolean badWgtDims(EDWDataRecord record) {
		return (record.getHeight() == null || record.getLength() == null || record.getWeight() == null || record.getWidth() == null);
	}

	public static void dumpBadRecords(List<EDWDataRecord> edwDataRecords) throws IOException {
		BufferedWriter bwBad = new BufferedWriter(new FileWriter("/Support/badRecordDump.rec"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int bad = 0;

		logger.info("Total records provided to method: " + edwDataRecords.size());
		for (EDWDataRecord record : edwDataRecords) {
			StringBuilder sb = new StringBuilder();
			sb.append(record.getUpn()).append(',').append(record.getPackageId()).append(',').append(record.getMailClass()).append(',').append(record.getSizeCategory())
			  .append(',').append(record.getProcessingCategory()).append(',').append(record.getContainerId()).append(',').append(record.getRecipentPostalCode()).append(',')
			  .append(record.getDestinationSortCode()).append(',').append(sdf.format(record.getSortDate())).append(',').append(record.getHubId()).append(',')
			  .append(record.getWeight()).append(',').append(record.getLength()).append(',').append(record.getWidth()).append(',').append(record.getHeight()).append(',')
			  .append(record.getWeightSource()).append(',').append(record.getDimensionSource()).append(',').append(record.isImpb()).append(',')
			  .append(record.getSenderPostalCode()).append(',').append(record.getShareId()).append('\n');
			if (record.getSenderPostalCode() == null || badWgtDims(record)) {
				bwBad.write(sb.toString());
				bad++;
			}
		}
		bwBad.close();
		logger.info("Total BAD records written: " + bad);
	}

	public static void buildMessageFile(String masterFile, ShipmentUtils shipmentUtils, EDWDao edwDao, List<EDWDataRecord> edwDataRecords, boolean isOC) throws Exception {
		EDWResults edwResults = new EDWResults();
		Map<Long, Message> masterMap;
		int packageCount = 0;
		int recordCount = 0;
		Shipment tempShipment = shipmentUtils.getStarterShipment();
		for (EDWDataRecord record : edwDataRecords) {
			String payload;
			if (record.getSenderPostalCode() != null) {
				String packageId = record.getPackageId();
				shipmentUtils
						.populateShipment(tempShipment, packageId, record.getMailClass(), record.getSizeCategory(), record.getProcessingCategory(), record.getContainerId(),
										  record.getDestinationSortCode(), record.getSortDate(), record.getHubId(), record.getWeight(), record.getLength(), record.getWidth(),
										  record.getHeight(), record.getWeightSource(), record.getDimensionSource(), record.isImpb());
				if (isOC) {
					tempShipment.setShipperInformation(new CustomerInformation());
					tempShipment.getShipperInformation().setPostalCode(record.getSenderPostalCode());
					tempShipment.setShipping(new Shipping());
					tempShipment.getShipping().setHubId(record.getHubId());
				}
				payload = shipmentUtils.encodeObject(tempShipment);
				if ((payload != null) && (!payload.isEmpty())) {
					edwResults.addMessage(record.getSortDate(), new Message(record.getUpn(), record.getSortDate(), packageId, payload));
					packageCount++;
				}
			}
			if ((++recordCount % 100) == 0) {
				logger.debug("Processed " + recordCount + " records.");
			}
		}
		logger.info("Total records processed: " + recordCount);
		logger.info("Total records to be replayed: " + packageCount);
		masterMap = addToMasterRecords(masterFile, edwDao, edwResults);
		storeMasterFile(masterFile, masterMap);
	}

	public static Set<String> retrievePackageIdsFromFile(String filename) throws IOException {
		Set<String> packageIds = new TreeSet<>();

		if (filename == null) {
			logger.error("Source filename can not be NULL - No package ids can be extracted.");
			return packageIds;
		}
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
		while (bufferedReader.ready()) {
			packageIds.add(bufferedReader.readLine().trim());
		}
		return packageIds;
	}

	private static Map<Long, Message> addToMasterRecords(String masterFile, EDWDao edwDao, EDWResults edwResults) throws ParseException, IOException {
		Map<Long, Message> masterMap = MiscUtil.retrieveMasterReplayFileRecords(masterFile);

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
			br = new BufferedReader(new FileReader(EXTRACTED));
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
		File file = new File(EXTRACTED);
		file.delete();
		logger.info("Master Map Size After Replayed Messages removed: " + messageMap.size());
		return messageMap;
	}

	private static Map<Long, Message> removeReleasedPackages(Map<Long, Message> masterMap, Set<Long> unreleasedSet) {
		Map<Long, Message> map = new TreeMap<>();

		for (Long unreleased : unreleasedSet) {
			map.put(unreleased, masterMap.get(unreleased));
		}
		return map;
	}
}