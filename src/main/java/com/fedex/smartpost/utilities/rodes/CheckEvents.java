package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.DomesticEventStatGateway;
import com.fedex.smartpost.utilities.rodes.dao.ReturnsEventStatGateway;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EventRecord;
import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.rodes.model.StatusTally;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CheckEvents {
	private static final Log logger = LogFactory.getLog(CheckEvents.class);
	private DomesticEventStatGateway domesticEventStatGateway;
	private ReturnsEventStatGateway returnsEventStatGateway;

	private CheckEvents() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		domesticEventStatGateway = (DomesticEventStatGateway)context.getBean("domesticEventStatGateway");
		returnsEventStatGateway = (ReturnsEventStatGateway)context.getBean("returnsEventStatGateway");
	}

	private void tally(List<EventRecord> records) {
		StatusTally tally = new StatusTally();
		Map<String, Integer> reasonMap = new HashMap<>();

		for (EventRecord record : records) {
			switch (record.getPackageEventStatus()) {
				case "NEW":
					tally.incNewStatus();
					break;
				case "VALID":
					tally.incValidStatus();
					break;
				case "INVALID":
					tally.incInvalidStatus();
					String reason = record.getPackageEventReason();
					Integer count = reasonMap.computeIfAbsent(reason, k -> 0);
					reasonMap.put(reason, ++count);
					break;
				case "IGNORED":
					tally.incIgnoredStatus();
					break;
				case "USED":
					tally.incUsedStatus();
			}
		}
		logger.info(String.format("[%d NEW] [%d VALID] [%d USED] [%d INVALID] [%d IGNORED]",tally.getNewStatus(),
			tally.getValidStatus(), tally.getUsedStatus(), tally.getInvalidStatus(), tally.getIgnoredStatus()));
		for (String reason : reasonMap.keySet()) {
			logger.info("Reason: " + reason + " occurred " + reasonMap.get(reason) + " times.");
		}
	}

	private void process(String filename) throws IOException {
//		Set<String> packageIds = MiscUtil.retreivePackageIdsFromMessages(MiscUtil.retreiveMessagesFromFile(filename));
		Set<String> packageIds = MiscUtil.retrievePackageIdsFromFile(filename);
		logger.info("DOMESTIC PACKAGE EVENTS");
		tally(domesticEventStatGateway.retrieveEventRecords(new ArrayList<>(packageIds)));
		logger.info("RETURNS PACKAGE EVENTS");
		tally(returnsEventStatGateway.retrieveEventRecords(new ArrayList<>(packageIds)));
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			args = new String[1];
			args[0] = "/Support/missingPackageIds.txt";
		}
		CheckEvents checkEvents = new CheckEvents();
		checkEvents.process(args[0]);
	}
}
