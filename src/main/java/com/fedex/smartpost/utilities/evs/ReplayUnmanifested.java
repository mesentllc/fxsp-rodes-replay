package com.fedex.smartpost.utilities.evs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageException;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import com.fedex.smartpost.postal.types.UnmanifestedComplexType;
import com.fedex.smartpost.postal.types.UspsPostage;
import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.evs.converter.UspsPostageTransactionMessageConverter;
import com.fedex.smartpost.utilities.evs.factory.PublisherThreadFactory;
import com.fedex.smartpost.utilities.evs.model.PostalPackage;
import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.rodes.model.TransferContext;

public class ReplayUnmanifested {
	private static final Logger logger = LogManager.getLogger(ReplayUnmanifested.class);
	private UspsPostageTransactionMessageConverter postageTransactionMessageConverter;
	private final BlockingQueue<List<Message>> messageQueue = new LinkedBlockingQueue<>();
	private final TransferContext messageContext = new TransferContext();
	private List<Thread> messageThreadList = new ArrayList<>();
	private PublisherThreadFactory publisherThreadFactory;
	private EDWDao edwDao;
	private boolean justLog;

	public ReplayUnmanifested(boolean publish) {
		justLog = !publish;
	}

	private void setupThreads(int threadCount) {
		messageContext.setBatchSize(500);
		messageContext.setStringQueue(messageQueue);
		messageThreadList = new ArrayList<>(threadCount);
		for (int index = 0; index < threadCount; index++) {
			Thread thread = publisherThreadFactory.createBean(index, messageQueue, justLog);
			thread.start();
			messageThreadList.add(thread);
		}
	}

	private void stopThreads() throws InterruptedException {
		for (Thread thread : messageThreadList) {
			messageQueue.put(new ArrayList<>());
		}
		for (Thread thread : messageThreadList) {
			thread.join();
		}
	}

	private static UspsPostage convertToUsps(PostalPackage postalPackage, XMLGregorianCalendar gregorianCalendar) {
		UspsPostage uspsPostage = new UspsPostage();
		uspsPostage.setPackageId(postalPackage.getParcelId());
		UnmanifestedComplexType umct = new UnmanifestedComplexType();
		umct.setStatus("TU");
		umct.setEventDate(gregorianCalendar);
		uspsPostage.setUnmanifested(umct);
		return uspsPostage;
	}

	private void process() throws IOException, InterruptedException, SQLException, ParseException, DatatypeConfigurationException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-evs.xml");
		postageTransactionMessageConverter = (UspsPostageTransactionMessageConverter)applicationContext.getBean("uspsPostageTransactionMessageConverter");
		publisherThreadFactory = (PublisherThreadFactory)applicationContext.getBean("publisherThreadFactory");
		edwDao = (EDWDao)applicationContext.getBean("edwDao");

		PostalPackage postalPackage;
		Map<String, XMLGregorianCalendar> packageIds = readPackageIdAndScanDate("/Support/2016.10.11/replayUnmanifested.txt");

		if (justLog) {
			logger.info("WILL NOT PUBLISH - JUST SEND MESSAGES TO LOG SET!!!");
		}
		logger.info(packageIds.size() + " records to process...");
		List<String> releasedPackages = edwDao.getReleasedPackages(packageIds.keySet());
		logger.info(releasedPackages.size() + " records already released...");
		dumpToFile("/Support/2016.10.11/alreadyReleased.txt", releasedPackages);
		packageIds = removeReleased(packageIds, releasedPackages);
		logger.info(packageIds.size() + " records left to process...");
		dumpToFile("/Support/2016.10.11/tuSentToRodes.txt", packageIds.keySet());
//		setupThreads(5);
//		for (String packageId : packageIds.keySet()) {
//			postalPackage = new PostalPackage();
//			postalPackage.setParcelId(packageId);
//			messageContext.addToList(new Message(null, null, postalPackage.getParcelId(),
//												 postageTransactionMessageConverter.createPostageTransactionMessage(convertToUsps(postalPackage, packageIds.get(packageId)))));
//		}
//		logger.info("Send EOM sequence to threads...");
//		messageContext.completeBatch();
//		logger.info("Waiting for threads to complete...");
//		stopThreads();
	}

	private static void dumpToFile(String filename, Collection<String> packageIdList) {
		logger.info("Creating " + filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			for (String packageId : packageIdList) {
				bw.write(packageId + "\r\n");
			}
			bw.close();
		}
		catch (IOException e) {
			logger.error("Unable to create and write to file: " + filename);
		}
	}

	private static Map<String, XMLGregorianCalendar> removeReleased(Map<String, XMLGregorianCalendar> packageIds, List<String> releasedPackages) {
		Map<String, XMLGregorianCalendar> scrubbed = new HashMap<>();

		for (String packageId : packageIds.keySet()) {
			if (!releasedPackages.contains(packageId)) {
				scrubbed.put(packageId, packageIds.get(packageId));
			}
		}
		return scrubbed;
	}

	private static Map<String, XMLGregorianCalendar> readPackageIdAndScanDate(String filename) throws IOException, ParseException, DatatypeConfigurationException {
		Map<String, XMLGregorianCalendar> packageIds = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		FxspPackage fxspPackage;

		while (br.ready()) {
			String line = br.readLine().trim();
			String[] split = line.split("\\|");
			GregorianCalendar scanDate = new GregorianCalendar();
			if (split.length > 1) {
				if ('-' == split[1].charAt(4)) {
					scanDate.setTime(MiscUtil.SDF.parse(split[1]));
				}
				if ('/' == split[1].charAt(2)) {
					scanDate.setTime(MiscUtil.SDF2.parse(split[1]));
				}
			}
			// If the package ids are mixed with 30-character barcodes, or just want to be sure,
			// use the FxspPackageFactory... Otherwise if the file contains package ids that have the same
			// length, you could just truncate... Either way.
			XMLGregorianCalendar xmlSacnDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(scanDate);
			try {
				fxspPackage = FxspPackageFactory.createFromUnknown(split[0]);
				packageIds.put(fxspPackage.getUspsBarcode().getPackageIdentificationCode().substring(2), xmlSacnDate);
			}
			catch (FxspPackageException e) {
				logger.info("Bad Package", e);
			}
		}
		br.close();
		return packageIds;
	}

	public static void main(String[] args) throws IOException, InterruptedException, SQLException, ParseException, DatatypeConfigurationException {
		ReplayUnmanifested replayUnmanifested = new ReplayUnmanifested(true);
		replayUnmanifested.process();
	}
}
