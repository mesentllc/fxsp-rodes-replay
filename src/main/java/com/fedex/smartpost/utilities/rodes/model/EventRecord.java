package com.fedex.smartpost.utilities.rodes.model;

import java.io.Serializable;
import java.util.Date;

public class EventRecord implements Serializable {
    private static final long serialVersionUID = 9158003673229968951L;
    private String balloonOversizedFlag;    // PKG_TYPE_CD
    private Long billingPackageSeq;
    private String channelAppId;
    private String containerCode;
    private String eventCode;
    private Date eventDate;
    private String eventType;
    private String fedexCustomerAccountNumber;
    private String fedexPackageId;
    private String fxspPackageDimensionSourceCode;
    private Double fxspPackageHeight;
    private Double fxspPackageLength;
    private Double fxspPackageWeight;
    private String fxspPackageWeightSourceCode;
    private Double fxspPackageWidth;
    private String hubCode;
    private String machinable;              // PKG_CATEGORY_CD
    private String mailerId;
    private String packageEventReason;
    private String packageEventStatus;
    private Long packageReturnEventId;
    private String postalCode;

    public String getPackageEventStatus() {
        return packageEventStatus;
    }

    public void setPackageEventStatus(String packageEventStatus) {
        this.packageEventStatus = packageEventStatus;
    }

    public String getPackageEventReason() {
        return packageEventReason;
    }

    public void setPackageEventReason(String packageEventReason) {
        this.packageEventReason = packageEventReason;
    }

    public Long getBillingPackageSeq() {
        return billingPackageSeq;
    }

    public void setBillingPackageSeq(Long billingPackageSeq) {
        this.billingPackageSeq = billingPackageSeq;
    }

    public Long getPackageReturnEventId() {
        return packageReturnEventId;
    }

    public void setPackageReturnEventId(Long packageReturnEventId) {
        this.packageReturnEventId = packageReturnEventId;
    }

    public String getFedexPackageId() {
        return fedexPackageId;
    }

    public void setFedexPackageId(String fedexPackageId) {
        this.fedexPackageId = fedexPackageId;
    }

    public String getChannelAppId() {
        return channelAppId;
    }

    public void setChannelAppId(String channelAppId) {
        this.channelAppId = channelAppId;
    }

    public String getFxspPackageWeightSourceCode() {
        return fxspPackageWeightSourceCode;
    }

    public void setFxspPackageWeightSourceCode(String fxspPackageWeightSourceCode) {
        this.fxspPackageWeightSourceCode = fxspPackageWeightSourceCode;
    }

    public String getFxspPackageDimensionSourceCode() {
        return fxspPackageDimensionSourceCode;
    }

    public void setFxspPackageDimensionSourceCode(String fxspPackageDimensionSourceCode) {
        this.fxspPackageDimensionSourceCode = fxspPackageDimensionSourceCode;
    }

    public Double getFxspPackageLength() {
        return fxspPackageLength;
    }

    public void setFxspPackageLength(Double fxspPackageLength) {
        this.fxspPackageLength = fxspPackageLength;
    }

    public Double getFxspPackageHeight() {
        return fxspPackageHeight;
    }

    public void setFxspPackageHeight(Double fxspPackageHeight) {
        this.fxspPackageHeight = fxspPackageHeight;
    }

    public Double getFxspPackageWidth() {
        return fxspPackageWidth;
    }

    public void setFxspPackageWidth(Double fxspPackageWidth) {
        this.fxspPackageWidth = fxspPackageWidth;
    }

    public Double getFxspPackageWeight() {
        return fxspPackageWeight;
    }

    public void setFxspPackageWeight(Double fxspPackageWeight) {
        this.fxspPackageWeight = fxspPackageWeight;
    }

    public String getMachinable() {
        return machinable;
    }

    public void setMachinable(String machinable) {
        this.machinable = machinable;
    }

    public String getBalloonOversizedFlag() {
        return balloonOversizedFlag;
    }

    public void setBalloonOversizedFlag(String balloonOversizedFlag) {
        this.balloonOversizedFlag = balloonOversizedFlag;
    }

    public String getHubCode() {
        return hubCode;
    }

    public void setHubCode(String hubCode) {
        this.hubCode = hubCode;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getFedexCustomerAccountNumber() {
        return fedexCustomerAccountNumber;
    }

    public void setFedexCustomerAccountNumber(String fedexCustomerAccountNumber) {
        this.fedexCustomerAccountNumber = fedexCustomerAccountNumber;
    }

    public String getMailerId() {
        return mailerId;
    }

    public void setMailerId(String mailerId) {
        this.mailerId = mailerId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
