package com.fedex.smartpost.utilities.evs.factory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.fedex.smartpost.utilities.evs.thread.PublishThread;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;

public class PublisherThreadFactoryImpl implements PublisherThreadFactory {
	private JmsTemplate publisher;

	@Override
	public PublishThread createBean(int threadNumber, BlockingQueue<List<Message>> messageStringQueue, boolean justLog) {
		return new PublishThread(threadNumber, messageStringQueue, publisher, justLog);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		publisher = (JmsTemplate)applicationContext.getBean("jmsTemplate");
	}
}
