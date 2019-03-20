package com.fedex.smartpost.utilities.rodes;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class DuplicatePackageCountInFile {
	private static final Log logger = LogFactory.getLog(DuplicatePackageCountInFile.class);

	public static void main(String[] args) throws IOException {
		DuplicatePackageCountInFile.process("D:/Support/2018-02-15/replayList-mod.txt");
	}

	private static void process(String filename) throws IOException {
		logger.info("Reading " + filename);
		int recordCount = 0;
		Map<String, Integer> packageCounts = new TreeMap<>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		Integer count;
		while (br.ready()) {
			recordCount++;
			String packageId = br.readLine().trim();
			count = packageCounts.getOrDefault(packageId, 0);
			packageCounts.put(packageId, ++count);
		}
		logger.info(recordCount + " records read from file.");
		logger.info(packageCounts.size() + " unique package ids found.");
		outputDups(packageCounts);
	}

	private static void outputDups(Map<String, Integer> packageCounts) {
		int totalDups = 0;
		for (String packageId : packageCounts.keySet()) {
			int recordsFound = packageCounts.get(packageId);
			if (recordsFound > 1) {
				totalDups += recordsFound - 1;
				logger.info(recordsFound + " Records found for " + packageId);
			}
		}
		logger.info(totalDups + " duplicate records found.");
	}
}
