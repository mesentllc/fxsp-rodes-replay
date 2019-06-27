package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.factory.PublisherThreadFactory;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import com.fedex.smartpost.utilities.rodes.model.Instance;
import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.rodes.model.TransferContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PublishFile {
	private static final Log logger = LogFactory.getLog(PublishFile.class);
	private final BlockingQueue<List<Message>> messageQueue = new LinkedBlockingQueue<>();
	private final TransferContext messageContext = new TransferContext();
	private List<Thread> messageThreadList = new ArrayList<>();
	private BillingPackageDao billingPackageDao;
	private PublisherThreadFactory publisherThreadFactory;
	private EDWDao edwDao;
	private boolean justLog;

	public PublishFile(boolean publish) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		publisherThreadFactory = (PublisherThreadFactory)context.getBean("publisherThreadFactory");
		edwDao = (EDWDao)context.getBean("edwDao");
		justLog = !publish;
	}

	private void setupThreads(int threadCount, String msgType) {
		messageContext.setBatchSize(500);
		messageContext.setStringQueue(messageQueue);
		messageThreadList = new ArrayList<>(threadCount);
		for (int index = 0; index < threadCount; index++) {
			Thread thread = null;
			switch (msgType) {
				case "SS":
					thread = publisherThreadFactory.createBean(index, messageQueue, justLog);
					break;
				case "OC":
					thread = publisherThreadFactory.createOCBean(index, messageQueue, justLog);
					break;
				case "PD":
					thread = publisherThreadFactory.createPDBean(index, messageQueue, justLog);
			}
			if (thread != null) {
				thread.start();
				messageThreadList.add(thread);
			}
			else {
				logger.info("Unknown publisher type [" + msgType + "].");
				break;
			}
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

	private void process(String filename, int threadCount, String msgType) throws IOException, ParseException {
		Set<String> packageIds = new TreeSet<>();

		if (justLog) {
			logger.info("WILL NOT PUBLISH - JUST SEND MESSAGES TO LOG SET!!!");
		}
		setupThreads(threadCount, msgType);
		Collection<Message> messages = MiscUtil.retreiveMessagesFromFile(filename);
		for (Message message : messages) {
			packageIds.add(message.getPackageId());
		}
//		Set<String> existingPackageIds = getExistingPackages(packageIds);
//		Set<String> existingPackageIds = new HashSet<>();
		Set<String> existingPackageIds = extractPackageIds(edwDao.retrieveReleasedPackages(new ArrayList<>(packageIds)));
		logger.info("Package Ids that already exist in BILLING_PACKAGE: " + existingPackageIds.size());
		processMessages(messages, existingPackageIds);
		messageContext.completeBatch();
		try {
			stopThreads();
		}
		catch (InterruptedException e) {
			logger.info("Threads abruptly stopped!", e);
		}
	}

	private Set<String> extractPackageIds(List<Instance> packageInstances) {
		Set<String> packageIds = new TreeSet<>();
		for (Instance instance : packageInstances) {
			packageIds.add(instance.getPackageId());
		}
		return packageIds;
	}

	private void processMessages(Collection<Message> messages, Set<String> existingPackageIds) throws IOException {
		Calendar cal = Calendar.getInstance();
		BufferedWriter bw = new BufferedWriter(new FileWriter("/Support/duplicates-" + MiscUtil.SDF.format(cal.getTime()) + ".txt", true));
		Set<Long> replayedUpns = new HashSet<>();
		int counter = 0;

		for (Message message : messages) {
			if (replayedUpns.contains(message.getUpn())) {
				// If we have duplicate messages, remove them!!
				continue;
			}
			if (existingPackageIds.contains(message.getPackageId())) {
				bw.write(message.getPayload() + "\r\n");
			}
			else {
				replayedUpns.add(message.getUpn());
				if ((++counter % 1000) == 0) {
					logger.debug("Processed " + counter + " records.");
				}
				messageContext.addToList(message);
			}
		}
		logger.debug("Processed " + counter + " records total.");
		bw.close();
	}

	private Set<String> getExistingPackages(Set<String> packageIdsToProcess) {
		List<BillingPackage> billingPackages = billingPackageDao.retrieveDups(new ArrayList<>(packageIdsToProcess));
		Set<String> existingPackageIds = new TreeSet<>();

		for (BillingPackage billingPackage : billingPackages) {
			existingPackageIds.add(billingPackage.getFedexPkgId());
		}
		return existingPackageIds;
	}

	public static void main(String[] args) throws IOException, ParseException {
		// Well, I am sure you figured it out that this application will accept the name of
		// a file that contains SORTSCAN/ORDERCREATE messages to be sent to JMS Queues, to be processed
		// by RODeS downstream processes.
		if (args.length != 3) {
			args = new String[3];
			args[0] = "/Support/Kienast/onePkgId.rec";
			args[1] = "1";
			args[2] = "SS"; // SS, OC or PD
		}
		PublishFile publishFile = new PublishFile(true);
		publishFile.process(args[0], Integer.parseInt(args[1]), args[2]);
	}
}
