package com.fedex.smartpost.utilities.rodes.historical;

import com.fedex.smartpost.utilities.HadoopFileUtils;
import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.ShipmentUtils;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.evs.model.EDWDataRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExtractReplayData {
	private static final Log logger = LogFactory.getLog(ExtractReplayData.class);
	private static final String MASTER_FILE = "/Support/EdwMasterReplayRequests.rec";
	private static final String MIN_RANGE = "2015-12-01";
	private static final String MAX_RANGE = "2016-03-01";
	private static final ShipmentUtils shipmentUtils = new ShipmentUtils();
	private EDWDao edwDao;
	private List<EDWDataRecord> scanDateTo15th;

	public ExtractReplayData() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	public static void main(String[] args) throws Exception {
		ExtractReplayData extractReplayData = new ExtractReplayData();
		List<String> replayPackageFiles = new ArrayList<>();
		replayPackageFiles.add("/Support/01.2016/replay-2016-01.txt");
		replayPackageFiles.add("/Support/02.2016/replay-2016-02.txt");
		HadoopFileUtils hadoopFileUtils = new HadoopFileUtils();
		Map<Long, EDWDataRecord> edwDataRecordMap = hadoopFileUtils.readHadoopDataFile("/EbayForMichael.csv");
		logger.info("Read " + edwDataRecordMap.size() + " records from Hadoop Extract.");
		extractReplayData.process(replayPackageFiles, edwDataRecordMap);
	}

	private void process(List<String> filenames, Map<Long, EDWDataRecord> edwDataRecordMap) throws Exception {
		Set<String> packageIds = MiscUtil.retrievePackageIdRecordsFromFiles(filenames);
		Map<String, String> sharePostalMap = buildSharePostalMap(edwDataRecordMap);
		List<EDWDataRecord> edwDataRecords = edwDao.retrieveEDWOCByPackageIds(new ArrayList<>(packageIds));
		List<EDWDataRecord> ssDataRecords = new ArrayList<>();
		removeOutOfRangeRecords(edwDataRecords);
		Map<Long, String> upnShareMap = buildShareMap(edwDataRecordMap);
		int total = 0;
		int bad = 0;
		int counter = 0;

		logger.info("Set share id/postal code to records.");
		for (EDWDataRecord dataRecord : edwDataRecords) {
			ssDataRecords.add(dataRecord);
			populateMissing(dataRecord, edwDataRecordMap.get(dataRecord.getUpn()));
			String shareId = upnShareMap.get(dataRecord.getUpn());
			if (shareId != null) {
				dataRecord.setShareId(shareId);
				String postalCode = sharePostalMap.get(shareId.trim());
				total++;
				if (postalCode != null) {
					dataRecord.setSenderPostalCode(postalCode);
				}
				else {
					logger.debug("No Sender Postal Code for Package Id: " + dataRecord.getPackageId());
					bad++;
				}
			}
			counter++;
		}
		logger.info(counter + " records needing replay.");
		logger.info("Share ids added to " + total + " records.");
		logger.info("Total Packages without a sender postal code: " + bad);
		MiscUtil.dumpBadRecords(edwDataRecords);
		setScanDateTo15th(edwDataRecords);
		setCorrectShortZIPs(edwDataRecords);
		//		List<EDWDataRecord> edwDataRecords = MiscUtil.readEDWDataRecordFile("/Support/SortVsRated/recordDump.rec");
		MiscUtil.buildMessageFile(MASTER_FILE.substring(0, MASTER_FILE.length() - 4) + "-oc.rec", shipmentUtils, edwDao, edwDataRecords, true);
		//		MiscUtil.buildMessageFile(MASTER_FILE, shipmentUtils, edwDao, ssDataRecords, false);
	}

	private void removeOutOfRangeRecords(List<EDWDataRecord> edwDataRecords) throws ParseException {
		logger.info("Total records to be processed for out of range removal: " + edwDataRecords.size());
		List<EDWDataRecord> notInRange = new ArrayList<>();
		for (EDWDataRecord record : edwDataRecords) {
			if (!inDateRange(record)) {
				notInRange.add(record);
			}
		}
		logger.info("Total records that are out of range: " + notInRange.size());
		edwDataRecords.removeAll(notInRange);
		logger.info("Total records left to process against: " + edwDataRecords.size());
	}

	private void populateMissing(EDWDataRecord dataRecord, EDWDataRecord edwDataRecord) {
		dataRecord.setContainerId("WP999999999999");
		if (edwDataRecord == null) {
			logger.debug("Missing an EDW record for UPN: " + dataRecord.getUpn());
			return;
		}
		if (StringUtils.isEmpty(dataRecord.getMailClass())) {
			dataRecord.setMailClass(edwDataRecord.getMailClass());
		}
		if (StringUtils.isEmpty(dataRecord.getPackageId())) {
			dataRecord.setPackageId(edwDataRecord.getPackageId());
		}
		if (StringUtils.isEmpty(dataRecord.getRecipentPostalCode())) {
			dataRecord.setRecipentPostalCode(edwDataRecord.getRecipentPostalCode());
		}
		if (StringUtils.isEmpty(dataRecord.getDestinationSortCode())) {
			dataRecord.setDestinationSortCode(edwDataRecord.getDestinationSortCode());
		}
		if (StringUtils.isEmpty(dataRecord.getWeightSource())) {
			dataRecord.setWeightSource(edwDataRecord.getWeightSource());
		}
		if (StringUtils.isEmpty(dataRecord.getDimensionSource())) {
			dataRecord.setDimensionSource(edwDataRecord.getDimensionSource());
		}
		if (dataRecord.getSortDate() == null) {
			dataRecord.setSortDate(edwDataRecord.getSortDate());
		}
		if (dataRecord.getHubId() == null) {
			dataRecord.setHubId(edwDataRecord.getHubId());
		}
		if (dataRecord.getWeight() == null) {
			dataRecord.setWeight(edwDataRecord.getWeight());
		}
		if (dataRecord.getLength() == null) {
			dataRecord.setLength(edwDataRecord.getLength());
		}
		if (dataRecord.getWidth() == null) {
			dataRecord.setWidth(edwDataRecord.getWidth());
		}
		if (dataRecord.getHeight() == null) {
			dataRecord.setHeight(edwDataRecord.getHeight());
		}
	}

	private Map<String, String> buildSharePostalMap(Map<Long, EDWDataRecord> edwDataRecordMap) {
		logger.info("Total number of records to extract SHARE Ids: " + edwDataRecordMap.size());
		Map<Long, String> upnShareMap = buildShareMap(edwDataRecordMap);
		logger.info("Total UPN -> SHARE Id records found: " + upnShareMap.size());
		Set<String> uniqueShareIds = new HashSet<>();
		for (Long upn : edwDataRecordMap.keySet()) {
			uniqueShareIds.add(edwDataRecordMap.get(upn).getShareId());
		}
		logger.info("Total Unique SHARE Id records: " + uniqueShareIds.size());
		Map<String, String> returnMap = edwDao.retrieveSHAREInformation(upnShareMap);
		logger.info("Total number of unique SHARE Ids found: " + returnMap.size());
		return returnMap;
	}

	private Map<Long, String> buildShareMap(Map<Long, EDWDataRecord> edwDataRecordMap) {
		Map<Long, String> upnShareMap = new HashMap<>();
		for (Long upn : edwDataRecordMap.keySet()) {
			upnShareMap.put(upn, edwDataRecordMap.get(upn).getShareId());
		}
		return upnShareMap;
	}

	private boolean inDateRange(EDWDataRecord record) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return (record.getSortDate().after(sdf.parse(MIN_RANGE)) && record.getSortDate().before(sdf.parse(MAX_RANGE)));
	}

	private void setScanDateTo15th(List<EDWDataRecord> scanDates) {
		for (EDWDataRecord record : scanDates) {
			record.getSortDate().setDate(15);
		}
	}

	private void setCorrectShortZIPs(List<EDWDataRecord> scanDates) {
		for (EDWDataRecord record : scanDates) {
			record.setDestinationSortCode(StringUtils.leftPad(record.getDestinationSortCode(), 5, '0'));
		}
	}
}
