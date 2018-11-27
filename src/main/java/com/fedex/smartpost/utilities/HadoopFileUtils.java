package com.fedex.smartpost.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.fedex.smartpost.utilities.evs.model.EDWDataRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class HadoopFileUtils {
	private String[] columnNames;

	public Map<Long, EDWDataRecord>  readHadoopDataFile(String filename) throws IOException, ParseException, InvalidFormatException {
		Map<Long, EDWDataRecord> edwDataRecordMap = new HashMap<>();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		while (br.ready()) {
			String line = br.readLine().trim();
			if (columnNames == null) {
				columnNames = line.split(",");
			}
			else {
				EDWDataRecord record = getRowData(line);
				edwDataRecordMap.put(record.getUpn(), record);
			}
		}
		br.close();
		return edwDataRecordMap;
	}

	private EDWDataRecord getRowData(String line) throws ParseException {
		EDWDataRecord record = new EDWDataRecord();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		line = line.replaceAll("\\\".*,.*\\\"", "Value Removed");
		String[] cells = line.split(",");
		int ptr = 0;

		for (String cellValue : cells) {
			if ("DEST_SORT_CD".equals(columnNames[ptr])) {
				record.setDestinationSortCode(cellValue);
			}
			if ("DIM_SRC_CD".equals(columnNames[ptr])) {
				record.setDimensionSource(cellValue);
			}
			if ("dt_EVENT_TZ_TMSTP".equals(columnNames[ptr])) {
				record.setSortDate(new Timestamp(sdf.parse(cellValue).getTime()));
			}
			if ("HUB_CD".equals(columnNames[ptr])) {
				record.setHubId(Integer.parseInt(cellValue));
			}
			if ("MAIL_CLASS_CD".equals(columnNames[ptr])) {
				if (record.getMailClass() != null) {
					record.setMailClass(cellValue + record.getMailClass());
				}
				else {
					record.setMailClass(cellValue);
				}
			}
			if ("MAIL_SUB_CLASS_CD".equals(columnNames[ptr])) {
				if (record.getMailClass() != null) {
					record.setMailClass(record.getMailClass() + cellValue);
				}
				else {
					record.setMailClass(cellValue);
				}
			}
			if ("UNVSL_PKG_NBR".equals(columnNames[ptr])) {
				record.setUpn(Long.parseLong(cellValue));
			}
			if ("RECP_PSTL_CD".equals(columnNames[ptr])) {
				if (cellValue.length() == 8 || cellValue.length() == 4) {
					cellValue = "0" + cellValue;
				}
				record.setRecipentPostalCode(cellValue);
			}
			if ("PKG_HGT_QTY".equals(columnNames[ptr])) {
				record.setHeight(BigDecimal.valueOf(Double.parseDouble(cellValue)));
			}
			if ("SHPR_ADDR_SHARE_ID_NBR".equals(columnNames[ptr])) {
				record.setShareId(cellValue);
			}
			if ("PKG_LTH_QTY".equals(columnNames[ptr])) {
				record.setLength(BigDecimal.valueOf(Double.parseDouble(cellValue)));
			}
			if ("PKG_WGT".equals(columnNames[ptr])) {
				record.setWeight(BigDecimal.valueOf(Double.parseDouble(cellValue)));
			}
			if ("PKG_WIDTH_QTY".equals(columnNames[ptr])) {
				record.setWidth(BigDecimal.valueOf(Double.parseDouble(cellValue)));
			}
			if ("PRCS_CTGY_CD".equals(columnNames[ptr])) {
				record.setProcessingCategory(cellValue);
			}
			if ("PRCS_SIZE_CD".equals(columnNames[ptr])) {
				record.setSizeCategory(cellValue);
			}
			if ("WGT_SRC_CD".equals(columnNames[ptr])) {
				record.setWeightSource(cellValue);
			}
			if ("PSTL_BARCD_NBR".equals(columnNames[ptr])) {
				record.setPackageId(cellValue);
			}
			ptr++;
		}
		return record;
	}
}
