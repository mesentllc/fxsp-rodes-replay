package com.fedex.smartpost.utilities.evs;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.evs.dao.UspsPackageEventDao;
import com.fedex.smartpost.utilities.evs.model.UspsPackageEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CheckUspsEvents {
	private static final Log log = LogFactory.getLog(CheckUspsEvents.class);
	private UspsPackageEventDao dao;

	private CheckUspsEvents() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		dao = (UspsPackageEventDao)context.getBean("evsUspsEventDao");
	}

	private void process(String filename) {
		List<String> packageIds = MiscUtil.runThroughBusinessCommon(MiscUtil.retreivePackageIdRecordsFromFile(filename));
		printMap(dao.retrieveEvents(packageIds));
	}

	private void printMap(List<UspsPackageEvent> packageList) {
		Map<String, List<UspsPackageEvent>> map = new TreeMap<>();
		for (UspsPackageEvent event : packageList) {
			List<UspsPackageEvent> subset;
			if (!map.containsKey(event.getEventCd())) {
				subset = new ArrayList<>();
				map.put(event.getEventCd(), subset);
			}
			else {
				subset = map.get(event.getEventCd());
			}
			subset.add(event);
		}
		for (String key : map.keySet()) {
			log.info(map.get(key).size() + " records with event code of " + key);
		}
	}

	public static void main(String[] args) {
		CheckUspsEvents checkUspsEvents = new CheckUspsEvents();
		checkUspsEvents.process("/Support/2019-11-12/pkgIds.txt");
	}
}
