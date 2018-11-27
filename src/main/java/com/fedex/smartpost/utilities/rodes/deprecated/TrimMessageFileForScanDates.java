package com.fedex.smartpost.utilities.rodes.deprecated;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TrimMessageFileForScanDates {
	private static final Logger logger = LogManager.getLogger(TrimMessageFileForScanDates.class);
	private static final SimpleDateFormat scanDateSDF = new SimpleDateFormat("yyyy-MM-dd");

	private Date parseMessage(String message) throws ParseException {
		String scanDate = message.substring(message.indexOf("<SortDateTime>") + 14);
		return scanDateSDF.parse(scanDate.substring(0,scanDate.indexOf("<")));
	}

	private void process(String filename, String scanDates) throws FileNotFoundException, ParseException {
		PrintStream ps = new PrintStream("/messageSubset.txt");
		Set<Date> scanDateSet = stringToSet(scanDates);
		int counter = 0;

		List<Message> messages = MiscUtil.retreiveMessagesFromFile(filename);
		for (Message message : messages) {
			if (scanDateSet.contains(message.getScanDate())) {
				ps.println(message.getPayload());
			}
			if ((++counter % 1000) == 0) {
				logger.debug("Processed " + counter + " records.");
			}
		}
		logger.debug("Processed " + counter + " records.");
		ps.close();
	}

	private Set<Date> stringToSet(String scanDates) throws ParseException {
		String[] split = scanDates.split(",");
		Set<Date> scanDateSet = new TreeSet<>();

		for (String string :split) {
			scanDateSet.add(MiscUtil.SDF.parse(StringUtils.strip(string)));
		}
		return scanDateSet;
	}

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		// Use this application to rip out a subset of messages to be published, based on the
		// scan date that is provided to the application.  What will be produced will be a file
		// that only contains messages that have scan dates that were specified.
		if (args.length != 2) {
			args = new String[2];
			args[0] = "/rfs319572/Message_Dump-EFS.319572.txt";
			args[1] = "2015-08-25, 2015-08-28";
		}
		TrimMessageFileForScanDates trimMessageFileForScanDates = new TrimMessageFileForScanDates();
		trimMessageFileForScanDates.process(args[0], args[1]);
	}
}
