package com.fedex.smartpost.utilities.rodes.model;

public class EPDIRecord {
    private String epdiAuditFlag = "Y";
    private String handleFlag    = "N";
    private String billingNbr;
    private String chanlAppId;
    private String custDistCtr;
    private String custFxgHubId;
    private String custMailClassCd;
    private String custMnfstId;
    private Float custPkgHgt;
    private String custPkgId;
    private Float custPkgLength;
    private Float custPkgWgt;
    private Float custPkgWidth;
    private String custSubMailClassCd;
    private String delConReqFlag;
    private String delConfReqdFlagSrc;
    private String expectedFlag;
    private Long fedexCustAcctNbr;
    private String fedexCustAcctNbrSrc;
    private String fedexPkgId;
    private String fxgSvcCdSrc;
    private String labelFmt;
    private String mailerId;
    private String meterNum;
    private String mnfstGrpText;
    private String originHubCd;
    private String originPstlCd;
    private String pkgCategoryCd;
    private String pkgRcvdFlag;
    private String pkgTypeCd;
    private String poNum;
    private String pstlClassCd;
    private String pstlSubClassCd;
    private String recpPstlCd;
    private String returnTyp;
    private String rmaNum;
    private String spBlngSvcCd;
    private String spPkgDimSourceCd;
    private Float spPkgHgt;
    private Float spPkgLength;
    private Float spPkgWgt;
    private String spPkgWgtSourceCd;
    private Float spPkgWidth;
    private String tptyFedexCustAcctNbr;

    public String getFedexPkgId() {
        return fedexPkgId;
    }

    public void setFedexPkgId(String fedexPkgId) {
        this.fedexPkgId = fedexPkgId;
    }

    public Long getFedexCustAcctNbr() {
        return fedexCustAcctNbr;
    }

    public void setFedexCustAcctNbr(Long fedexCustAcctNbr) {
        this.fedexCustAcctNbr = fedexCustAcctNbr;
    }

    public String getHandleFlag() {
        return handleFlag;
    }

    public void setHandleFlag(String handleFlag) {
        this.handleFlag = handleFlag;
    }

    public String getPkgCategoryCd() {
        return pkgCategoryCd;
    }

    public void setPkgCategoryCd(String pkgCategoryCd) {
        this.pkgCategoryCd = pkgCategoryCd;
    }

    public String getPkgTypeCd() {
        return pkgTypeCd;
    }

    public void setPkgTypeCd(String pkgTypeCd) {
        this.pkgTypeCd = pkgTypeCd;
    }

    public String getCustDistCtr() {
        return custDistCtr;
    }

    public void setCustDistCtr(String custDistCtr) {
        this.custDistCtr = custDistCtr;
    }

    public String getCustMnfstId() {
        return custMnfstId;
    }

    public void setCustMnfstId(String custMnfstId) {
        this.custMnfstId = custMnfstId;
    }

    public Float getSpPkgWgt() {
        return spPkgWgt;
    }

    public void setSpPkgWgt(Float spPkgWgt) {
        this.spPkgWgt = spPkgWgt;
    }

    public Float getSpPkgHgt() {
        return spPkgHgt;
    }

    public void setSpPkgHgt(Float spPkgHgt) {
        this.spPkgHgt = spPkgHgt;
    }

    public Float getSpPkgLength() {
        return spPkgLength;
    }

    public void setSpPkgLength(Float spPkgLength) {
        this.spPkgLength = spPkgLength;
    }

    public Float getSpPkgWidth() {
        return spPkgWidth;
    }

    public void setSpPkgWidth(Float spPkgWidth) {
        this.spPkgWidth = spPkgWidth;
    }

    public String getSpPkgWgtSourceCd() {
        return spPkgWgtSourceCd;
    }

    public void setSpPkgWgtSourceCd(String spPkgWgtSourceCd) {
        this.spPkgWgtSourceCd = spPkgWgtSourceCd;
    }

    public String getSpPkgDimSourceCd() {
        return spPkgDimSourceCd;
    }

    public void setSpPkgDimSourceCd(String spPkgDimSourceCd) {
        this.spPkgDimSourceCd = spPkgDimSourceCd;
    }

    public Float getCustPkgWgt() {
        return custPkgWgt;
    }

    public void setCustPkgWgt(Float custPkgWgt) {
        this.custPkgWgt = custPkgWgt;
    }

    public Float getCustPkgHgt() {
        return custPkgHgt;
    }

    public void setCustPkgHgt(Float custPkgHgt) {
        this.custPkgHgt = custPkgHgt;
    }

    public Float getCustPkgLength() {
        return custPkgLength;
    }

    public void setCustPkgLength(Float custPkgLength) {
        this.custPkgLength = custPkgLength;
    }

    public Float getCustPkgWidth() {
        return custPkgWidth;
    }

    public void setCustPkgWidth(Float custPkgWidth) {
        this.custPkgWidth = custPkgWidth;
    }

    public String getPstlClassCd() {
        return pstlClassCd;
    }

    public void setPstlClassCd(String pstlClassCd) {
        this.pstlClassCd = pstlClassCd;
    }

