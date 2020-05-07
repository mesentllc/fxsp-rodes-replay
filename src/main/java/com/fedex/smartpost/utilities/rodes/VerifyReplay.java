package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageHistoryGateway;
import com.fedex.smartpost.utilities.rodes.model.Instance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VerifyReplay {
	private static final Log logger = LogFactory.getLog(VerifyReplay.class);
	private EDWDao edwDao;
	private BillingPackageDao billingPackageDao;
	private BillingPackageHistoryGateway billingPackageHistoryGateway;

	private VerifyReplay() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		billingPackageHistoryGateway = (BillingPackageHistoryGateway)context.getBean("billingPackageHistoryGateway");
	}

	private void process(List<String> filenames) throws SQLException, IOException {
		for (String filename : filenames) {
			logger.info("Filename: " + filename);
			List<String> packageIds = MiscUtil.runThroughBusinessCommon(MiscUtil.retrievePackageIdsFromUnmanifestedFile(filename));
//			logInstances(edwDao.retrieveReleasedPackages(packageIds));
			edwDao.retrieveReleasedPackages(packageIds);
			edwDao.getPackagesFromSmartPostPackageDetail(packageIds);
			billingPackageDao.retrieveDups(packageIds);
			billingPackageHistoryGateway.retrieveBillingPackageHistoryRecordsByPackageIds(packageIds);
		}
		closeConnections();
	}

	private void logInstances(List<Instance> instances) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			BufferedWriter bw = new BufferedWriter(
				new FileWriter(String.format("/Support/Kienast/%s-pkgIds.txt", sdf.format(new Date()))));
			for (Instance instance : instances) {
				bw.write(instance.getPackageId() + "\n");
			}
			bw.close();
		}
		catch (IOException ioe) {
			logger.error("Exception: ", ioe);
		}
	}

	private void closeConnections() throws SQLException {
		edwDao.close();
	}

	public static void main(String[] args) throws SQLException, IOException {
		List<String> filenames = new ArrayList<>();

		filenames.add("/Support/2020-03-09-REPLAY/replayUnmanifested-2020.03.23.txt");
		VerifyReplay verifyReplay = new VerifyReplay();
		verifyReplay.process(filenames);
	}
}