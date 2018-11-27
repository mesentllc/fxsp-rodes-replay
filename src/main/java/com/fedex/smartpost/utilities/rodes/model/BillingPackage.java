package com.fedex.smartpost.utilities.rodes.model;

import java.util.Date;

public class BillingPackage {
	private String fedexPkgId;
	private String deliveryType;
	private Date deliveryDt;
	private String status;
	private Date statusDt;
	private Date originHubScanDt;
	private Date createdDt;
	private boolean released;
	private Long billingGroup;
	private String fxspOriginLocCd;
	public String originHubCd;

	public String getFedexPkgId() {
		return fedexPkgId;
	}

	public void setFedexPkgId(String fedexPkgId) {
		this.fedexPkgId = fedexPkgId;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public Date getDeliveryDt() {
		return deliveryDt;
	}

	public void setDeliveryDt(Date deliveryDt) {
		this.deliveryDt = deliveryDt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStatusDt() {
		return statusDt;
	}

	public void setStatusDt(Date statusDt) {
		this.statusDt = statusDt;
	}

	public Date getOriginHubScanDt() {
		return originHubScanDt;
	}

	public void setOriginHubScanDt(Date originHubScanDt) {
		this.originHubScanDt = originHubScanDt;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public boolean isReleased() {
		return released;
	}

	public void setReleased(boolean released) {
		this.released = released;
	}

	public Long getBillingGroup() {
		return billingGroup;
	}

	public void setBillingGroup(Long billingGroup) {
		this.billingGroup = billingGroup;
	}

	public String getFxspOriginLocCd() {
		return fxspOriginLocCd;
	}

	public void setFxspOriginLocCd(String fxspOriginLocCd) {
		this.fxspOriginLocCd = fxspOriginLocCd;
	}

	public String getOriginHubCd() {
		return originHubCd;
	}

	public void setOriginHubCd(String originHubCd) {
		this.originHubCd = originHubCd;
	}
}
