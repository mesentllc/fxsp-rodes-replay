package com.fedex.smartpost.utilities.evs;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.evs.dao.PackageDao;
import com.fedex.smartpost.utilities.evs.model.Package;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CheckPackages {
	private static final Log log = LogFactory.getLog(CheckPackages.class);
	private PackageDao packageDao;

	private CheckPackages() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		packageDao = (PackageDao)context.getBean("evsPackageDao");
	}

	private void process(String filename) {
		List<String> packageIds = MiscUtil.runThroughBusinessCommon(MiscUtil.retreivePackageIdRecordsFromFile(filename));
		printMap(packageDao.retrievePackages(packageIds));
	}

	private String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyy");
		return (date == null) ? "NULL" : sdf.format(date);
	}

	private void printMap(List<Package> packageList) {
		Map<String, Map<String, List<Package>>> map = new TreeMap<>();
		for (Package pkg : packageList) {
			String key = dateToString(pkg.getMailDateTmstp());
			Map<String, List<Package>> subset = map.computeIfAbsent(key, k -> new HashMap<>());
			List<Package> sublist = subset.computeIfAbsent(pkg.getEvsReleaseTypeCd(), k -> new ArrayList<>());
			sublist.add(pkg);
		}
		for (String key : map.keySet()) {
			log.info("Mail Date: " + key);
			for (String subKey : map.get(key).keySet()) {
				log.info("     " + map.get(key).get(subKey).size() + " records with a release code of " + subKey);
			}
		}
	}

	public static void main(String[] args) {
		CheckPackages checkPackages = new CheckPackages();
		checkPackages.process("/Support/2019-11-12/pkgIds.txt");
	}
}
