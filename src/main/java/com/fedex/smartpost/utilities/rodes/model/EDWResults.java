package com.fedex.smartpost.utilities.rodes.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fedex.smartpost.utilities.MiscUtil;

public class EDWResults {
	private Map<Date, List<Message>> messages = new TreeMap<>();

	public Set<Date> getScanDates() {
		return messages.keySet();
	}

	public List<Message> getMessages(Date date) {
		return messages.get(date);
	}

	public void addMessage(Date scanDate, Message message) throws ParseException {
		String normalize = MiscUtil.SDF.format(scanDate);
		Date date = MiscUtil.SDF.parse(normalize);
		if (!messages.containsKey(date)) {
			messages.put(date, new ArrayList<>());
		}
		messages.get(date).add(message);
	}

	public int totalRecords() {
		int total = 0;

		for (Date date : messages.keySet()) {
			total += messages.get(date).size();
		}
		return total;
	}
}
