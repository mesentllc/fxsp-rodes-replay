package com.fedex.smartpost.utilities.rodes.enums;

public enum BillingServiceCode {
	BP(913, "BP"),
	AP(914, "AP"),
	BB(915, "BB"),
	BM(916, "BM"),
	RP(918, "RP");

	private int serviceCode;
	private String mailClass;

	BillingServiceCode(int serviceCode, String mailClass) {
		this.serviceCode = serviceCode;
		this.mailClass = mailClass;
	}

	public static String getMailClass(int serviceCode) {
		for (BillingServiceCode code : BillingServiceCode.values()) {
			if (code.serviceCode == serviceCode) {
				return code.mailClass;
			}
		}
		throw new RuntimeException(serviceCode + " is an invalid code.");
	}
}
