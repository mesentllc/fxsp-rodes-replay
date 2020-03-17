package com.fedex.smartpost.utilities.analysis;

import com.fedex.smartpost.utilities.MiscUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class SearchForEFNs {
	private static final Log log = LogFactory.getLog(SearchForEFNs.class);

	public static void main(String[] args) throws IOException {
		SearchForEFNs searchForEFNs = new SearchForEFNs();
		searchForEFNs.process();
	}

	private void process() throws IOException {
		int created = 0;
		int published = 0;
		Set<String> efns = MiscUtil.retreiveEFNsFromFile("/Support/2020-02-11/efns.txt");
		for (int serverNum = 6; serverNum < 10; serverNum++) {
			try (BufferedReader br = new BufferedReader(new FileReader(String.format("/Support/2020-02-11/pje5615%d/web.log", serverNum)))) {
				while (br.ready()) {
					String line = br.readLine();
					for (String efn : efns) {
						if (line.contains(efn)) {
							log.info(line);
							if (line.endsWith("created.")) {
								created++;
							}
							if (line.endsWith("published.")) {
								published++;
							}
						}
					}
				}
			}
		}
		log.info("Number created: " + created);
		log.info("Number published: " + published);
	}
}
