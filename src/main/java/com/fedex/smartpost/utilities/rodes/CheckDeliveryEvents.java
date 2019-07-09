package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CheckDeliveryEvents {
	private static final Log logger = LogFactory.getLog(CheckDeliveryEvents.class);

	public static void main(String[] args) throws ParseException {
		CheckDeliveryEvents checkDeliveryEvents = new CheckDeliveryEvents();
		checkDeliveryEvents.process("/Support/Kienast/ToBeReplayed-2019.07.09.rec");
	}

	private void process(String filename) throws ParseException {
		List<Message> messages = MiscUtil.retreiveMessagesFromFile(filename);
		List<String> badEvents = new ArrayList<>();
		for (Message message : messages) {
			String working = message.getPayload();
			working = working.substring(working.indexOf("<DeliveryEvent>"));
			if (!working.contains("<PostalCode>")) {
				badEvents.add(message.getPackageId());
				logger.info(message.getPackageId());
			}
		}
		logger.info("Total number of BAD delivery events: " + badEvents.size());
	}
}
