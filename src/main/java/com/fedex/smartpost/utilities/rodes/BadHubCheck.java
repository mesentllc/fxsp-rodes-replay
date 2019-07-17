package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.DomesticEventGateway;
import com.fedex.smartpost.utilities.rodes.dao.ESmartpostHubXrefDao;
import com.fedex.smartpost.utilities.rodes.model.EventRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class BadHubCheck {
	private static final Log log = LogFactory.getLog(BadHubCheck.class);
	private DomesticEventGateway domesticEventGateway;
	private ESmartpostHubXrefDao eSmartpostHubXrefDao;

	private BadHubCheck() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		domesticEventGateway = (DomesticEventGateway)context.getBean("domesticEventGateway");
		eSmartpostHubXrefDao = (ESmartpostHubXrefDao)context.getBean("eSmartpostHubXrefDao");
	}

	private void process(String filename) throws IOException {
		int total = 0;
		Map<String, List<String>> badHubIdPackageMap = new TreeMap<>();
		Set<String> packageIds = MiscUtil.retrievePackageIdsFromFile(filename);
		log.info("Records retrieved: " + packageIds.size());
		List<EventRecord> events = domesticEventGateway.retrieveHubIds(new ArrayList<>(packageIds));
		log.info("Event records found for packages in " + filename + ": " + events.size());
		List<String> validHubIds = eSmartpostHubXrefDao.retrieveHubIds();
		for (EventRecord record : events) {
			String hubId = record.getHubCode();
			if (hubId != null && !validHubIds.contains(hubId)) {
				List<String> packageIdList = badHubIdPackageMap.computeIfAbsent(hubId, k -> new ArrayList<>());
				packageIdList.add(record.getFedexPackageId());
				total++;
			}
		}
		log.info("Number of invalid hub codes found: " + badHubIdPackageMap.keySet().size());
		log.info("Total number of package events with bad hub codes: " + total);
		for (String key : badHubIdPackageMap.keySet()) {
			log.info("Hub: " + key + " had " + badHubIdPackageMap.get(key).size() + " package ids associated.");
		}
		dumpFindings(badHubIdPackageMap);
	}

	private void dumpFindings(Map<String, List<String>> badHubIdPackageMap) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("/Support/badHubReport-%s.txt", sdf.format(new Date()))));
		for (String key : badHubIdPackageMap.keySet()) {
			for (String packageId : badHubIdPackageMap.get(key)) {
				bw.write(key + "|" + packageId + "\n");
			}
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		BadHubCheck badHubCheck = new BadHubCheck();
		badHubCheck.process("/Support/missingPackageIds.txt");
	}
}
