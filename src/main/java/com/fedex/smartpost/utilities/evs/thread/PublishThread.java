package com.fedex.smartpost.utilities.evs.thread;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.fedex.smartpost.utilities.jms.creator.JmsMessageCreator;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.jms.core.JmsTemplate;

public class PublishThread extends Thread {
	private static final Logger logger = LogManager.getLogger(PublishThread.class);
	private int threadNumber;
	private final BlockingQueue<List<Message>> messageStringQueue;
	private JmsTemplate publisher;
	private boolean justLog;
	private AtomicLong packageCount = new AtomicLong();

	public PublishThread(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue, JmsTemplate publisher, boolean justLog) {
		this.publisher = publisher;
		this.threadNumber = threadNumber;
		this.messageStringQueue = messageStringQueue;
		this.justLog = justLog;
	}

	@Override
	public void run() {
		while (true) {
			List<Message> list = null;

			try {
				list = messageStringQueue.take();
			}
			catch (InterruptedException e) {
				logger.info("Thread " + threadNumber + " has been interrupted.", e);
			}
			if (list == null || list.isEmpty()) {
				logger.info(String.format("Total messages published from Thread %d: %d", threadNumber, packageCount.get()));
				logger.info(String.format("Thread %d shutting down.", threadNumber));
				return;
			}
			for (Message message : list) {
				Properties properties = new Properties();
				properties.put("MsgCreateTmstp", new DateTime().toString());
				properties.put("MsgSource", "fxsp-evs-process-postage");
				properties.put("MsgVsn", "1.0");
				properties.put("EventType", "PROCESSPOSTAGE");
				properties.put("ParcelId", message.getPackageId());
				properties.put("ReleaseTypeCode", "U");
				if (justLog) {
					logger.info(message.getPayload());
				}
				else {
					publisher.send(new JmsMessageCreator(message.getPayload(), properties));
				}

				long printedValue = packageCount.incrementAndGet();
				if (printedValue % 1000 == 0) {
					logger.info(String.format("Messages published from Thread %d: %d", threadNumber, printedValue));
				}
			}
		}
	}
}
