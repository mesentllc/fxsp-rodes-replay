package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CheckForExistence {
	private static final Log logger = LogFactory.getLog(CheckForExistence.class);

	private void process(String srcPackageIdFile, List<String> filenames) throws IOException, ParseException {
		Set<String> sourceIds = MiscUtil.retrievePackageIdsFromFile(srcPackageIdFile);
		for (String filename : filenames) {
			int counter = 0;
			logger.info("Filename: " + filename);
			List<Message> messages = MiscUtil.retreiveMessagesFromFile(filename);
			Set<String> packageIds = MiscUtil.retreivePackageIdsFromMessages(messages);
			packageIds.retainAll(sourceIds);
			for (String packageId : packageIds) {
				logger.info(packageId);
				counter++;
			}
			logger.info(counter + " package ids found in file: " + filename);
			if (counter > 0) {
				getBillingRef(messages, packageIds);
			}
		}
	}

	private void getBillingRef(List<Message> messages, Set<String> packageIds) {
		for (Message message : messages) {
			if (packageIds.contains(message.getPackageId())) {
				String parse = message.getPayload();
				parse = parse.substring(parse.indexOf("<BillingReferenceNumber>") + "<BillingReferenceNumber>".length());
				parse = parse.substring(0, parse.indexOf("<"));
				logger.info(message.getPackageId() + " - Ref: " + parse);
			}
		}
	}

	public static void main(String[] args) throws IOException, ParseException {
		List<String> filenames;

		filenames = new ArrayList<>();
		filenames.add("/Support/2019-Jan-Replay/2019-04-02/ToBeReplayed-2019.04.02.rec");
//		filenames.add("/Support/2019-Jan-Replay/2019-04-03/ToBeReplayed-2019.04.03.rec");
//		filenames.add("/Support/2019-Jan-Replay/2019-04-04/ToBeReplayed-2019.04.04.rec");
		CheckForExistence checkForExistence = new CheckForExistence();
		checkForExistence.process("/Support/2019-Jan-Replay/question.txt", filenames);
	}
}