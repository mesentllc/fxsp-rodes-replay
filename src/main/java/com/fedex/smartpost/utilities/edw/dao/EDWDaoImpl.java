package com.fedex.smartpost.utilities.edw.dao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import static com.fedex.smartpost.utilities.MiscUtil.getXmlDate;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import com.fedex.smartpost.common.types.MailClass;
import com.fedex.smartpost.common.types.MailSubClass;
import com.fedex.smartpost.common.types.MeasurementSource;
import com.fedex.smartpost.common.types.ParcelSize;
import com.fedex.smartpost.common.types.ProcessingCategory;
import com.fedex.smartpost.common.types.Shipment;
import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.evs.model.EDWDataRecord;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Instance;
import com.fedex.smartpost.utilities.rodes.model.Message;
import com.teradata.jdbc.TeraDriver;

public class EDWDaoImpl implements EDWDao {
	private static final Logger logger = LogManager.getLogger(EDWDao.class);
	private static final String CREATE_VOLATILE_TABLE =
        "create volatile table packages (pkg_barcd_nbr varchar(30)) on commit preserve rows";
	private static final String CREATE_VOLATILE_UPN_TABLE =
			"create volatile table upnTable (unvsl_pkg_nbr decimal(19,0)) on commit preserve rows";
    private static final String DROP_VOLATILE_TABLE = "drop table packages";
	private static final String DROP_VOLATILE_UPN_TABLE = "drop table upnTable";

	private DataSource dataSource;

	private static String SortScanBase =
        "<Shipment><Package><PackageId>02901001082007022425</PackageId><UsPostal>"
        + "<MailClass>B</MailClass><MailSubClass>M</MailSubClass></UsPostal>"
        + "<ContainerId>WP548100019244</ContainerId><DestinationSortCode>75212</DestinationSortCode>"
        + "<SortationInformation><SortEvent><SortDateTime>2009-09-27T16:30:38-04:00</SortDateTime>"
        + "</SortEvent><Location><HubId>5431</HubId></Location></SortationInformation><Dimensions>"
        + "<Weight>16</Weight><Height>16</Height><Width>16</Width><Length>16</Length><WeightSource>A"
        + "</WeightSource><DimensionSource>A</DimensionSource></Dimensions><UsPostal><MailClass>B</MailClass>"
        + "<MailSubClass>B</MailSubClass><ParcelSize>B</ParcelSize><ProcessingCategory>M</ProcessingCategory>"
        + "</UsPostal></Package></Shipment>";
    private static final JAXBContext context = initContext();

	public EDWDaoImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private static JAXBContext initContext() {
        try {
            return JAXBContext.newInstance("com.fedex.smartpost.common.types", Shipment.class.getClassLoader());
        }
		catch (JAXBException e) {
            logger.info("JAXBException from EDWDao: " + e.getMessage());
            return null;
        }
    }

