package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

public class CheckMissingRecords {
	private static final Log log = LogFactory.getLog(CheckMissingRecords.class);

	private void process(String pkgIdFile, String msgFile) throws ParseException {
		List<String> pkgIds = MiscUtil.retrievePackageIdRecordsFromFile(pkgIdFile);
		List<Message> messages = MiscUtil.retreiveMessagesFromFile(msgFile);
		Set<String> msgPkgIds = MiscUtil.retreivePackageIdsFromMessages(messages);
		for (String pkgId : pkgIds) {
			if (!msgPkgIds.contains(pkgId)) {
				log.info(pkgId);
			}
		}
	}

	public static void main(String[] args) throws ParseException {
		CheckMissingRecords checkMissingRecords = new CheckMissingRecords();
		checkMissingRecords.process("/Support/2020-03-09/pkgIds.txt", "/Support/2020-03-09/messages.rec");
	}
}
