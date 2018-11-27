package com.fedex.smartpost.utilities.rodes;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fedex.smartpost.utilities.rodes.dao.BillingGroupSummaryDao;

public class CheckOutstandingScanDates {
	private static final Logger logger = LogManager.getLogger(CheckOutstandingScanDates.class);
	private BillingGroupSummaryDao billingGroupSummaryDao;

	public CheckOutstandingScanDates() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		billingGroupSummaryDao = (BillingGroupSummaryDao)context.getBean("billingGroupSummaryDao");
	}

	public static void main(String[] args) throws SQLException {
		CheckOutstandingScanDates check = new CheckOutstandingScanDates();
		check.process();
	}

	private void process() throws SQLException {
		Set<Date> scanDates = billingGroupSummaryDao.getOutstandingScanDates();
		logger.info(scanDates.size() + " outstanding scan dates ready for release.");
		logger.info("Scan Dates: " + concatDates(scanDates));
		billingGroupSummaryDao.close();
	}

	private static String concatDates(Set<Date> scanDates) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		StringBuilder sb = new StringBuilder();
		if (scanDates == null) {
			return sb.toString();
		}
		for (Date scanDate : scanDates) {
			sb.append(sdf.format(scanDate)).append(", ");
		}
		return sb.toString().substring(0, sb.length() - 2);
	}
}