    private void populateShipment(Shipment shipment, String packageId, String mailClass, String sizeCategory,
                                  String processingCategory, String containerId, String destinationSortCode,
                                  Timestamp sortDate, Integer hubId, BigDecimal weight, BigDecimal length,
                                  BigDecimal width, BigDecimal height, String weightSource, String dimensionSource,
								  boolean isImpb) {
        ParcelSize parcelSize;
        if ((mailClass == null) || (mailClass.length() != 2)) {
            mailClass = "BP";
        }
        if ("B".equals(sizeCategory)) {
            parcelSize = ParcelSize.B;
        }
		else {
            if ("O".equals(sizeCategory)) {
                parcelSize = ParcelSize.O;
            }
			else {
                parcelSize = ParcelSize.N;
            }
        }
        shipment.getPackage().setPackageId(packageId);
        if (isImpb) {
            shipment.getPackage().setApplicationId("92");
        }
		else {
            shipment.getPackage().setApplicationId("91");
        }
        shipment.getPackage().getUsPostal().setMailClass(MailClass.valueOf(mailClass.substring(0, 1)));
        shipment.getPackage().getUsPostal().setMailSubClass(MailSubClass.valueOf(mailClass.substring(1, 2)));
        shipment.getPackage().getUsPostal().setParcelSize(parcelSize);
        shipment.getPackage().getUsPostal().setProcessingCategory(ProcessingCategory.valueOf(processingCategory));
		if (containerId == null) {
			shipment.getPackage().setContainerId("WP000000000000");
		}
		else {
			shipment.getPackage().setContainerId(containerId);
		}
        shipment.getPackage().setDestinationSortCode(destinationSortCode);
        shipment.getPackage().getSortationInformation().getSortEvent().setSortDateTime(getXmlDate(sortDate));
        shipment.getPackage().getSortationInformation().getLocation().setHubId(hubId);
		if (weight != null) {
			shipment.getPackage().getDimensions().setWeight(weight);
		}
		else {
			shipment.getPackage().getDimensions().setWeight(BigDecimal.ZERO);
		}
		if (length != null) {
			shipment.getPackage().getDimensions().setLength(length);
		}
		else {
			shipment.getPackage().getDimensions().setLength(BigDecimal.ZERO);
		}
		if (width != null) {
			shipment.getPackage().getDimensions().setWidth(width);
		}
		else {
			shipment.getPackage().getDimensions().setWidth(BigDecimal.ZERO);
		}
		if (height != null) {
        	shipment.getPackage().getDimensions().setHeight(height);
		}
		else {
			shipment.getPackage().getDimensions().setHeight(BigDecimal.ZERO);
		}
        shipment.getPackage().getDimensions().setWeightSource(MeasurementSource.valueOf(weightSource));
        shipment.getPackage().getDimensions().setDimensionSource(MeasurementSource.valueOf(dimensionSource));
    }

    private static Shipment getStarterShipment() throws Exception {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader messageStringReader = new StringReader(SortScanBase);
        Shipment shipment = (Shipment) unmarshaller.unmarshal(messageStringReader);
        return shipment;
    }

    private String encodeObject(Shipment shipment) throws Exception {
        StringWriter sw = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(shipment, new StreamResult(sw));
        return sw.toString();
    }

	private String dumpToFile(EDWResults edwResults, boolean createFile) throws IOException {
		String filename = null;
		BufferedWriter bw;
		Calendar dtNow = Calendar.getInstance();
		SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMddHHmmss");

		if (createFile) {
			String timeStamp = dtf.format(dtNow.getTime());
			filename = "/Message_Dump-" + timeStamp;
			bw = new BufferedWriter(new FileWriter(filename));
			for (Date date : edwResults.getScanDates()) {
				for (Message message : edwResults.getMessages(date)) {
					MiscUtil.writeMessageFormat(bw, message);
				}
			}
			bw.close();
		}
		return filename;
	}