    public String getPstlSubClassCd() {
        return pstlSubClassCd;
    }

    public void setPstlSubClassCd(String pstlSubClassCd) {
        this.pstlSubClassCd = pstlSubClassCd;
    }

    public String getOriginHubCd() {
        return originHubCd;
    }

    public void setOriginHubCd(String originHubCd) {
        this.originHubCd = originHubCd;
    }

    public String getOriginPstlCd() {
        return originPstlCd;
    }

    public void setOriginPstlCd(String originPstlCd) {
        this.originPstlCd = originPstlCd;
    }

    public String getRecpPstlCd() {
        return recpPstlCd;
    }

    public void setRecpPstlCd(String recpPstlCd) {
        this.recpPstlCd = recpPstlCd;
    }

    public String getCustPkgId() {
        return custPkgId;
    }

    public void setCustPkgId(String custPkgId) {
        this.custPkgId = custPkgId;
    }

    public String getSpBlngSvcCd() {
        return spBlngSvcCd;
    }

    public void setSpBlngSvcCd(String spBlngSvcCd) {
        this.spBlngSvcCd = spBlngSvcCd;
    }

    public String getDelConReqFlag() {
        return delConReqFlag;
    }

    public void setDelConReqFlag(String delConReqFlag) {
        this.delConReqFlag = delConReqFlag;
    }

    public String getEpdiAuditFlag() {
        return epdiAuditFlag;
    }

    public void setEpdiAuditFlag(String epdiAuditFlag) {
        this.epdiAuditFlag = epdiAuditFlag;
    }

    public String getExpectedFlag() {
        return expectedFlag;
    }

    public void setExpectedFlag(String expectedFlag) {
        this.expectedFlag = expectedFlag;
    }

    public String getBillingNbr() {
        return billingNbr;
    }

    public void setBillingNbr(String billingNbr) {
        this.billingNbr = billingNbr;
    }

    public String getMnfstGrpText() {
        return mnfstGrpText;
    }

    public void setMnfstGrpText(String mnfstGrpText) {
        this.mnfstGrpText = mnfstGrpText;
    }

    public String getFxgSvcCdSrc() {
        return fxgSvcCdSrc;
    }

    public void setFxgSvcCdSrc(String fxgSvcCdSrc) {
        this.fxgSvcCdSrc = fxgSvcCdSrc;
    }

    public String getChanlAppId() {
        return chanlAppId;
    }

    public void setChanlAppId(String chanlAppId) {
        this.chanlAppId = chanlAppId;
    }

    public String getPoNum() {
        return poNum;
    }

    public void setPoNum(String poNum) {
        this.poNum = poNum;
    }

    public String getMeterNum() {
        return meterNum;
    }

    public void setMeterNum(String meterNum) {
        this.meterNum = meterNum;
    }

    public String getRmaNum() {
        return rmaNum;
    }

    public void setRmaNum(String rmaNum) {
        this.rmaNum = rmaNum;
    }

    public String getTptyFedexCustAcctNbr() {
        return tptyFedexCustAcctNbr;
    }

    public void setTptyFedexCustAcctNbr(String tptyFedexCustAcctNbr) {
        this.tptyFedexCustAcctNbr = tptyFedexCustAcctNbr;
    }

    public String getDelConfReqdFlagSrc() {
        return delConfReqdFlagSrc;
    }

    public void setDelConfReqdFlagSrc(String delConfReqdFlagSrc) {
        this.delConfReqdFlagSrc = delConfReqdFlagSrc;
    }

    public String getFedexCustAcctNbrSrc() {
        return fedexCustAcctNbrSrc;
    }

    public void setFedexCustAcctNbrSrc(String fedexCustAcctNbrSrc) {
        this.fedexCustAcctNbrSrc = fedexCustAcctNbrSrc;
    }

    public String getMailerId() {
        return mailerId;
    }

    public void setMailerId(String mailerId) {
        this.mailerId = mailerId;
    }

    public String getLabelFmt() {
        return labelFmt;
    }

    public void setLabelFmt(String labelFmt) {
        this.labelFmt = labelFmt;
    }

    public String getReturnTyp() {
        return returnTyp;
    }

    public void setReturnTyp(String returnTyp) {
        this.returnTyp = returnTyp;
    }

    public String getCustMailClassCd() {
        return custMailClassCd;
    }

    public void setCustMailClassCd(String custMailClassCd) {
        this.custMailClassCd = custMailClassCd;
    }

    public String getCustSubMailClassCd() {
        return custSubMailClassCd;
    }

    public void setCustSubMailClassCd(String custSubMailClassCd) {
        this.custSubMailClassCd = custSubMailClassCd;
    }

    public String getCustFxgHubId() {
        return custFxgHubId;
    }

    public void setCustFxgHubId(String custFxgHubId) {
        this.custFxgHubId = custFxgHubId;
    }

    public String getPkgRcvdFlag() {
        return pkgRcvdFlag;
    }

    public void setPkgRcvdFlag(String pkgRcvdFlag) {
        this.pkgRcvdFlag = pkgRcvdFlag;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
