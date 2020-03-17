package com.fedex.smartpost.utilities.analysis;

import com.fedex.smartpost.utilities.MiscUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

public class CompareFiles {
	private static final Log log = LogFactory.getLog(CompareFiles.class);

	private void process(String messageFile, String filename) throws ParseException {
		Set<String> msgPkgIds = MiscUtil.retreivePackageIdsFromMessages(MiscUtil.retreiveMessagesFromFile(messageFile));
		List<String> packageIds = MiscUtil.retreivePackageIdRecordsFromFile(filename);
		for (String packageId : packageIds) {
			if (msgPkgIds.contains(packageId)) {
				log.info("Duplicate package id: " + packageId);
			}
		}
	}

	public static void main(String[] args) throws ParseException {
		CompareFiles compareFiles = new CompareFiles();
		compareFiles.process("/Support/2020-03-09/messages.rec","/Support/2020-03-09/jacobPkgId.txt");
	}
}
