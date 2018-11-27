package com.fedex.smartpost.utilities.rodes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;

public class BuildMessagesForReplay {
	private static final Logger logger = LogManager.getLogger(BuildMessagesForReplay.class);
	private EDWDao edwDao;

	public BuildMessagesForReplay() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

//	// Was used for the Hadoop project
//	private void process(String filename) throws IOException {
//		int processed = 0;
//		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
//
//		logger.info("Starting the Message Extract.");
//		EDWResults edwResults = edwDao.retrieveMessagesViaSQL(EDWDao.GET_PACKAGES_FOR_REPLAY);
//		for (Date date : edwResults.getScanDates()) {
//			List<Message> messages = edwResults.getMessages(date);
//			if (messages != null) {
//				for (Message message : messages) {
//					MiscUtil.writeMessageFormat(bw, message);
//					processed++;
//				}
//			}
//		}
//		bw.close();
//		logger.info("File built: " + filename);
//		logger.info("Total Records to be replayed: " + processed);
//	}

	private void process(String inFile, String filename) throws IOException {
		int processed = 0;
		List<String> packageIds = MiscUtil.retreivePackageIdRecordsFromFile(inFile);
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

//		EDWResults edwResults = edwDao.retrieveMessagesForReplayWOC(packageIds);
		EDWResults edwResults = edwDao.retrievePackageIds(packageIds, EDWDao.SELECT_PACKAGES_FROM_PF);
		for (Date date : edwResults.getScanDates()) {
			List<Message> messages = edwResults.getMessages(date);
			if (messages != null) {
				for (Message message : messages) {
					MiscUtil.writeMessageFormat(bw, message);
					processed++;
				}
			}
		}
		bw.close();
		logger.info("File built: " + filename);
		logger.info("Total Records to be replayed: " + processed);
	}

	public static void main(String[] args) throws IOException {
		BuildMessagesForReplay buildMessagesForReplay = new BuildMessagesForReplay();
		buildMessagesForReplay.process("/Support/2016.10.28/replayList.txt", "/Support/2016.10.28/toBeReplayed-PF.rec");
	}
}