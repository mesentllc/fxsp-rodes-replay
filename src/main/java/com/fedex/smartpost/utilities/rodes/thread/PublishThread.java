package com.fedex.smartpost.utilities.rodes.thread;

import com.fedex.smartpost.utilities.jms.creator.JmsMessageCreator;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class PublishThread extends Thread {
	private static final Log logger = LogFactory.getLog(PublishThread.class);
	private int threadNumber;
	private final BlockingQueue<List<Message>> messageStringQueue;
	private JmsTemplate domesticPublisher;
	private JmsTemplate returnsPublisher;
	private String eventType;
	private AtomicLong packageCount = new AtomicLong();
	private boolean justLog;

	public PublishThread(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue,
						 JmsTemplate domesticPublisher, JmsTemplate returnsPublisher, String eventType, boolean justLog) {
		this.domesticPublisher = domesticPublisher;
		this.returnsPublisher = returnsPublisher;
		this.threadNumber = threadNumber;
		this.messageStringQueue = messageStringQueue;
		this.eventType = eventType;
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
				properties.setProperty("EventType", eventType);
				if (justLog) {
					logger.info(message.getPayload());
				}
				else {
					if (message.isReturnPackage()) {
						returnsPublisher.send(new JmsMessageCreator(message.getPayload(), properties));
					}
					else {
						domesticPublisher.send(new JmsMessageCreator(message.getPayload(), properties));
					}
				}
				long printedValue = packageCount.incrementAndGet();
				if (printedValue % 1000 == 0) {
					logger.info(String.format("Messages published from Thread %d: %d", threadNumber, printedValue));
				}
			}
		}
	}
}
