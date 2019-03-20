package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.lang.StringUtils;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BuildMessageFileFromMaster {
	private static final Log logger = LogFactory.getLog(BuildMessageFileFromMaster.class);
	private EDWDao edwDao;

	public BuildMessageFileFromMaster() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void process(String scanDates) throws ParseException, IOException, SQLException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		long processed = 0;
		Calendar cal = Calendar.getInstance();
		String filename = "/Support/ToBeReplayed-" + sdf.format(cal.getTime()) + ".rec";
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(MiscUtil.EXTRACTED, true));
		List<Long> upnList = MiscUtil.retreiveUPNsFromFile(MiscUtil.SS_MASTER_FILE, buildScanDates(scanDates));
		EDWResults edwResults = edwDao.retrievePackageIdsViaUPN(upnList, EDWDao.SELECT_PACKAGES_FROM_PF_USING_UPN, false);
		for (Date date : edwResults.getScanDates()) {
			List<Message> messages = edwResults.getMessages(date);
			if (messages != null) {
				for (Message message : messages) {
					MiscUtil.writeMessageFormat(bw, message);
					bw2.write(message.getUpn() + "\r\n");
					processed++;
				}
			}
		}
		bw.close();
		bw2.close();
		logger.info("File built: " + filename);
		logger.info("Total Records to be replayed: " + processed);
		edwDao.close();
	}

	private Set<Date> buildScanDates(String scanDateString) throws ParseException {
		Set<Date> scanDateSet = new TreeSet<>();

		if (scanDateString == null) {
			return null;
		}
		for (String scanDate : Arrays.asList(StringUtils.deleteWhitespace(scanDateString).split(","))) {
			scanDateSet.add(MiscUtil.SDF.parse(scanDate));
		}
		return scanDateSet;
	}

	public static void main(String[] args) throws IOException, ParseException, SQLException {
		if (args.length != 1) {
			args = new String[1];
//			args[0] = null;
//			args[0] = "2017-12-05, 2017-12-06, 2017-12-07, 2017-12-08, 2017-12-09, 2017-12-10, " +
//			          "2017-12-11, 2017-12-12, 2017-12-13";
			args[0] = "2017-12-14, 2017-12-15, 2017-12-16, 2017-12-17, 2017-12-18, 2017-12-19, 2018-01-10, 2018-01-12";
		}
		BuildMessageFileFromMaster buildMessageFileFromMaster = new BuildMessageFileFromMaster();
		buildMessageFileFromMaster.process(args[0]);
	}
}
