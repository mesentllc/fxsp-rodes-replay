package com.fedex.smartpost.utilities.rodes.factory;

import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.rodes.thread.PublishThread;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface PublisherThreadFactory extends ApplicationContextAware {
	PublishThread createBean(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue, boolean justLog);
	PublishThread createOCBean(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue, boolean justLog);
	PublishThread createPDBean(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue, boolean justLog);
}
