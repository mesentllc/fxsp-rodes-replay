package com.fedex.smartpost.utilities.evs.dto;

import java.util.Date;

public class PostageTransactionDto extends AbstractDto {
	private static final long serialVersionUID = 1L;

    private String packageId;
    private Long rateId;
    private String postageType;
    private String zone;
    private Double weight;
    private Double postageAmount;
    private Date curDateTimeStamp;
	private String channelApplicationId;

	public String getPackageId() {
        return packageId;
    }
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    public Long getRateId() {
        return rateId;
    }
    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }
    public String getPostageType() {
        return postageType;
    }
    public void setPostageType(String postageType) {
        this.postageType = postageType;
    }
    public String getZone() {
        return zone;
    }
    public void setZone(String zone) {
        this.zone = zone;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public Double getPostageAmount() {
        return postageAmount;
    }
    public void setPostageAmount(Double postageAmount) {
        this.postageAmount = postageAmount;
    }
    public Date getCurDateTimeStamp() {
        return curDateTimeStamp;
    }
    public void setCurDateTimeStamp(Date curDateTimeStamp) {
        this.curDateTimeStamp = curDateTimeStamp;
    }
    public String getChannelApplicationId() {
		return channelApplicationId;
	}
	public void setChannelApplicationId(String channelApplicationId) {
		this.channelApplicationId = channelApplicationId;
	}

    @Override
    public String toString() {
        return

         "packageId = [" + packageId +
        "] rateId = [" + rateId +
        "] postageType = [" + postageType +
        "] zone = [" + zone +
        "] weight = [" + weight +
        "] postageAmount = [" + postageAmount +
        "] curDateTimeStamp = [" + curDateTimeStamp + "]";
    }
}
