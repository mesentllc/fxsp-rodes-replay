package com.fedex.smartpost.utilities.rodes.factory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.context.ApplicationContextAware;

import com.fedex.smartpost.utilities.rodes.model.Message;
import com.fedex.smartpost.utilities.rodes.thread.PublishThread;

public interface PublisherThreadFactory extends ApplicationContextAware {
	PublishThread createBean(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue, boolean justLog);
	PublishThread createOCBean(int threadNumber, BlockingQueue<List<Message>> messageStringQueue, boolean justLog);
}
