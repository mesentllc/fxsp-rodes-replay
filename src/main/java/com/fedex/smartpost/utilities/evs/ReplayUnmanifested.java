package com.fedex.smartpost.utilities.evs;

import com.fedex.smartpost.postal.types.UnmanifestedComplexType;
import com.fedex.smartpost.postal.types.UspsPostage;
import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.evs.converter.UspsPostageTransactionMessageConverter;
import com.fedex.smartpost.utilities.evs.factory.PublisherThreadFactory;
import com.fedex.smartpost.utilities.evs.model.PostalPackage;
import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.rodes.model.TransferContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReplayUnmanifested {
	private static final Log logger = LogFactory.getLog(ReplayUnmanifested.class);
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

	private void process() throws IOException, ParseException, DatatypeConfigurationException, InterruptedException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-evs.xml");
		postageTransactionMessageConverter = (UspsPostageTransactionMessageConverter)applicationContext.getBean("uspsPostageTransactionMessageConverter");
		publisherThreadFactory = (PublisherThreadFactory)applicationContext.getBean("publisherThreadFactory");
		edwDao = (EDWDao)applicationContext.getBean("edwDao");

		PostalPackage postalPackage;
		Map<String, XMLGregorianCalendar> packageIds = MiscUtil.readPackageIdAndScanDate("/Support/2019-03-14/2019-04-02-Replay/replayUnmanifested.txt");

		if (justLog) {
			logger.info("WILL NOT PUBLISH - JUST SEND MESSAGES TO LOG SET!!!");
		}
		logger.info(packageIds.size() + " records to process...");
//		List<String> releasedPackages = edwDao.getReleasedPackages(packageIds.keySet());
//		logger.info(releasedPackages.size() + " records already released...");
//		dumpToFile("/Support/2019.03.14/2019-04-02-Replay/alreadyReleased.txt", releasedPackages);
		List<String> packageIdsFromFile = MiscUtil.retrievePackageIdRecordsFromFile("/Support/2019-03-14/2019-04-02-Replay/Status1_packages.txt");
//		packageIds = removeReleased(packageIds, releasedPackages);
		packageIds = removeMissingPackageIds(packageIds, packageIdsFromFile);
		logger.info(packageIds.size() + " records left to process...");
		dumpToFile("/Support/2019-03-14/2019-04-02-Replay/tuSentToRodes.txt", packageIds.keySet());
		setupThreads(5);
		for (String packageId : packageIds.keySet()) {
			postalPackage = new PostalPackage();
			postalPackage.setParcelId(packageId);
			messageContext.addToList(new Message(null, null, postalPackage.getParcelId(),
												 postageTransactionMessageConverter.createPostageTransactionMessage(convertToUsps(postalPackage, packageIds.get(packageId)))));
		}
		logger.info("Send EOM sequence to threads...");
		messageContext.completeBatch();
		logger.info("Waiting for threads to complete...");
		stopThreads();
	}

	private Map<String, XMLGregorianCalendar> removeMissingPackageIds(Map<String, XMLGregorianCalendar> packageIds, List<String> packageIdsFromFile) {
		Map<String, XMLGregorianCalendar> scrubbed = new HashMap<>();

		for (String packageId : packageIdsFromFile) {
			if (packageIds.containsKey(packageId)) {
				scrubbed.put(packageId, packageIds.get(packageId));
			}
		}
		return scrubbed;
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

	public static void main(String[] args) throws IOException, SQLException, ParseException, DatatypeConfigurationException, InterruptedException {
		ReplayUnmanifested replayUnmanifested = new ReplayUnmanifested(true);
		replayUnmanifested.process();
	}
}
