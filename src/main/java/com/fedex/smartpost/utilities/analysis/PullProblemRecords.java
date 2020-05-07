package com.fedex.smartpost.utilities.analysis;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class PullProblemRecords {
	private static final Log log = LogFactory.getLog(PullProblemRecords.class);

	public static void main(String[] args1) throws IOException, ParseException {
		PullProblemRecords ppr = new PullProblemRecords();
		ppr.process("/Support/missingPackageIds.rec", "/Support/Kienast/Issue/2019-08-08-NoCustRef.txt");
	}

	private void process(String recFile, String pkgFile) throws ParseException, IOException {
		List<Message> messages = MiscUtil.retreiveMessagesFromFile(recFile);
		List<String> badPackages = MiscUtil.retrievePackageIdRecordsFromFile(pkgFile);
		BufferedWriter bw = new BufferedWriter(new FileWriter(pkgFile.substring(0,pkgFile.lastIndexOf('/')) +
		                                                      "/extractedRecords.txt"));
		int cntr = 0;
		for (Message message : messages) {
			if (badPackages.contains(message.getPackageId())) {
				bw.write(message.getUpn() + "|" + message.getPayload() + "\n");
				cntr++;
			}
		}
		log.info(cntr + " records written.");
		bw.close();
	}
}
