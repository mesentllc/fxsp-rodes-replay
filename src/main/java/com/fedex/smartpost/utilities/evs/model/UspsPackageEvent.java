package com.fedex.smartpost.utilities.evs.model;

import java.math.BigDecimal;
import java.util.Date;

public class UspsPackageEvent {
	private String pkgId;
	private String eventCd;
	private Date eventDt;
	private String scanFacZip;
	private String scanFacName;
	private String eventName;
	private String destCntryCode;
	private String destZipCd4;
	private String mailerId;
	private String clientMailerId;
	private String destZip;
	private String mailerName;
	private String recipientName;
	private BigDecimal evsManifestSeq;
	private String custRefNbr;
	private String chanlAppId;

	public String getPkgId() {
		return pkgId;
	}

	public void setPkgId(String pkgId) {
		this.pkgId = pkgId;
	}

	public String getEventCd() {
		return eventCd;
	}

	public void setEventCd(String eventCd) {
		this.eventCd = eventCd;
	}

	public Date getEventDt() {
		return eventDt;
	}

	public void setEventDt(Date eventDt) {
		this.eventDt = eventDt;
	}

	public String getScanFacZip() {
		return scanFacZip;
	}

	public void setScanFacZip(String scanFacZip) {
		this.scanFacZip = scanFacZip;
	}

	public String getScanFacName() {
		return scanFacName;
	}

	public void setScanFacName(String scanFacName) {
		this.scanFacName = scanFacName;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getDestCntryCode() {
		return destCntryCode;
	}

	public void setDestCntryCode(String destCntryCode) {
		this.destCntryCode = destCntryCode;
	}

	public String getDestZipCd4() {
		return destZipCd4;
	}

	public void setDestZipCd4(String destZipCd4) {
		this.destZipCd4 = destZipCd4;
	}

	public String getMailerId() {
		return mailerId;
	}

	public void setMailerId(String mailerId) {
		this.mailerId = mailerId;
	}

	public String getClientMailerId() {
		return clientMailerId;
	}

	public void setClientMailerId(String clientMailerId) {
		this.clientMailerId = clientMailerId;
	}

	public String getDestZip() {
		return destZip;
	}

	public void setDestZip(String destZip) {
		this.destZip = destZip;
	}

	public String getMailerName() {
		return mailerName;
	}

	public void setMailerName(String mailerName) {
		this.mailerName = mailerName;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public BigDecimal getEvsManifestSeq() {
		return evsManifestSeq;
	}

	public void setEvsManifestSeq(BigDecimal evsManifestSeq) {
		this.evsManifestSeq = evsManifestSeq;
	}

	public String getCustRefNbr() {
		return custRefNbr;
	}

	public void setCustRefNbr(String custRefNbr) {
		this.custRefNbr = custRefNbr;
	}

	public String getChanlAppId() {
		return chanlAppId;
	}

	public void setChanlAppId(String chanlAppId) {
		this.chanlAppId = chanlAppId;
	}
}
