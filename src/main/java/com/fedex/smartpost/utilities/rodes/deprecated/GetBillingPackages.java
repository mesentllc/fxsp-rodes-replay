package com.fedex.smartpost.utilities.rodes.deprecated;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
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

public class GetBillingPackages {
	private BillingPackageDao billingPackageDao;

	public GetBillingPackages() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
	}

	private void getCurrentSnapshot(String filename, boolean buildFile) {
		// This method should be called the day that the blast to RODeS has happened, so that we capture the
		// current state of BILLING_PACKAGE in regards to the packages that were replayed.
		List<String> packageIds = MiscUtil.retrievePackageIdRecordsFromFile(filename);
		List<BillingPackage> existingPackages;
		BufferedWriter bw = null;

		existingPackages = billingPackageDao.retrieveDups(packageIds);
		System.out.println("Number of package ids found in BILLING_PACKAGE: " + existingPackages.size());
		if (existingPackages.size() != packageIds.size()) {
			try {
				if (buildFile) {
					bw = new BufferedWriter(new FileWriter("/existing.txt"));
				}
				for (BillingPackage bp : existingPackages) {
					if (buildFile) {
						bw.write(bp.getFedexPkgId() + "|" + MiscUtil.SDF.format(bp.getOriginHubScanDt()) + "|" +
								 MiscUtil.SDF.format(bp.getCreatedDt()) + "\r\n");
					}
				}
				if (buildFile) {
					bw.close();
				}
			}
			catch (IOException ioe) {
				System.out.println("Can't open file for writing.");
			}
		}
		else {
			System.out.println();
		}
	}

	private void compareToSnapshot(String filename, boolean buildFile) throws ParseException {
		// Basically, this method should be executed a day after the user ran the getCurrentSnapshot method,
		// to see what package ids were purged.
		List<BillingPackage> billingPackages = MiscUtil.retreiveBPsFromFile(filename);
		List<BillingPackage> existingPackages;
		List<String> packageIds = new ArrayList<>();
		BufferedWriter bw = null;

		for (BillingPackage bp : billingPackages) {
			packageIds.add(bp.getFedexPkgId());
		}
		existingPackages = billingPackageDao.retrieveDups(packageIds);
		System.out.println("Number of package ids found in BILLING_PACKAGE: " + existingPackages.size());
		packageIds = ListUtils.removeAll(packageIds, MiscUtil.extractPackageIdsFromBP(existingPackages));
		if (packageIds.isEmpty()) {
			System.out.println("All packages exists in BILLING_PACKAGE");
			return;
		}
		try {
			if (buildFile) {
				bw = new BufferedWriter(new FileWriter("/missingFromSnapshot.txt"));
			}
			for (BillingPackage bp : billingPackages) {
				if (packageIds.contains(bp.getFedexPkgId())) {
					if (buildFile) {
						bw.write(bp.getFedexPkgId() + "|" + MiscUtil.SDF.format(bp.getOriginHubScanDt()) + "|" +
								 MiscUtil.SDF.format(bp.getCreatedDt()) + "\r\n");
					}
				}
			}
			if (buildFile) {
				bw.close();
			}
		}
		catch (IOException ioe) {
			System.out.println("Can't open file for writing.");
		}
	}

	public static void main(String[] args) throws ParseException {
		// This method will extract all the package ids, scan dates and created dates that exist in the BILLING_PACKAGE
		// table for the package ids that are contained in the file.  This was created due to a blast to the RODeS
		// system, that resulted in 71 scan dates, where the business needed to ungroup a bunch of packages to
		// reduce the scan dates to a reasonable level, so they could successfully release.  With the output,
		// the user is able to check if BILLING_PACKAGE records were deleted during the purging process, so that
		// the packages could be replayed when the business is ready for the extra scan dates.
		if (args.length != 1) {
			args = new String[1];
//			args[0] = "/rfs319572/rfs319572.txt";
			args[0] = "/existing.txt";
		}
		GetBillingPackages preReplayProcess = new GetBillingPackages();
		preReplayProcess.compareToSnapshot(args[0], true);
	}
}
