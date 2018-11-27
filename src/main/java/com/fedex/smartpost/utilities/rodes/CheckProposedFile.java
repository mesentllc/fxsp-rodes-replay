package com.fedex.smartpost.utilities.rodes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageException;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.OutboundOrdCrtEvntStatDao;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;

public class CheckProposedFile {
	private static final Logger logger = LogManager.getLogger(CheckProposedFile.class);
	private EDWDao edwDao;
	private BillingPackageDao billingPackageDao;
	private OutboundOrdCrtEvntStatDao outboundOrdCrtEvntStatDao;

	public CheckProposedFile() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		outboundOrdCrtEvntStatDao = (OutboundOrdCrtEvntStatDao)context.getBean("outboundOrdCrtEvntStatDao");
	}

	private void process(List<String> filenames, boolean saveIt) throws IOException, SQLException {
		for (String filename : filenames) {
			logger.info("Filename: " + filename);
			if (saveIt) {
				new UpdateMasterReplayFile(edwDao, filename);
			}
			else {
				List<String> packageIds = runThroughBusinessCommon(MiscUtil.retreivePackageIdRecordsFromFile(filename));
				dumpUnreleased(edwDao.retrieveUnreleasedPackageIdsAndUPNs(packageIds));
				List<BillingPackage> dups = billingPackageDao.retrieveDups(packageIds);
//				dumpIds(dups);
//				outboundOrdCrtEvntStatDao.retrievePackages(packageIds);
			}
		}
		closeConnections();
	}

	private void closeConnections() throws SQLException {
		edwDao.close();
		billingPackageDao.close();
		outboundOrdCrtEvntStatDao.close();
	}

	private void dumpUnreleased(EDWResults edwResults) throws IOException {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/Support/unreleased-" + sdf.format(now) + ".txt"));
		for (Date scanDate : edwResults.getScanDates()) {
			List<Message> messageList = edwResults.getMessages(scanDate);
			for (Message message : messageList) {
				bw.write(message.getPackageId() + "\r\n");
			}
		}
		bw.close();
	}

	private List<String> runThroughBusinessCommon(List<String> packageIds) {
		List<String> processedList = new ArrayList<>();

		for (String packageId : packageIds) {
			try {
				FxspPackage fxspPackage = FxspPackageFactory.createFromUnknown(packageId.trim());
				processedList.add(fxspPackage.getUspsBarcode().getPackageIdentificationCode().substring(2));
			}
			catch (FxspPackageException e) {
				logger.debug("Exception found: ", e);
			}
		}
		return processedList;
	}

	private void dumpIds(List<BillingPackage> billingPackages) {
		for (BillingPackage billingPackage : billingPackages) {
			logger.info(billingPackage.getFedexPkgId() + " -> Status: " + billingPackage.getStatus());
		}
	}

	public static void main(String[] args) throws IOException, SQLException {
		// Run this process to check how many package ids are not existing currently in the EDW Database.
		//  It might prove that the file doesn't need to be replayed.
		List<String> filenames;

		if (args.length != 1) {
			filenames = new ArrayList<>();
//			filenames.add("/Support/02.2016/replay-2016-02.txt");
//			filenames.add(MiscUtil.SS_MASTER_FILE);
			filenames.add("D:/Support/2018-11-27/pkgIds.txt");
		}
		else {
			filenames = Arrays.asList(args);
		}
		CheckProposedFile checkProposedFile = new CheckProposedFile();
		checkProposedFile.process(filenames, false);
	}
}