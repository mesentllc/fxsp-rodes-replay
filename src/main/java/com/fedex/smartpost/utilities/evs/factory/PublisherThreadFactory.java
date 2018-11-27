package com.fedex.smartpost.utilities.evs.factory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.fedex.smartpost.utilities.evs.thread.PublishThread;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.springframework.context.ApplicationContextAware;

public interface PublisherThreadFactory extends ApplicationContextAware {
	PublishThread createBean(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue, boolean justLog);
}
