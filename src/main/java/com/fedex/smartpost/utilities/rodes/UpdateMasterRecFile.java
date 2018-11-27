package com.fedex.smartpost.utilities.rodes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;

public class UpdateMasterRecFile {
	private static final Logger logger = LogManager.getLogger(UpdateMasterRecFile.class);

	private static void process(boolean isOC) throws ParseException, IOException {
		if (isOC) {
			process(MiscUtil.OC_MASTER_REC_FILE);
		}
		else {
			process(MiscUtil.SS_MASTER_REC_FILE);
		}
	}

	private static void process(String filename) throws ParseException, IOException {
		List<Message> messageList;
		BufferedWriter bw;
		int counter = 0;

		Set<Long> upnList = MiscUtil.retreiveUPNsFromExtracted();
		logger.info(upnList.size() + " records to be removed (already replayed) from " + filename);
		messageList = MiscUtil.retreiveMessagesFromFile(filename);
		bw = new BufferedWriter(new FileWriter(filename));
		for (Message message : messageList) {
			if (!upnList.contains(message.getUpn())) {
				MiscUtil.writeMessageFormat(bw, message);
				counter++;
			}
		}
		logger.info(counter + " written.");
		bw.close();
	}

	public static void main(String[] args) throws ParseException, IOException {
		// This will remove items for the packages that have been replayed.
//		UpdateMasterRecFile.process("/Support/toBeReplayed.rec");
		UpdateMasterRecFile.process(true);
	}
}
