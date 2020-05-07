package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageException;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SortUngroupPackages {
	private static final Log logger = LogFactory.getLog(SortUngroupPackages.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
	private BillingPackageDao billingPackageDao;

	public SortUngroupPackages() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
	}

	public static void main(String[] args) throws IOException, ParseException {
		SortUngroupPackages sortUngroupPackages = new SortUngroupPackages();
		if (args.length != 1) {
			args = new String[1];
			args[0] = "D:/Support/2018-02-15/unGrouped.txt";
		}
		sortUngroupPackages.process(args[0],false);
	}

	private void process(String filename, boolean logIt) throws IOException, ParseException {
		logger.info("Filename: " + filename);
		List<String> packageIds = runThroughBusinessCommon(MiscUtil.retrievePackageIdRecordsFromFile(filename));
		List<BillingPackage> dups = billingPackageDao.retrieveDups(packageIds);
		Map<Date, Set<String>> packageMap = sort(dups);
		makeFile(packageMap);
		if (logIt) {
			dumpIds(packageMap);
		}
	}

	private List<String> runThroughBusinessCommon(List<String> packageIds) {
		List<String> processedList = new ArrayList<>();

		for (String packageId : packageIds) {
			try {
				FxspPackage fxspPackage = FxspPackageFactory.createFromUnknown(packageId.trim());
				processedList.add(fxspPackage.getUspsBarcode().getPackageIdentificationCode().substring(2));
			}
			catch (FxspPackageException e) {
				logger.debug("Exception found: ", e);
			}
		}
		return processedList;
	}

	private Map<Date, Set<String>> sort(List<BillingPackage> billingPackages) throws ParseException {
		Map<Date, Set<String>> packageMap = new TreeMap<>();

		logger.info("Collecting " + billingPackages.size() + " by origin hub scan date.");
		for (BillingPackage billingPackage : billingPackages) {
			Date hubDate = SDF.parse(SDF.format(billingPackage.getOriginHubScanDt()));
			String packageId = billingPackage.getFedexPkgId();
			Set<String> packageSet = packageMap.computeIfAbsent(hubDate, k -> new TreeSet<>());
			packageSet.add(packageId);
		}
		logger.info("Found " + packageMap.keySet().size() + " unique scan dates.");
		return packageMap;
	}

	private void dumpIds(Map<Date, Set<String>> packageMap) {
		for (Date hubDate : packageMap.keySet()) {
			for (String packageId : packageMap.get(hubDate)) {
				logger.info(SDF.format(hubDate) + '|' + packageId);
			}
		}
	}

	private void makeFile(Map<Date, Set<String>> packageMap) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		BufferedWriter br = new BufferedWriter(new FileWriter("D:\\Support\\2018-02-15\\toGroup.txt"));
		for (Date hubDate : packageMap.keySet()) {
			for (String packageId : packageMap.get(hubDate)) {
				br.write(sdf.format(hubDate) + '|' + packageId + '\n');
			}
		}
		br.close();
	}
}
