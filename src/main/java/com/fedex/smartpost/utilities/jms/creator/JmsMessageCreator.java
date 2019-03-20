package com.fedex.smartpost.utilities.jms.creator;

import org.springframework.jms.core.MessageCreator;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Enumeration;
import java.util.Properties;

public class JmsMessageCreator implements MessageCreator {
	private String message;
	private Properties messageProperties;

	public JmsMessageCreator(String message, Properties messageProperties) {
		this.message = message;
		this.messageProperties = messageProperties;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		BytesMessage bytesMessage = session.createBytesMessage();

		bytesMessage.writeBytes(message.getBytes());
		Enumeration<Object> enumeration = messageProperties.keys();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			bytesMessage.setStringProperty(key, (String)messageProperties.get(key));
		}
		return bytesMessage;
	}
}
