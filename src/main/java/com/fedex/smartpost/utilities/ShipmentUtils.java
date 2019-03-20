package com.fedex.smartpost.utilities;

import com.fedex.smartpost.common.types.MailClass;
import com.fedex.smartpost.common.types.MailSubClass;
import com.fedex.smartpost.common.types.MeasurementSource;
import com.fedex.smartpost.common.types.ParcelSize;
import com.fedex.smartpost.common.types.ProcessingCategory;
import com.fedex.smartpost.common.types.Shipment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;

import static com.fedex.smartpost.utilities.MiscUtil.getXmlDate;

public class ShipmentUtils {
	private static final Log logger = LogFactory.getLog(ShipmentUtils.class);

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

	private static JAXBContext initContext() {
		try {
			return JAXBContext.newInstance("com.fedex.smartpost.common.types", Shipment.class.getClassLoader());
		}
		catch (JAXBException e) {
			logger.info("JAXBException from EDWDao: " + e.getMessage());
			return null;
		}
	}

	public Shipment getStarterShipment() throws Exception {
		Unmarshaller unmarshaller = context.createUnmarshaller();
		StringReader messageStringReader = new StringReader(SortScanBase);
		Shipment shipment = (Shipment) unmarshaller.unmarshal(messageStringReader);
		return shipment;
	}

	public String encodeObject(Shipment shipment) throws Exception {
		StringWriter sw = new StringWriter();
		Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(shipment, new StreamResult(sw));
		return sw.toString();
	}

	public void populateShipment(Shipment shipment, String packageId, String mailClass, String sizeCategory,
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
		shipment.getPackage().getDimensions().setWeight(weight);
		shipment.getPackage().getDimensions().setLength(length);
		shipment.getPackage().getDimensions().setWidth(width);
		shipment.getPackage().getDimensions().setHeight(height);
		shipment.getPackage().getDimensions().setWeightSource(MeasurementSource.valueOf(weightSource));
		shipment.getPackage().getDimensions().setDimensionSource(MeasurementSource.valueOf(dimensionSource));
	}
}
