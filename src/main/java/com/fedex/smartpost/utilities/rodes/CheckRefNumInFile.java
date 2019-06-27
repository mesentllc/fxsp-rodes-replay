package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageHistoryGateway;
import com.fedex.smartpost.utilities.rodes.dao.OutboundOrdCrtEvntStatDao;
import com.fedex.smartpost.utilities.rodes.dao.UnmanifestedPackageDao;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.transportation.dao.PackageDao;
import com.fedex.smartpost.utilities.transportation.dao.PackageHistoryDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CheckRefNumInFile {
	private static final Log logger = LogFactory.getLog(CheckRefNumInFile.class);

	private void process(List<String> filenames) throws ParseException {
		int counter = 0;
		for (String filename : filenames) {
			logger.info("Filename: " + filename);
			List<Message> records = MiscUtil.retreiveMessagesFromFile(filename);
			for (Message message : records) {
				String parse = message.getPayload();
				parse = parse.substring(parse.indexOf("<BillingReferenceNumber>") + "<BillingReferenceNumber>".length());
				parse = parse.substring(0, parse.indexOf("<"));
				if (StringUtils.isBlank(parse)) {
					logger.info(message.getPackageId());
					counter++;
				}
			}
		}
		logger.info(counter + " total packages.");
	}

	public static void main(String[] args) throws ParseException {
		List<String> filenames;

		filenames = new ArrayList<>();
		filenames.add("/Support/2019-Jan-Replay/2019-04-02/ToBeReplayed-2019.04.02.rec");
		CheckRefNumInFile checkProposedFile = new CheckRefNumInFile();
		checkProposedFile.process(filenames);
	}
}