	@Override
	public EDWResults retrievePackageIds(List<String> packageList, String sql) {
		EDWResults edwResults = new EDWResults();
		FxspPackage fxspPackage;
		Connection conn;
		Statement stmt;
		ResultSet rs;
		Shipment tempShipment;
		int packageCount = 0;
		int batchCount = 0;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			tempShipment = getStarterShipment();
			final Shipment shipment = tempShipment;
			stmt.execute(CREATE_VOLATILE_TABLE);
			PreparedStatement ps = conn.prepareStatement("insert into packages (?)");
			for (String item : packageList) {
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setString(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String payload;
				String packageId = rs.getString("pkg_barcd_nbr");
				try {
					fxspPackage = FxspPackageFactory.createFromTrackingId(packageId);
					populateShipment(shipment, packageId,
									 rs.getString("mail_class_cd") + rs.getString("mail_sub_class_cd"),
									 rs.getString("prcs_size_cd"), rs.getString("prcs_ctgy_cd"), rs.getString("cntnr_nm"),
									 rs.getString("dest_sort_cd"), rs.getTimestamp("mindate"), rs.getInt("hub_cd"),
									 rs.getBigDecimal("pkg_wgt"), rs.getBigDecimal("pkg_lth_qty"),
									 rs.getBigDecimal("pkg_width_qty"), rs.getBigDecimal("pkg_hgt_qty"),
									 rs.getString("wgt_src_cd"), rs.getString("wgt_src_cd"), fxspPackage.isImpb());
					payload = encodeObject(shipment);
					if ((payload != null) && (payload.length() > 0)) {
						edwResults.addMessage(rs.getTimestamp("mindate"), new Message(rs.getLong("unvsl_pkg_nbr"), rs.getTimestamp("mindate"),
																					  packageId, payload));
						if ((++packageCount % 100) == 0) {
							logger.debug("Processed " + packageCount + " records.");
						}
					}
				}
				catch (Exception e) {
					logger.error("Error with package Id: " + packageId + " - " + e.getMessage());
				}
			}
			logger.debug("Processed " + packageCount + " records total.");
			rs.close();
			stmt.execute(DROP_VOLATILE_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return edwResults;
	}

	@Override
	public EDWResults retrievePackageIdsViaUPN(List<Long> packageList, String sql, boolean createRecFile) {
		EDWResults edwResults = new EDWResults();
		FxspPackage fxspPackage;
		Connection conn;
		Statement stmt;
		ResultSet rs;
		Shipment tempShipment;
		int packageCount = 0;
		int batchCount = 0;
		PrintStream ps2 = null;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			tempShipment = getStarterShipment();
			final Shipment shipment = tempShipment;
			stmt.execute(CREATE_VOLATILE_UPN_TABLE);
			PreparedStatement ps = conn.prepareStatement("insert into upnTable (?)");
			for (Long item : packageList) {
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setLong(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(sql);
			if (createRecFile) {
				try {
					ps2 = new PrintStream("/Record_Dump.txt");
					ps2.print("package id|mail class|mail sub class|B/O/N|M/I|container|dest sort code|scan date|hub code");
					ps2.println("|weight|length|width|height|wgt source code");
				}
				catch (IOException ioe) {

				}
			}
			while (rs.next()) {
				if (createRecFile) {
					ps2.print(rs.getString("pkg_barcd_nbr") + "|" + rs.getString("mail_class_cd") + "|" + rs.getString("mail_sub_class_cd") + "|");
					ps2.print(rs.getString("prcs_size_cd") + "|" + rs.getString("prcs_ctgy_cd") +"|" + rs.getString("cntnr_nm") + "|");
					ps2.print(rs.getString("dest_sort_cd") + "|" + rs.getTimestamp("mindate") + "|" + rs.getInt("hub_cd") + "|");
					ps2.print(rs.getBigDecimal("pkg_wgt") + "|" + rs.getBigDecimal("pkg_lth_qty") + "|" + rs.getBigDecimal("pkg_width_qty") + "|");
					ps2.println(rs.getBigDecimal("pkg_hgt_qty") + "|" + rs.getString("wgt_src_cd"));
				}
				String payload;
				String packageId = rs.getString("pkg_barcd_nbr");
				try {
					fxspPackage = FxspPackageFactory.createFromTrackingId(packageId);
					populateShipment(shipment, packageId,
									 rs.getString("mail_class_cd") + rs.getString("mail_sub_class_cd"),
									 rs.getString("prcs_size_cd"), rs.getString("prcs_ctgy_cd"), rs.getString("cntnr_nm"),
									 rs.getString("dest_sort_cd"), rs.getTimestamp("mindate"), rs.getInt("hub_cd"),
									 rs.getBigDecimal("pkg_wgt"), rs.getBigDecimal("pkg_lth_qty"),
									 rs.getBigDecimal("pkg_width_qty"), rs.getBigDecimal("pkg_hgt_qty"),
									 rs.getString("wgt_src_cd"), rs.getString("wgt_src_cd"), fxspPackage.isImpb());
					payload = encodeObject(shipment);
					if ((payload != null) && (payload.length() > 0)) {
						edwResults.addMessage(rs.getTimestamp("mindate"), new Message(rs.getLong("unvsl_pkg_nbr"), rs.getTimestamp("mindate"),
																					  packageId, payload));
						if ((++packageCount % 100) == 0) {
							logger.debug("Processed " + packageCount + " records.");
						}
					}
				}
				catch (Exception e) {
					logger.error("Error with package Id: " + packageId + " - (UPN: " +
								 rs.getLong("unvsl_pkg_nbr") + ") " + e.getMessage());
				}
			}
			if (createRecFile) {
				ps2.close();
			}
			logger.debug("Processed " + packageCount + " records total.");
			rs.close();
			stmt.execute(DROP_VOLATILE_UPN_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return edwResults;
	}

	@Override
	public EDWResults retrieveMessagesViaSQL(String sql) {
		EDWResults edwResults = new EDWResults();
		FxspPackage fxspPackage;
		Connection conn;
		Statement stmt;
		ResultSet rs;
		Shipment tempShipment;
		int packageCount = 0;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			tempShipment = getStarterShipment();
			final Shipment shipment = tempShipment;
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String payload;
				String packageId = rs.getString("pkg_barcd_nbr");
				try {
					fxspPackage = FxspPackageFactory.createFromTrackingId(packageId);
					populateShipment(shipment, packageId,
									 rs.getString("mail_class_cd") + rs.getString("mail_sub_class_cd"),
									 rs.getString("prcs_size_cd"), rs.getString("prcs_ctgy_cd"), rs.getString("cntnr_nm"),
									 rs.getString("dest_sort_cd"), rs.getTimestamp("mindate"), rs.getInt("hub_cd"),
									 rs.getBigDecimal("pkg_wgt"), rs.getBigDecimal("pkg_lth_qty"),
									 rs.getBigDecimal("pkg_width_qty"), rs.getBigDecimal("pkg_hgt_qty"),
									 rs.getString("wgt_src_cd"), rs.getString("wgt_src_cd"), fxspPackage.isImpb());
					payload = encodeObject(shipment);
					if ((payload != null) && (payload.length() > 0)) {
						edwResults.addMessage(rs.getTimestamp("mindate"), new Message(rs.getLong("unvsl_pkg_nbr"), rs.getTimestamp("mindate"),
																					  packageId, payload));
						if ((++packageCount % 100) == 0) {
							logger.debug("Processed " + packageCount + " records.");
						}
					}
				}
				catch (Exception e) {
					logger.error("Error with package Id: " + packageId + " - (UPN: " +
								 rs.getLong("unvsl_pkg_nbr") + ") " + e.getMessage());
				}
			}
			logger.debug("Processed " + packageCount + " records total.");
			rs.close();
			stmt.execute(DROP_VOLATILE_UPN_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return edwResults;
	}

	@Override
	public EDWResults retrieveMessagesForReplayWOC(List<String> packageIds) {
		return retrievePackageIds(packageIds, GET_PACKAGES_FOR_REPLAY_W_OC_WEIGHT);
	}

	@Override
	@PreDestroy
	public void close() throws SQLException {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.close();
	}

	@Override
	public EDWResults retrieveOCByPackageIds(List<String> packageList) {
		EDWResults edwResults = new EDWResults();
		FxspPackage fxspPackage;
		Connection conn;
		Statement stmt;
		ResultSet rs;
		Shipment tempShipment;
		int packageCount = 0;
		int batchCount = 0;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			tempShipment = getStarterShipment();
			final Shipment shipment = tempShipment;
			stmt.execute(CREATE_VOLATILE_TABLE);
			PreparedStatement ps = conn.prepareStatement("insert into PACKAGES (?)");
			for (String item : packageList) {
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setString(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(GET_ORDER_CREATES_BY_PACKAGES);
			while (rs.next()) {
				String payload;
				String packageId = rs.getString("pkg_barcd_nbr");
				try {
					fxspPackage = FxspPackageFactory.createFromTrackingId(packageId);
					populateShipment(shipment, packageId,
									 rs.getString("mail_class_cd") + rs.getString("mail_sub_class_cd"),
									 rs.getString("prcs_size_cd"), rs.getString("prcs_ctgy_cd"), rs.getString("cntnr_nm"),
									 rs.getString("dest_sort_cd"), rs.getTimestamp("mindate"), rs.getInt("hub_cd"),
									 rs.getBigDecimal("pkg_wgt"), rs.getBigDecimal("pkg_lth_qty"),
									 rs.getBigDecimal("pkg_width_qty"), rs.getBigDecimal("pkg_hgt_qty"),
									 rs.getString("wgt_src_cd"), rs.getString("wgt_src_cd"), fxspPackage.isImpb());
					payload = encodeObject(shipment);
					if ((payload != null) && (payload.length() > 0)) {
						edwResults.addMessage(rs.getTimestamp("mindate"), new Message(rs.getLong("unvsl_pkg_nbr"), rs.getTimestamp("mindate"),
																					  packageId, payload));
						if ((++packageCount % 100) == 0) {
							logger.debug("Processed " + packageCount + " records.");
						}
					}
				}
				catch (Exception e) {
					logger.error("Error with package Id: " + packageId + " - (UPN: " +
								 rs.getLong("unvsl_pkg_nbr") + ") " + e.getMessage());
				}
			}
			logger.debug("Processed " + packageCount + " records total.");
			rs.close();
			stmt.execute(DROP_VOLATILE_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return edwResults;
	}

	@Override
	public Map<String, String> retrieveSHAREInformation(Map<Long, String> shareMap) {
		Set<String> shareSet = shareMap.keySet().stream().map(shareMap::get).collect(Collectors.toSet());
		return retrieveSHAREInformation(shareSet);
	}

	@Override
	public Map<String, String> retrieveSHAREInformation(Set<String> shareSet) {
		Map<String, String> xRefMap = new HashMap<>();
		Connection conn;
		Statement stmt;
		ResultSet rs;
		int batchCount = 0;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			stmt.execute("create volatile table sharetable (share_id varchar(25)) on commit preserve rows");
			PreparedStatement ps = conn.prepareStatement("insert into sharetable (?)");
			for (String item : shareSet) {
				if (item == null || "null".equals(item)) {
					continue;
				}
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setString(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(GET_POSTAL_CODE_BY_SHARE);
			while (rs.next()) {
				xRefMap.put(rs.getString("SHARE_ID_NBR").trim(), rs.getString("pstl_cd"));
			}
			rs.close();
			stmt.execute("drop table sharetable");
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return xRefMap;
	}

	@Override
	public List<EDWDataRecord> retrieveEDWOCByPackageIds(List<String> packageList) {
		logger.info("Records to be processed: " + packageList.size());
		List<EDWDataRecord> edwDataRecords = new ArrayList<>();
		FxspPackage fxspPackage;
		Connection conn;
		Statement stmt;
		ResultSet rs;
		int packageCount = 0;
		int batchCount = 0;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			stmt.execute(CREATE_VOLATILE_TABLE);
			PreparedStatement ps = conn.prepareStatement("insert into PACKAGES (?)");
			for (String item : packageList) {
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setString(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(GET_EDW_ORDER_CREATE_BY_PACKAGES);
			while (rs.next()) {
				String packageId = rs.getString("pkg_barcd_nbr");
				try {
					fxspPackage = FxspPackageFactory.createFromTrackingId(packageId);
					EDWDataRecord edwDataRecord = new EDWDataRecord();
					edwDataRecord.setUpn(rs.getLong("unvsl_pkg_nbr"));
					edwDataRecord.setPackageId(packageId);
//					edwDataRecord.setContainerId(rs.getString("cntnr_nm"));
//					edwDataRecord.setDestinationSortCode(rs.getString("dest_sort_cd"));
//					edwDataRecord.setHeight(rs.getBigDecimal("pkg_hgt_qty"));
//					edwDataRecord.setWeight(rs.getBigDecimal("pkg_wgt"));
//					edwDataRecord.setWidth(rs.getBigDecimal("pkg_width_qty"));
//					edwDataRecord.setLength(rs.getBigDecimal("pkg_lth_qty"));
//					edwDataRecord.setHubId(rs.getInt("hub_cd"));
					edwDataRecord.setSortDate(rs.getTimestamp("mindate"));
					edwDataRecord.setProcessingCategory(rs.getString("prcs_ctgy_cd"));
					edwDataRecord.setSizeCategory(rs.getString("prcs_size_cd"));
					edwDataRecord.setMailClass(fxspPackage.getFxspMailClass());
					edwDataRecord.setWeightSource(rs.getString("wgt_src_cd"));
					edwDataRecord.setDimensionSource(rs.getString("wgt_src_cd"));
					edwDataRecord.setImpb(fxspPackage.isImpb());
					edwDataRecords.add(edwDataRecord);
					packageCount++;
				}
				catch (Exception e) {
					logger.error("Error with package Id: " + packageId + " - (UPN: " +
								 rs.getLong("unvsl_pkg_nbr") + ") " + e.getMessage());
				}
			}
			logger.info("Retrieved " + packageCount + " records from EDW.");
			rs.close();
			stmt.execute(DROP_VOLATILE_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return edwDataRecords;
	}

	@Override
	public EDWResults retrieveEDWResultOCByPackageIds(List<String> packageList) {
		logger.info("Records to be processed: " + packageList.size());
		EDWResults edwResults = new EDWResults();
		FxspPackage fxspPackage;
		Connection conn;
		Statement stmt;
		ResultSet rs;
		int packageCount = 0;
		int batchCount = 0;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			stmt.execute(CREATE_VOLATILE_TABLE);
			PreparedStatement ps = conn.prepareStatement("insert into PACKAGES (?)");
			for (String item : packageList) {
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setString(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(GET_EDW_ORDER_CREATE_BY_PACKAGES);
			while (rs.next()) {
				final Shipment shipment = getStarterShipment();
				String payload;
				String packageId = rs.getString("pkg_barcd_nbr");
				try {
					fxspPackage = FxspPackageFactory.createFromTrackingId(packageId);
					populateShipment(shipment, packageId,
									 rs.getString("mail_class_cd") + rs.getString("mail_sub_class_cd"),
									 rs.getString("prcs_size_cd"), rs.getString("prcs_ctgy_cd"), rs.getString("cntnr_nm"),
									 rs.getString("dest_sort_cd"), rs.getTimestamp("mindate"), rs.getInt("hub_cd"),
									 rs.getBigDecimal("pkg_wgt"), rs.getBigDecimal("pkg_lth_qty"),
									 rs.getBigDecimal("pkg_width_qty"), rs.getBigDecimal("pkg_hgt_qty"),
									 rs.getString("wgt_src_cd"), rs.getString("wgt_src_cd"), fxspPackage.isImpb());
					payload = encodeObject(shipment);
					if ((payload != null) && (payload.length() > 0)) {
						edwResults.addMessage(rs.getTimestamp("mindate"), new Message(rs.getLong("unvsl_pkg_nbr"), rs.getTimestamp("mindate"),
																					  packageId, payload));
						if ((++packageCount % 100) == 0) {
							logger.debug("Processed " + packageCount + " records.");
						}
					}
				}
				catch (Exception e) {
					logger.error("Error with package Id: " + packageId + " - (UPN: " +
								 rs.getLong("unvsl_pkg_nbr") + ") " + e.getMessage());
				}
			}
			logger.info("Retrieved " + packageCount + " records from EDW.");
			rs.close();
			stmt.execute(DROP_VOLATILE_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return edwResults;
	}

	@Override
	public EDWResults retrieveUnreleasedPackageIdsAndUPNs(List<String> packageList) {
		EDWResults edwResults = new EDWResults();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int packageCount = 0;
		int batchCount = 0;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			stmt.execute(CREATE_VOLATILE_TABLE);
			PreparedStatement ps = conn.prepareStatement("insert into packages (?)");
			for (String item : packageList) {
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setString(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(SELECT_UNRELEASED_PACKAGES_FROM_RRR);
			while (rs.next()) {
				if (StringUtils.isNotEmpty(rs.getString("mindate"))) {
					edwResults.addMessage(rs.getTimestamp("mindate"), new Message(rs.getLong("unvsl_pkg_nbr"), rs.getTimestamp("mindate"),
					                                                              rs.getString("pkg_barcd_nbr"), null));
					packageCount++;
				}
			}
			logger.info("Number of UNRELEASED package ids using PACKAGE_FACT: " + packageCount);
			rs.close();
			stmt.execute(DROP_VOLATILE_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e1) {
				logger.info("Exception: ", e1);
			}
		}
		return edwResults;
	}

	@Override
	public List<Instance> retrieveReleasedPackages(List<String> packageList) {
		List<Instance> instances = new ArrayList<>();
		Connection conn;
		Statement stmt;
		ResultSet rs;
		int packageCount = 0;
		int batchCount = 0;

		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			stmt.execute(CREATE_VOLATILE_TABLE);
			PreparedStatement ps = conn.prepareStatement("insert into packages (?)");
			for (String item : packageList) {
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setString(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(SELECT_RELEASED_PACKAGES_FROM_RRR);
			while (rs.next()) {
				Instance instance = new Instance();
				instance.setPackageId(rs.getString("pstl_barcd_nbr"));
				instance.setUniversalPackageId(rs.getLong("unvsl_pkg_nbr"));
				instance.setOriginScanDate(rs.getTimestamp("mindate"));
				instances.add(instance);
				packageCount++;
			}
			logger.info("Number of UNRELEASED package ids using PACKAGE_FACT: " + packageCount);
			rs.close();
			stmt.execute(DROP_VOLATILE_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return instances;
	}

	@Override
	public List<String> getReleasedPackages(Set<String> packageList) throws SQLException {
		List<String> released = new ArrayList<>();
		Connection conn;
		Statement stmt;
		ResultSet rs;
		int packageCount = 0;
		int batchCount = 0;

		DriverManager.registerDriver(new TeraDriver());
		conn = dataSource.getConnection();
		stmt = conn.createStatement();
		stmt.execute(CREATE_VOLATILE_TABLE);
		PreparedStatement ps = conn.prepareStatement("insert into packages (?)");
		for (String item : packageList) {
			if ((++batchCount % 5000) == 0) {
				ps.executeBatch();
			}
			ps.setString(1, item);
			ps.addBatch();
		}
		ps.executeBatch();
		ps.close();
		rs = stmt.executeQuery(GET_RELEASED_PACKAGES_FROM_RRR);
		while (rs.next()) {
			released.add(rs.getString("pkg_barcd_nbr"));
			packageCount++;
		}
		logger.info("Number of UNRELEASED package ids using RODES_RATING_RELEASE: " + packageCount);
		rs.close();
		stmt.execute(DROP_VOLATILE_TABLE);
		stmt.close();
		conn.close();
		return released;
	}

	@Override
	public Set<Long> retrieveUnreleasedUPNs(Set<Long> upnSet) {
		Set<Long> unreleasedSet = new TreeSet<>();
		Connection conn;
		Statement stmt;
		ResultSet rs;
		int packageCount = 0;
		int batchCount = 0;

		if (upnSet == null || upnSet.size() == 0) {
			return unreleasedSet;
		}
		try {
			DriverManager.registerDriver(new TeraDriver());
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			stmt.execute(CREATE_VOLATILE_UPN_TABLE);
			PreparedStatement ps = conn.prepareStatement("insert into upnTable (?)");
			for (Long item : upnSet) {
				if ((++batchCount % 5000) == 0) {
					ps.executeBatch();
				}
				ps.setLong(1, item);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			rs = stmt.executeQuery(SELECT_UNRELEASED_UPN_FROM_RRR);
			while (rs.next()) {
				unreleasedSet.add(rs.getLong("unvsl_pkg_nbr"));
				packageCount++;
			}
			logger.debug("Processed " + packageCount + " records total.");
			rs.close();
			stmt.execute(DROP_VOLATILE_UPN_TABLE);
			stmt.close();
			conn.close();
		}
		catch (Exception e) {
			logger.error("Setup Error: " + e.getMessage());
		}
		return unreleasedSet;
	}

	@Override
	public String buildFileUsingPackageIdsAndSPEEDS(List<String> packageList, boolean createFile) throws IOException {
		return dumpToFile(retrievePackageIds(packageList, SELECT_PACKAGES_FROM_EDW), createFile);
    }

	@Override
	public String buildFileUsingPackageIdsAndPackageFact(List<String> packageList, boolean createFile) throws IOException {
		return dumpToFile(retrievePackageIds(packageList, SELECT_PACKAGES_FROM_PF), createFile);
	}

	@Override
	public String buildFileUsingUnreleasedPackageIds(List<String> packageList, boolean createFile) throws IOException {
		return dumpToFile(retrieveUnreleasedPackageIdsAndUPNs(packageList), createFile);
	}

	@Override
	public String buildFileUsingUPNsAndPackageFact(List<Long> upnList, boolean createFile) throws IOException {
		return dumpToFile(retrievePackageIdsViaUPN(upnList, SELECT_PACKAGES_FROM_PF_USING_UPN, true), createFile);
	}
}
