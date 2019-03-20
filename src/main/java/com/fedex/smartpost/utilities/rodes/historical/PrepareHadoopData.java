package com.fedex.smartpost.utilities.rodes.historical;

import com.fedex.smartpost.utilities.HadoopFileUtils;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.evs.model.EDWDataRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class PrepareHadoopData {
	private static final Log logger = LogFactory.getLog(PrepareHadoopData.class);
	private EDWDao edwDao;

	public PrepareHadoopData() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	// This will build a file of UPN, SHARE ID, RECP POSTAL CODE...
//	public static void main(String[] args) throws IOException {
//		PrepareHadoopData prepareHadoopData = new PrepareHadoopData();
//		// This file is UPN,SHARE ID - If we wish to extract straight from the Excel - that is an option, or just
//		// extract these two columns and use the extract - If we want to use POI, then some changes are needed.
//		prepareHadoopData.process("/Support/SortVsRated/upn-share.csv");
//	}

	public static void main(String[] args) throws ParseException, InvalidFormatException, IOException {
		HadoopFileUtils hadoopFileUtils = new HadoopFileUtils();
		Map<Long, EDWDataRecord> edwDataRecords = hadoopFileUtils.readHadoopDataFile("/EbayForMichael.csv");
		logger.info("Read " + edwDataRecords.size() + " records.");
	}

	private void process(String filename) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
		Map<Long, String> upnShareMap = buildShareMap(filename);
		Map<String, String> sharePostalMap = edwDao.retrieveSHAREInformation(upnShareMap);

		BufferedWriter bw = new BufferedWriter(new FileWriter("/Support/SortVsRated/upn-share-postal-" + sdf.format(new Date()) + ".csv"));
		for (Long upn : upnShareMap.keySet()) {
			String share = StringUtils.rightPad(upnShareMap.get(upn), 25);
			String postal = sharePostalMap.get(share);
			bw.write(upn + "," + share + ',' + postal + '\n');
		}
		bw.close();
	}

	private static Map<Long, String> buildShareMap(String filename) {
		Map<Long, String> shareMap = new HashMap<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String[] split = br.readLine().trim().split(",");
				if (split.length == 2 && StringUtils.isNumeric(split[0])) {
					shareMap.put(Long.parseLong(split[0]), split[1]);
				}
			}
		}
		catch (IOException e) {
			logger.error(e.getMessage());
		}
		return shareMap;
	}
}
