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

public class VerifyRun {
	private static final Log logger = LogFactory.getLog(VerifyRun.class);
	private static final String[] urls = {"http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/processAllDomesticEvents",
										  "http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/processAllReturnsEvents"};
	private BillingPackageDao billingPackageDao;
	private DomesticEventStatGateway domesticEventStatGateway;
	private ReturnsEventStatGateway returnsEventStatGateway;

	public VerifyRun() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		domesticEventStatGateway = (DomesticEventStatGateway)context.getBean("domesticEventStatGateway");
		returnsEventStatGateway = (ReturnsEventStatGateway)context.getBean("returnsEventStatGateway");
	}

	private void displayTally(List<EventRecord> records) {
		Map<String, StatusTally> results = new HashMap<>();
		StatusTally tally;

		for (EventRecord record : records) {
			String packageId = record.getFedexPackageId();
			if (results.containsKey(packageId)) {
				tally = results.get(packageId);
			}
			else {
				tally = new StatusTally();
				results.put(packageId, tally);
			}
			tally.setAccountNbr(record.getFedexCustomerAccountNumber());
			if ("NEW".equals(record.getPackageEventStatus())) {
				tally.incNewStatus();
			}
			if ("VALID".equals(record.getPackageEventStatus())) {
				tally.incValidStatus();
			}
			if ("INVALID".equals(record.getPackageEventStatus())) {
				tally.incInvalidStatus();
			}
			if ("IGNORED".equals(record.getPackageEventStatus())) {
				tally.incIgnoredStatus();
			}
			if ("USED".equals(record.getPackageEventStatus())) {
				tally.incUsedStatus();
			}
		}
		for (String packageId : results.keySet()) {
			tally = results.get(packageId);
			if (tally.getInvalidStatus() > 0) {
				logger.info(String.format("[%s] [%s] [%d NEW] [%d VALID] [%d USED] [%d INVALID] [%d IGNORED]", packageId,
					tally.getAccountNbr(), tally.getNewStatus(), tally.getValidStatus(), tally.getUsedStatus(),
					tally.getInvalidStatus(), tally.getIgnoredStatus()));
			}
		}
	}

	private void process(String arg) throws ParseException {
		List<Message> messages = MiscUtil.retreiveMessagesFromFile(arg);
		Set<String> packageIds = MiscUtil.retreivePackageIdsFromMessages(messages);
		List<BillingPackage> bpList = billingPackageDao.retrieveDups(new ArrayList<>(packageIds));
		Set<String> bpPackageIds = new TreeSet<>();
		for (BillingPackage bp : bpList) {
			bpPackageIds.add(bp.getFedexPkgId());
		}
		logger.info("Total billing packages found: " + bpPackageIds.size());
		packageIds.removeAll(bpPackageIds);
		logger.info("DOMESTIC PACKAGE EVENTS FROM MISSING PACKAGE IDS");
		displayTally(domesticEventStatGateway.retrieveEventRecords(new ArrayList<>(packageIds)));
		logger.info("RETURNS PACKAGE EVENTS FROM MISSING PACKAGE IDS");
		displayTally(returnsEventStatGateway.retrieveEventRecords(new ArrayList<>(packageIds)));
	}

	private void kickOffProcessing() throws IOException {
		for (String urlString : urls) {
			logger.info("Executing: " + urlString);
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			reader.readLine();
			reader.close();
		}
	}

	public static void main(String[] args) throws ParseException, IOException {
		// You should run this application to verify that the packages were replayed.
		if (args.length != 1) {
			args = new String[1];
			args[0] = "/Support/2020-03-09-REPLAY/messages.rec";
		}
		VerifyRun verifyRun = new VerifyRun();
		verifyRun.kickOffProcessing();
		verifyRun.process(args[0]);
	}
}
