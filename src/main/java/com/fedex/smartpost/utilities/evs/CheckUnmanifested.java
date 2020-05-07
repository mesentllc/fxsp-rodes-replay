package com.fedex.smartpost.utilities.evs;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.evs.dao.UnmanifestedPackageDao;
import com.fedex.smartpost.utilities.evs.model.Unmanifested;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckUnmanifested {
	private static final Log log = LogFactory.getLog(CheckUnmanifested.class);
	private UnmanifestedPackageDao dao;

	private CheckUnmanifested() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		dao = (UnmanifestedPackageDao)context.getBean("evsUnmanifestedPackageDao");
	}

	private void process(String filename) {
		List<String> packageIds = MiscUtil.runThroughBusinessCommon(MiscUtil.retrievePackageIdRecordsFromFile(filename));
		printMap(dao.retrievePackages(packageIds));
	}

	private void printMap(List<Unmanifested> unmanifested) {
		Map<String, List<Unmanifested>> map = new HashMap<>();
		for (Unmanifested item  : unmanifested) {
			List<Unmanifested> subset;
			if (!map.containsKey(item.getUnmanStat())) {
				subset = new ArrayList<>();
				map.put(item.getUnmanStat(), subset);
			}
			else {
				subset = map.get(item.getUnmanStat());
			}
			subset.add(item);
		}
		for (String key : map.keySet()) {
			log.info(map.get(key).size() + " records with status of " + key);
		}
	}

	public static void main(String[] args) {
		CheckUnmanifested process = new CheckUnmanifested();
		process.process("/Support/2019-11-12/pkgIds.txt");
	}
}
