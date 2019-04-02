package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ExtractMessagesFromRecMaster {
	private static final Log logger = LogFactory.getLog(ExtractMessagesFromRecMaster.class);

	private void process(String scanDates, boolean isOC) throws ParseException, IOException {
		if (isOC) {
			process(scanDates, MiscUtil.OC_MASTER_REC_FILE);
		}
		else {
			process(scanDates, MiscUtil.SS_MASTER_REC_FILE);
		}
	}

	private void process(String scanDates, String inFile) throws IOException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		long processed = 0;
		List<Message> messageList;
		Calendar cal = Calendar.getInstance();
		Set<Date> dateSet = buildScanDates(scanDates);
		String filename = "/Support/ToBeReplayed-" + sdf.format(cal.getTime()) + ".rec";
		String pkgFile = "/Support/replayUnmanifested-" + sdf.format(cal.getTime()) + ".txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(MiscUtil.EXTRACTED, true));
		BufferedWriter bw3 = new BufferedWriter(new FileWriter(pkgFile, true));

		messageList = MiscUtil.retreiveMessagesFromFile(inFile);
		for (Message message : messageList) {
			if (dateSet == null || dateSet.contains(message.getScanDate())) {
				MiscUtil.writeMessageFormat(bw, message);
				bw2.write(message.getUpn() + "\r\n");
				bw3.write(message.getPackageId() + '|' + MiscUtil.SDF.format(message.getScanDate()) + "\r\n");
				processed++;
			}
		}
		bw.close();
		bw2.close();
		bw3.close();
		logger.info("File built: " + filename);
		logger.info("File built: " + pkgFile);
		logger.info("Total Records to be replayed: " + processed);
	}

	private Set<Date> buildScanDates(String scanDateString) {
		Set<Date> scanDateSet = new TreeSet<>();

		if (scanDateString == null) {
			return null;
		}
		for (String scanDate : Arrays.asList(StringUtils.deleteWhitespace(scanDateString).split(","))) {
			try {
				scanDateSet.add(MiscUtil.SDF.parse(scanDate));
			}
			catch (ParseException e) {
				logger.error(scanDate + " is an invalid date - skipping.");
			}
		}
		return scanDateSet;
	}

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length != 1) {
			args = new String[1];
			args[0] = "2019-01-11, 2019-01-12, 2019-01-13, 2019-01-14, 2019-01-15, " +
			          "2019-01-16, 2019-01-17, 2019-01-18, 2019-01-19, 2019-01-20";
//			args[0] = "2019-01-21, 2019-01-22, 2019-01-23, 2019-01-24, 2019-01-25, " +
//			          "2019-01-26, 2019-01-27, 2019-01-28, 2019-01-29, 2019-01-30";
//			args[0] = "2019-01-31, 2019-02-01, 2019-02-08";
//			args[0] = null;
		}
		ExtractMessagesFromRecMaster buildMessageFileFromMaster = new ExtractMessagesFromRecMaster();
//		buildMessageFileFromMaster.process(args[0], true);
		buildMessageFileFromMaster.process(args[0], "D:\\Support\\2019-03-14\\EPDI_Jan.rec");
	}
}
