package com.fedex.smartpost.utilities.evs.model;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Date;

public class PostalPackage {
    private String parcelId;
    private String applId;
    private String mailClass;
    private BigDecimal actWeight;
    private String destZip;
    private String cntryCode;
    private String procCatg;
    private String destRateInd;
    private String rateInd;
    private String zone;
    private BigDecimal artclValue;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal dimWeight;
    private String clientMailerId;
    private String customerRefNbr;
    private String facilityEntryZip;
    private String entryFacilityType;
    private String spHubId;
    private String blnOvszFlg;
    private String delConFlg;
    private String codFlg;
    private XMLGregorianCalendar trlrCloseDt;
    private String barcode;
    private String productCode;
    private String releaseTypeCode;
    private String userLoadNumber;
    private Integer tripId;
    private String zipDiscountFlag;
    private UnmanifestedInfo unmanifested;
    private Date postageDate;
    private String containerId;

    @Override
	public String toString() {
    	StringBuilder sb = new StringBuilder();

   		sb.append("Parcel Id: ").append(parcelId).append("\n")
   		  .append("Channel App Id: ").append(applId).append("\n")
   		  .append("Mail Class: ").append(mailClass).append("\n")
   		  .append("Actual Wgt: ").append(actWeight).append("\n")
   		  .append("Dest Zip: ").append(destZip).append("\n")
   		  .append("Cntry Cd: ").append(cntryCode).append("\n")
   		  .append("Proc Ctg: ").append(procCatg).append("\n")
   		  .append("Dest Rate Ind: ").append(destRateInd).append("\n")
   		  .append("Rate Ind: ").append(rateInd).append("\n")
   		  .append("Zone: ").append(zone).append("\n")
   		  .append("Artl Val: ").append(artclValue).append("\n")
   		  .append("Length: ").append(length).append("\n")
   		  .append("Width: ").append(width).append("\n")
   		  .append("Height: ").append(height).append("\n")
   		  .append("Dim Wgt: ").append(dimWeight).append("\n")
   		  .append("Client Mailer Id: ").append(clientMailerId).append("\n")
   		  .append("Customer Ref Nbr: ").append(customerRefNbr).append("\n")
   		  .append("Facility Entry Zip: ").append(facilityEntryZip).append("\n")
   		  .append("Hub Id: ").append(spHubId).append("\n")
   		  .append("Balloon/Ovsz Flg: ").append(blnOvszFlg).append("\n")
   		  .append("Delcon Flg: ").append(delConFlg).append("\n")
   		  .append("COD Flg: ").append(codFlg).append("\n")
   		  .append("Trlr Close Dt: ").append(trlrCloseDt).append("\n")
   		  .append("Barcode: ").append(barcode).append("\n")
   		  .append("Product Code: ").append(productCode).append("\n")
   		  .append("Release Type Code: ").append(releaseTypeCode).append("\n")
   		  .append("User Load Nbr: ").append(userLoadNumber).append("\n")
   		  .append("Trip Id: ").append(tripId).append("\n")
   		  .append("Container Id: ").append(containerId).append("\n");
   		if (unmanifested != null) {
   			sb.append(unmanifested);
   		}
    	return sb.toString();
    }

	public String getParcelId() {
		return parcelId;
	}

	public void setParcelId(String parcelId) {
		this.parcelId = parcelId;
	}

	public void setApplId(String applId) {
		this.applId = applId;
	}

	public String getApplId() {
		return applId;
	}

	public String getMailClass() {
		return mailClass;
	}

	public void setMailClass(String mailClass) {
		this.mailClass = mailClass;
	}

	public BigDecimal getActWeight() {
		return actWeight;
	}

	public void setActWeight(BigDecimal actWeight) {
		this.actWeight = actWeight;
	}

	public String getDestZip() {
		return destZip;
	}

	public void setDestZip(String destZip) {
		this.destZip = destZip;
	}

	public String getCntryCode() {
		return cntryCode;
	}

	public void setCntryCode(String cntryCode) {
		this.cntryCode = cntryCode;
	}

	public String getProcCatg() {
		return procCatg;
	}

	public void setProcCatg(String procCatg) {
		this.procCatg = procCatg;
	}

	public String getDestRateInd() {
		return destRateInd;
	}

	public void setDestRateInd(String destRateInd) {
		this.destRateInd = destRateInd;
	}

	public String getRateInd() {
		return rateInd;
	}

	public void setRateInd(String rateInd) {
		this.rateInd = rateInd;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public BigDecimal getArtclValue() {
		return artclValue;
	}

	public void setArtclValue(BigDecimal artclValue) {
		this.artclValue = artclValue;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public BigDecimal getDimWeight() {
		return dimWeight;
	}

	public void setDimWeight(BigDecimal dimWeight) {
		this.dimWeight = dimWeight;
	}

	public String getClientMailerId() {
		return clientMailerId;
	}

	public void setClientMailerId(String clientMailerId) {
		this.clientMailerId = clientMailerId;
	}

	public String getCustomerRefNbr() {
		return customerRefNbr;
	}

	public void setCustomerRefNbr(String customerRefNbr) {
		this.customerRefNbr = customerRefNbr;
	}

	public String getFacilityEntryZip() {
		return facilityEntryZip;
	}

	public void setFacilityEntryZip(String facilityEntryZip) {
		this.facilityEntryZip = facilityEntryZip;
	}

	public String getEntryFacilityType() {
		return entryFacilityType;
	}

	public void setEntryFacilityType(String entryFacilityType) {
		this.entryFacilityType = entryFacilityType;
	}

	public String getSpHubId() {
		return spHubId;
	}

	public void setSpHubId(String spHubId) {
		this.spHubId = spHubId;
	}

	public String getBlnOvszFlg() {
		return blnOvszFlg;
	}

	public void setBlnOvszFlg(String blnOvszFlg) {
		this.blnOvszFlg = blnOvszFlg;
	}

	public String getDelConFlg() {
		return delConFlg;
	}

	public void setDelConFlg(String delConFlg) {
		this.delConFlg = delConFlg;
	}

	public String getCodFlg() {
		return codFlg;
	}

	public void setCodFlg(String codFlg) {
		this.codFlg = codFlg;
	}

	public XMLGregorianCalendar getTrlrCloseDt() {
		return trlrCloseDt;
	}

	public void setTrlrCloseDt(XMLGregorianCalendar trlrCloseDt) {
		this.trlrCloseDt = trlrCloseDt;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getReleaseTypeCode() {
		return releaseTypeCode;
	}

	public void setReleaseTypeCode(String releaseTypeCode) {
		this.releaseTypeCode = releaseTypeCode;
	}

	public String getUserLoadNumber() {
		return userLoadNumber;
	}

	public void setUserLoadNumber(String userLoadNumber) {
		this.userLoadNumber = userLoadNumber;
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public String getZipDiscountFlag() {
		return zipDiscountFlag;
	}

	public void setZipDiscountFlag(String zipDiscountFlag) {
		this.zipDiscountFlag = zipDiscountFlag;
	}

	public UnmanifestedInfo getUnmanifested() {
		return unmanifested;
	}

	public void setUnmanifested(UnmanifestedInfo unmanifested) {
		this.unmanifested = unmanifested;
	}

	public Date getPostageDate() {
		return postageDate;
	}

	public void setPostageDate(Date postageDate) {
		this.postageDate = postageDate;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}


}
