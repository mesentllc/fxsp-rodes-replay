package com.fedex.smartpost.utilities.rodes.model;

import com.fedex.smartpost.common.business.FxspPackageFactory;

import java.util.Date;

public class Message {
    private String payload;
    private String packageId;
	private Date scanDate;
	private Long upn;

    public Message(Long upn, Date scanDate, String packageId, String payload) {
		this.upn = upn;
		this.scanDate = scanDate;
        this.packageId = packageId;
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isReturnPackage() {
		return FxspPackageFactory.createFromTrackingId(packageId).isReturns();
    }

    public String getPackageId() {
        return packageId;
    }

	public Date getScanDate() {
		return scanDate;
	}

	public Long getUpn() {
		return upn;
	}
}