package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import com.fedex.smartpost.common.types.MailClass;
import com.fedex.smartpost.common.types.MailSubClass;
import com.fedex.smartpost.common.types.MeasurementSource;
import com.fedex.smartpost.common.types.ParcelSize;
import com.fedex.smartpost.common.types.ProcessingCategory;
import com.fedex.smartpost.common.types.Shipment;
import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.fedex.smartpost.utilities.MiscUtil.getXmlDate;

public class ReplayFromFile {
	private static final Log logger = LogFactory.getLog(ReplayFromFile.class);

	private static final JAXBContext context = initContext();

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

	public EDWResults buildFromFile(String filename) throws Exception {
		Shipment shipment = getStarterShipment();
		EDWResults edwResults = new EDWResults();
		long packageCount = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			while (br.ready()) {
				String line = br.readLine();
				String[] parts = line.split(",");
				FxspPackage fxspPackage = FxspPackageFactory.createFromUnknown(parts[0]);
				Date minDate = sdf.parse(parts[6]);
				populateShipment(shipment, parts[0], parts[1] + parts[2], parts[4], parts[3], null, parts[5], new Timestamp(minDate.getTime()),
					Integer.parseInt(parts[7]), BigDecimal.valueOf(Double.parseDouble(parts[8])), BigDecimal.valueOf(Double.parseDouble(parts[9])),
					BigDecimal.valueOf(Double.parseDouble(parts[10])), BigDecimal.valueOf(Double.parseDouble(parts[11])), "C", "C",
					fxspPackage.isImpb());
				String payload = encodeObject(shipment);
				if ((payload != null) && (payload.length() > 0)) {
					edwResults.addMessage(minDate, new Message(packageCount + 1, minDate, parts[0], payload));
					if ((++packageCount % 100) == 0) {
						logger.debug("Processed " + packageCount + " records.");
					}
				}
			}
		}
		return edwResults;
	}

	private static Map<Long, Message> addToMasterRecords(String filename, EDWResults edwResults) throws ParseException, IOException {
		Map<Long, Message> masterMap = MiscUtil.retrieveMasterReplayFileRecords(filename);

		if (edwResults != null) {
			for (Date date : edwResults.getScanDates()) {
				for (Message message : edwResults.getMessages(date)) {
					masterMap.put(message.getUpn(), message);
				}
			}
		}
		return masterMap;
	}

	public static void main(String[] args) throws Exception {
		ReplayFromFile replay = new ReplayFromFile();
		Map<Long, Message> masterMap;
		String recFilename = "/Support/2020-03-09/messages.rec";
		masterMap = addToMasterRecords(recFilename, replay.buildFromFile("/Support/2020-03-09/jacobsReplay.csv"));
		MiscUtil.storeMasterFile(recFilename, masterMap);
	}
}
