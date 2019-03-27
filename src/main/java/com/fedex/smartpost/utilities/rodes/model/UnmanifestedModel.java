package com.fedex.smartpost.utilities.rodes.model;

import java.math.BigDecimal;
import java.util.Date;

public class UnmanifestedModel {
	private String pkgId;
	private String chanlAppId;
	private String unmanStat;
	private Long fedexAcctNbr;
	private Date ptsXmtDt;
	private Date scanDt;
	private BigDecimal pstgAmt;
	private String clientMailerId;
	private String clientMailerNm;
	private String scanFacPstlCD;
	private String mailClass;

	public String getPkgId() {
		return pkgId;
	}

	public void setPkgId(String pkgId) {
		this.pkgId = pkgId;
	}

	public String getChanlAppId() {
		return chanlAppId;
	}

	public void setChanlAppId(String chanlAppId) {
		this.chanlAppId = chanlAppId;
	}

	public String getUnmanStat() {
		return unmanStat;
	}

	public void setUnmanStat(String unmanStat) {
		this.unmanStat = unmanStat;
	}

	public Long getFedexAcctNbr() {
		return fedexAcctNbr;
	}

	public void setFedexAcctNbr(Long fedexAcctNbr) {
		this.fedexAcctNbr = fedexAcctNbr;
	}

	public Date getPtsXmtDt() {
		return ptsXmtDt;
	}

	public void setPtsXmtDt(Date ptsXmtDt) {
		this.ptsXmtDt = ptsXmtDt;
	}

	public Date getScanDt() {
		return scanDt;
	}

	public void setScanDt(Date scanDt) {
		this.scanDt = scanDt;
	}

	public BigDecimal getPstgAmt() {
		return pstgAmt;
	}

	public void setPstgAmt(BigDecimal pstgAmt) {
		this.pstgAmt = pstgAmt;
	}

	public String getClientMailerId() {
		return clientMailerId;
	}

	public void setClientMailerId(String clientMailerId) {
		this.clientMailerId = clientMailerId;
	}

	public String getClientMailerNm() {
		return clientMailerNm;
	}

	public void setClientMailerNm(String clientMailerNm) {
		this.clientMailerNm = clientMailerNm;
	}

	public String getScanFacPstlCD() {
		return scanFacPstlCD;
	}

	public void setScanFacPstlCd(String scanFacPstlCD) {
		this.scanFacPstlCD = scanFacPstlCD;
	}

	public String getMailClass() {
		return mailClass;
	}

	public void setMailClass(String mailClass) {
		this.mailClass = mailClass;
	}
}
