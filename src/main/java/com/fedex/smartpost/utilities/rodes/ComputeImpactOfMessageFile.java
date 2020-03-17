package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ComputeImpactOfMessageFile {
	private static final Log logger = LogFactory.getLog(ComputeImpactOfMessageFile.class);

	private void process(boolean isRecFile, boolean isOC) throws ParseException {
		if (isOC) {
			if (isRecFile) {
				process(MiscUtil.OC_MASTER_REC_FILE);
			}
			else {
				process(MiscUtil.OC_MASTER_FILE);
			}
		}
		else {
			if (isRecFile) {
				process(MiscUtil.SS_MASTER_REC_FILE);
			}
			else {
				process(MiscUtil.SS_MASTER_FILE);
			}
		}
	}

	private void process(String filename) throws ParseException {
		Set<Date> scanDates = new TreeSet<>();
		Map<Date, Integer> detailMap = new HashMap<>();
		List<Message> messages;

		messages = MiscUtil.retreiveMessagesFromFile(filename);
		for (Message message : messages) {
			Date scanDate = message.getScanDate();
			scanDates.add(scanDate);
			if (detailMap.containsKey(scanDate)) {
				detailMap.replace(scanDate, detailMap.get(scanDate), detailMap.get(scanDate) + 1);
			}
			else {
				detailMap.put(scanDate, 1);
			}
		}
		logger.info("Total number of unique scan dates in file: " + scanDates.size());
		StringBuilder sb = new StringBuilder();
		StringBuilder sbDetail = new StringBuilder();
		for (Date date : scanDates) {
			sb.append(MiscUtil.SDF.format(date) + ", ");
			sbDetail.append(MiscUtil.SDF.format(date) + " [" + detailMap.get(date) + "], ");
		}
		logger.info("Scan Dates: " + sb);
		logger.info("Details: " + sbDetail);
	}

	public static void main(String[] args) throws ParseException {
		ComputeImpactOfMessageFile computeImpactOfMessageFile = new ComputeImpactOfMessageFile();
//		computeImpactOfMessageFile.process(false, false);
		computeImpactOfMessageFile.process("/Support/2020-03-09/messages.rec");
	}
}
