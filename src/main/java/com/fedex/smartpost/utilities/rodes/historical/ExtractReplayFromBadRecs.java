package com.fedex.smartpost.utilities.rodes.historical;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.ShipmentUtils;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.evs.model.EDWDataRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExtractReplayFromBadRecs {
	private static final Log logger = LogFactory.getLog(ExtractReplayFromBadRecs.class);
	private static final String MASTER_FILE = "/Support/SortVsRated/EdwMasterReplayRequests.rec";
	private static final ShipmentUtils shipmentUtils = new ShipmentUtils();
	private EDWDao edwDao;

	public ExtractReplayFromBadRecs() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	public static void main(String[] args) throws Exception {
		ExtractReplayFromBadRecs extractReplayFromBadRecs = new ExtractReplayFromBadRecs();
		extractReplayFromBadRecs.process();
	}

	private void process() throws Exception {
		Set<String> shareSet = new TreeSet<>();
		int counter = 0;

		List<EDWDataRecord> edwDataRecords = MiscUtil.readEDWDataRecordFile("/Support/SortVsRated/badRecordDump.rec");
		for (EDWDataRecord record : edwDataRecords) {
			if (record != null && record.getShareId() != null) {
				shareSet.add(record.getShareId());
			}
		}
		logger.info("Number of UNIQUE SHARE Ids: " + shareSet.size());
		Map<String, String> shareMap = edwDao.retrieveSHAREInformation(shareSet);
		logger.info("Number of RAW Postal Codes discovered by SHARE Ids from EDW: " + shareMap.size());
		shareMap = scrubOutNulls(shareMap);
		logger.info("Number of SCRUBBED Postal Codes discovered by SHARE Ids from EDW: " + shareMap.size());
		for (EDWDataRecord record : edwDataRecords) {
			String shareId = record.getShareId();
			if (shareId == null) {
				continue;
			}
			String postalCode = shareMap.get(shareId.trim());
			if (postalCode != null) {
				record.setSenderPostalCode(postalCode);
				counter++;
			}
		}
		logger.info("Number of records updated with discovered postal codes: " + counter);
		MiscUtil.dumpBadRecords(edwDataRecords);
		//		List<EDWDataRecord> edwDataRecords = MiscUtil.readEDWDataRecordFile("/Support/SortVsRated/recordDump.rec");
		MiscUtil.buildMessageFile(MASTER_FILE, shipmentUtils, edwDao, edwDataRecords, true);
	}

	private Map<String, String> scrubOutNulls(Map<String, String> shareMap) {
		Map<String, String> scrubbedMap = new HashMap<>();
		for (String shareId : shareMap.keySet()) {
			String postalCode = shareMap.get(shareId);
			if (postalCode != null) {
				scrubbedMap.put(shareId, postalCode);
			}
		}
		return scrubbedMap;
	}
}
