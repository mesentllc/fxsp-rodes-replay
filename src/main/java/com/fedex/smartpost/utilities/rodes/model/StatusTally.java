package com.fedex.smartpost.utilities.rodes.model;

public class StatusTally {
	private String accountNbr;
	private int newStatus;
	private int ignoredStatus;
	private int validStatus;
	private int invalidStatus;
	private int usedStatus;

	public int getNewStatus() {
		return newStatus;
	}

	public void incNewStatus() {
		newStatus++;
	}

	public int getIgnoredStatus() {
		return ignoredStatus;
	}

	public void incIgnoredStatus() {
		ignoredStatus++;
	}

	public int getValidStatus() {
		return validStatus;
	}

	public void incValidStatus() {
		validStatus++;
	}

	public int getInvalidStatus() {
		return invalidStatus;
	}

	public void incInvalidStatus() {
		invalidStatus++;
	}

	public int getUsedStatus() {
		return usedStatus;
	}

	public void incUsedStatus() {
		usedStatus++;
	}

	public String getAccountNbr() {
		return accountNbr;
	}

	public void setAccountNbr(String accountNbr) {
		this.accountNbr = accountNbr;
	}
}
