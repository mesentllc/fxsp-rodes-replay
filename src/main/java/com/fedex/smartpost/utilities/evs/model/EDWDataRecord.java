package com.fedex.smartpost.utilities.evs.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class EDWDataRecord {
	private Long upn;
	private String packageId;
	private String mailClass;
	private String sizeCategory;
	private String processingCategory;
	private String containerId;
	private String recipentPostalCode;
	private String destinationSortCode;
	private String senderPostalCode;
	private Timestamp sortDate;
	private Integer hubId;
	private BigDecimal weight;
	private BigDecimal length;
	private BigDecimal width;
	private BigDecimal height;
	private String weightSource;
	private String dimensionSource;
	private boolean isImpb;
	private String shareId;

	public Long getUpn() {
		return upn;
	}

	public void setUpn(Long upn) {
		this.upn = upn;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getMailClass() {
		return mailClass;
	}

	public void setMailClass(String mailClass) {
		this.mailClass = mailClass;
	}

	public String getSizeCategory() {
		return sizeCategory;
	}

	public void setSizeCategory(String sizeCategory) {
		this.sizeCategory = sizeCategory;
	}

	public String getProcessingCategory() {
		return processingCategory;
	}

	public void setProcessingCategory(String processingCategory) {
		this.processingCategory = processingCategory;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getRecipentPostalCode() {
		return recipentPostalCode;
	}

	public void setRecipentPostalCode(String recipentPostalCode) {
		this.recipentPostalCode = recipentPostalCode;
	}

	public String getDestinationSortCode() {
		return destinationSortCode;
	}

	public void setDestinationSortCode(String destinationSortCode) {
		this.destinationSortCode = destinationSortCode;
	}

	public Timestamp getSortDate() {
		return sortDate;
	}

	public void setSortDate(Timestamp sortDate) {
		this.sortDate = sortDate;
	}

	public Integer getHubId() {
		return hubId;
	}

	public void setHubId(Integer hubId) {
		this.hubId = hubId;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
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

	public String getWeightSource() {
		return weightSource;
	}

	public void setWeightSource(String weightSource) {
		this.weightSource = weightSource;
	}

	public String getDimensionSource() {
		return dimensionSource;
	}

	public void setDimensionSource(String dimensionSource) {
		this.dimensionSource = dimensionSource;
	}

	public boolean isImpb() {
		return isImpb;
	}

	public void setImpb(boolean impb) {
		isImpb = impb;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getSenderPostalCode() {
		return senderPostalCode;
	}

	public void setSenderPostalCode(String senderPostalCode) {
		this.senderPostalCode = senderPostalCode;
	}

}
