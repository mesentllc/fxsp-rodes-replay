package com.fedex.smartpost.utilities.evs.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class Package {
	private String pkgId;
	private BigDecimal evsManifestSeq;
	private BigDecimal errorCd;
	private String mailClass;
	private BigDecimal actlWgt;
	private String destZip;
	private String cntryCode;
	private String prcsCtg;
	private String destRateInd;
	private String rateInd;
	private String zone;
	private String barcodeCd;
	private String prodCd;
	private BigDecimal articleValue;
	private BigDecimal length;
	private BigDecimal width;
	private BigDecimal height;
	private BigDecimal dimWgt;
	private String clientMailerId;
	private String custRefNbr;
	private String entryFacZip;
	private Date trlrCloseDt;
	private String hubId;
	private String sentEvsFlag;
	private Date curDtTmstamp;
	private String ovszFlag;
	private String blnFlag;
	private String dcFlag;
	private String codFlag;
	private String errMsg;
	private String fieldErrMsg;
	private String errLineNbr;
	private String userLoadNbr;
	private String evsReleaseTypeCd;
	private String tripSeq;
	private String chanlAppId;
	private String serviceTypeCd;
	private String entryFacTypeCd;
	private String fastRsvntNbr;
	private Date fastScheduledDt;
	private String containerId;
	private String containerTypeCd;
	private Timestamp mailDateTmstp;
	private String supressPstgFlag;
	private String supressPstgRsnCd;
	private String mainElecFileNbr;
	private String rateSheetLabelTxt;
	private String zipListLabelTxt;
	private String allocateHubId;
	private String paymentAcctNbr;
	private Date ratineRlsRcvdDt;

	public String getPkgId() {
		return pkgId;
	}

	public void setPkgId(String pkgId) {
		this.pkgId = pkgId;
	}

	public BigDecimal getEvsManifestSeq() {
		return evsManifestSeq;
	}

	public void setEvsManifestSeq(BigDecimal evsManifestSeq) {
		this.evsManifestSeq = evsManifestSeq;
	}

	public BigDecimal getErrorCd() {
		return errorCd;
	}

	public void setErrorCd(BigDecimal errorCd) {
		this.errorCd = errorCd;
	}

	public String getMailClass() {
		return mailClass;
	}

	public void setMailClass(String mailClass) {
		this.mailClass = mailClass;
	}

	public BigDecimal getActlWgt() {
		return actlWgt;
	}

	public void setActlWgt(BigDecimal actlWgt) {
		this.actlWgt = actlWgt;
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

	public String getPrcsCtg() {
		return prcsCtg;
	}

	public void setPrcsCtg(String prcsCtg) {
		this.prcsCtg = prcsCtg;
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

	public String getBarcodeCd() {
		return barcodeCd;
	}

	public void setBarcodeCd(String barcodeCd) {
		this.barcodeCd = barcodeCd;
	}

	public String getProdCd() {
		return prodCd;
	}

	public void setProdCd(String prodCd) {
		this.prodCd = prodCd;
	}

	public BigDecimal getArticleValue() {
		return articleValue;
	}

	public void setArticleValue(BigDecimal articleValue) {
		this.articleValue = articleValue;
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

	public BigDecimal getDimWgt() {
		return dimWgt;
	}

	public void setDimWgt(BigDecimal dimWgt) {
		this.dimWgt = dimWgt;
	}

	public String getClientMailerId() {
		return clientMailerId;
	}

	public void setClientMailerId(String clientMailerId) {
		this.clientMailerId = clientMailerId;
	}

	public String getCustRefNbr() {
		return custRefNbr;
	}

	public void setCustRefNbr(String custRefNbr) {
		this.custRefNbr = custRefNbr;
	}

	public String getEntryFacZip() {
		return entryFacZip;
	}

	public void setEntryFacZip(String entryFacZip) {
		this.entryFacZip = entryFacZip;
	}

	public Date getTrlrCloseDt() {
		return trlrCloseDt;
	}

	public void setTrlrCloseDt(Date trlrCloseDt) {
		this.trlrCloseDt = trlrCloseDt;
	}

	public String getHubId() {
		return hubId;
	}

	public void setHubId(String hubId) {
		this.hubId = hubId;
	}

	public String getSentEvsFlag() {
		return sentEvsFlag;
	}

	public void setSentEvsFlag(String sentEvsFlag) {
		this.sentEvsFlag = sentEvsFlag;
	}

	public Date getCurDtTmstamp() {
		return curDtTmstamp;
	}

	public void setCurDtTmstamp(Date curDtTmstamp) {
		this.curDtTmstamp = curDtTmstamp;
	}

	public String getOvszFlag() {
		return ovszFlag;
	}

	public void setOvszFlag(String ovszFlag) {
		this.ovszFlag = ovszFlag;
	}

	public String getBlnFlag() {
		return blnFlag;
	}

	public void setBlnFlag(String blnFlag) {
		this.blnFlag = blnFlag;
	}

	public String getDcFlag() {
		return dcFlag;
	}

	public void setDcFlag(String dcFlag) {
		this.dcFlag = dcFlag;
	}

	public String getCodFlag() {
		return codFlag;
	}

	public void setCodFlag(String codFlag) {
		this.codFlag = codFlag;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getFieldErrMsg() {
		return fieldErrMsg;
	}

	public void setFieldErrMsg(String fieldErrMsg) {
		this.fieldErrMsg = fieldErrMsg;
	}

	public String getErrLineNbr() {
		return errLineNbr;
	}

	public void setErrLineNbr(String errLineNbr) {
		this.errLineNbr = errLineNbr;
	}

	public String getUserLoadNbr() {
		return userLoadNbr;
	}

	public void setUserLoadNbr(String userLoadNbr) {
		this.userLoadNbr = userLoadNbr;
	}

	public String getEvsReleaseTypeCd() {
		return evsReleaseTypeCd;
	}

	public void setEvsReleaseTypeCd(String evsReleaseTypeCd) {
		this.evsReleaseTypeCd = evsReleaseTypeCd;
	}

	public String getTripSeq() {
		return tripSeq;
	}

	public void setTripSeq(String tripSeq) {
		this.tripSeq = tripSeq;
	}

	public String getChanlAppId() {
		return chanlAppId;
	}

	public void setChanlAppId(String chanlAppId) {
		this.chanlAppId = chanlAppId;
	}

	public String getServiceTypeCd() {
		return serviceTypeCd;
	}

	public void setServiceTypeCd(String serviceTypeCd) {
		this.serviceTypeCd = serviceTypeCd;
	}

	public String getEntryFacTypeCd() {
		return entryFacTypeCd;
	}

	public void setEntryFacTypeCd(String entryFacTypeCd) {
		this.entryFacTypeCd = entryFacTypeCd;
	}

	public String getFastRsvntNbr() {
		return fastRsvntNbr;
	}

	public void setFastRsvntNbr(String fastRsvntNbr) {
		this.fastRsvntNbr = fastRsvntNbr;
	}

	public Date getFastScheduledDt() {
		return fastScheduledDt;
	}

	public void setFastScheduledDt(Date fastScheduledDt) {
		this.fastScheduledDt = fastScheduledDt;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getContainerTypeCd() {
		return containerTypeCd;
	}

	public void setContainerTypeCd(String containerTypeCd) {
		this.containerTypeCd = containerTypeCd;
	}

	public Timestamp getMailDateTmstp() {
		return mailDateTmstp;
	}

	public void setMailDateTmstp(Timestamp mailDateTmstp) {
		this.mailDateTmstp = mailDateTmstp;
	}

	public String getSupressPstgFlag() {
		return supressPstgFlag;
	}

	public void setSupressPstgFlag(String supressPstgFlag) {
		this.supressPstgFlag = supressPstgFlag;
	}

	public String getSupressPstgRsnCd() {
		return supressPstgRsnCd;
	}

	public void setSupressPstgRsnCd(String supressPstgRsnCd) {
		this.supressPstgRsnCd = supressPstgRsnCd;
	}

	public String getMainElecFileNbr() {
		return mainElecFileNbr;
	}

	public void setMainElecFileNbr(String mailElecFileNbr) {
		this.mainElecFileNbr = mailElecFileNbr;
	}

	public String getRateSheetLabelTxt() {
		return rateSheetLabelTxt;
	}

	public void setRateSheetLabelTxt(String rateSheetLabelTxt) {
		this.rateSheetLabelTxt = rateSheetLabelTxt;
	}

	public String getZipListLabelTxt() {
		return zipListLabelTxt;
	}

	public void setZipListLabelTxt(String zipListLabelTxt) {
		this.zipListLabelTxt = zipListLabelTxt;
	}

	public String getAllocateHubId() {
		return allocateHubId;
	}

	public void setAllocateHubId(String allocateHubId) {
		this.allocateHubId = allocateHubId;
	}

	public String getPaymentAcctNbr() {
		return paymentAcctNbr;
	}

	public void setPaymentAcctNbr(String paymentAcctNbr) {
		this.paymentAcctNbr = paymentAcctNbr;
	}

	public Date getRatineRlsRcvdDt() {
		return ratineRlsRcvdDt;
	}

	public void setRatineRlsRcvdDt(Date ratineRlsRcvdDt) {
		this.ratineRlsRcvdDt = ratineRlsRcvdDt;
	}
}
