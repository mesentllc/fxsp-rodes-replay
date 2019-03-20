package com.fedex.smartpost.utilities.evs.factory;

import com.fedex.smartpost.utilities.evs.thread.PublishThread;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface PublisherThreadFactory extends ApplicationContextAware {
	PublishThread createBean(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue, boolean justLog);
}
