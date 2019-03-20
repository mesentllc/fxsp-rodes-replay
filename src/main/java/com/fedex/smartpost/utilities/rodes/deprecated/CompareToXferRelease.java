package com.fedex.smartpost.utilities.rodes.deprecated;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.PackageDetailXfer;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import org.apache.commons.collections4.ListUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CompareToXferRelease {
	private PackageDetailXfer packageDetailXfer;

	public CompareToXferRelease() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		packageDetailXfer = (PackageDetailXfer)context.getBean("packageDetailXfer");
	}

	private List<String> packageIdsFromBP(List<BillingPackage> bpList) {
		List<String> packageIds = new ArrayList<>();

		for (BillingPackage bp : bpList) {
			packageIds.add(bp.getFedexPkgId());
		}
		return packageIds;
	}

	private void compareToSnapshot(String filename, String racIds, boolean buildFile) throws ParseException {
		List<BillingPackage> billingPackages = MiscUtil.retreiveBPsFromFile(filename);
		List<Integer> racIdList = new ArrayList<>();
		List existingPackages;
		BufferedWriter bw = null;
		List packageIds = packageIdsFromBP(billingPackages);
		String[] racSplit = racIds.split(",");

		for (String string : racSplit) {
			racIdList.add(Integer.parseInt(string));
		}
		existingPackages = packageDetailXfer.getReleasedPackages(packageIds, racIdList);
		System.out.println("Number of package ids found in PACKAGE_DETAIL_XFER: " + existingPackages.size());
		packageIds = ListUtils.removeAll(packageIds, packageIdsFromBP(existingPackages));
		if (packageIds.isEmpty()) {
			System.out.println("All packages exists in PACKAGE_DETAIL_XFER");
			return;
		}
		try {
			if (buildFile) {
				bw = new BufferedWriter(new FileWriter("/missingFromXfer.txt"));
				for (BillingPackage bp : billingPackages) {
					if (packageIds.contains(bp.getFedexPkgId())) {
						bw.write(bp.getFedexPkgId() + "|" + MiscUtil.SDF.format(bp.getOriginHubScanDt()) + "|" +
								 MiscUtil.SDF.format(bp.getCreatedDt()) + "\r\n");
					}
				}
				bw.close();
			}
		}
		catch (IOException ioe) {
			System.out.println("Can't open file for writing.");
		}
	}


	public static void main(String[] args) throws ParseException {
		// This takes the output from GetBillingPackage as input to check which of the BPs were released.
		// The output of this will contain the package ids, scan date and created date of records to be
		// replayed, since they were purged from the BP table.
		if (args.length != 2) {
			args = new String[2];
//			args[0] = "/rfs319572/rfs319572.txt";
			args[0] = "/existing.txt";
			args[1] = "2886,2887";
		}
		CompareToXferRelease compareToXferRelease = new CompareToXferRelease();
		compareToXferRelease.compareToSnapshot(args[0], args[1], true);
	}
}
