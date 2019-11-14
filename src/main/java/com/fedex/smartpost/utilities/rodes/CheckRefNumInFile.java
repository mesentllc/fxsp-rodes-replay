package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CheckRefNumInFile {
	private static final Log logger = LogFactory.getLog(CheckRefNumInFile.class);

	private void process(List<String> filenames) throws ParseException {
		int counter = 0;
		for (String filename : filenames) {
			logger.info("Filename: " + filename);
			List<Message> records = MiscUtil.retreiveMessagesFromFile(filename);
			for (Message message : records) {
				String parse = message.getPayload();
				parse = parse.substring(parse.indexOf("<BillingReferenceNumber>") + "<BillingReferenceNumber>".length());
				parse = parse.substring(0, parse.indexOf("<"));
				if (StringUtils.isBlank(parse)) {
					logger.info(message.getPackageId());
					counter++;
				}
			}
		}
		logger.info(counter + " total packages.");
	}

	public static void main(String[] args) throws ParseException {
		List<String> filenames;

		filenames = new ArrayList<>();
		filenames.add("/Support/2019-Jan-Replay/2019-04-02/ToBeReplayed-2019.04.02.rec");
		CheckRefNumInFile checkProposedFile = new CheckRefNumInFile();
		checkProposedFile.process(filenames);
	}
}