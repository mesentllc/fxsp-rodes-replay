package com.fedex.smartpost.utilities.rodes.factory;

import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.rodes.thread.PublishThread;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class PublisherThreadFactoryImpl implements PublisherThreadFactory {
	private JmsTemplate domesticPublisher;
	private JmsTemplate returnsPublisher;
	private JmsTemplate ocPublisher;

	@Override
	public PublishThread createBean(int threadNumber, BlockingQueue<List<Message>> messageStringQueue, boolean justLog) {
		return new PublishThread(threadNumber, messageStringQueue, domesticPublisher, returnsPublisher, "SORTSCAN", justLog);
	}

	@Override
	public PublishThread createOCBean(int threadNumber, BlockingQueue<List<Message>> messageStringQueue, boolean justLog) {
		return new PublishThread(threadNumber, messageStringQueue, ocPublisher, ocPublisher, "ORDERCREATE", justLog);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		domesticPublisher = (JmsTemplate)applicationContext.getBean("domesticPublisher");
		returnsPublisher = (JmsTemplate)applicationContext.getBean("returnsPublisher");
		ocPublisher = (JmsTemplate)applicationContext.getBean("ocPublisher");
	}
}
