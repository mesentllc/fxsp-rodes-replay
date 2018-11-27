package com.fedex.smartpost.utilities.rodes.model;

import java.util.Date;

public class Instance {
	private Long universalPackageId;
	private String packageId;
	private Date originScanDate;

	public Long getUniversalPackageId() {
		return universalPackageId;
	}

	public void setUniversalPackageId(Long universalPackageId) {
		this.universalPackageId = universalPackageId;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public Date getOriginScanDate() {
		return originScanDate;
	}

	public void setOriginScanDate(Date originScanDate) {
		this.originScanDate = originScanDate;
	}
}
