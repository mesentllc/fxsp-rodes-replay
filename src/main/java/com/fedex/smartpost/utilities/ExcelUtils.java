package com.fedex.smartpost.utilities;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fedex.smartpost.utilities.evs.model.EDWDataRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtils {
	private String[] columnNames;

	public List<EDWDataRecord> readHadoopDataFile(String filename) throws IOException, ParseException, InvalidFormatException {
		List<EDWDataRecord> edwDataRecords = new ArrayList<>();
		Workbook wb = WorkbookFactory.create(new File(filename));
		Sheet sheet = wb.getSheetAt(0);

		for (Row row : sheet) {
			if (columnNames == null) {
				columnNames = getColumnName(row);
			}
			else {
				edwDataRecords.add(getRowData(row));
			}
		}
		return edwDataRecords;
	}

	private EDWDataRecord getRowData(Row row) throws ParseException {
		EDWDataRecord record = new EDWDataRecord();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		for (Cell cell : row) {
			String cellValue = getStringValue(cell);
			int ptr = cell.getColumnIndex();
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
		}
		return record;
	}

	private String getStringValue(Cell cell) {
		String returnValue = null;
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				returnValue = cell.getRichStringCellValue().getString();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					returnValue = cell.getDateCellValue().toString();
				}
				else {
					returnValue = String.valueOf(cell.getNumericCellValue());
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				returnValue = String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				returnValue = cell.getCellFormula();
		}
		return returnValue;
	}

	private String[] getColumnName(Row row) {
		List<String> headerList = new ArrayList<>();
		for (Cell cell : row) {
			headerList.add(cell.getRichStringCellValue().getString());
		}
		return headerList.toArray(new String[headerList.size()]);
	}
}
