package com.fedex.smartpost.utilities.evs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.OutboundOrdCrtEvntStatDao;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;

public class ReplayCheck {
	private static final Logger logger = Logger.getLogger(ReplayCheck.class);
	private BillingPackageDao billingPackageDao;
	private OutboundOrdCrtEvntStatDao outboundOrdCrtEvntStatDao;

	public ReplayCheck() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		outboundOrdCrtEvntStatDao = (OutboundOrdCrtEvntStatDao)context.getBean("outboundOrdCrtEvntStatDao");
	}

	private void process(String inFile, String outFile) throws IOException, SQLException {
		List<String> packageIds = MiscUtil.retreivePackageIdRecordsFromFile(inFile);
		packageIds.removeAll(extractPackageIds(billingPackageDao.retrieveDups(packageIds)));
		logger.info(packageIds.size() + " package ids missing.");
		MiscUtil.dumpPackageIds(outFile, packageIds);
		List<String> ocPackages = outboundOrdCrtEvntStatDao.retrievePackages(packageIds);
		MiscUtil.dumpPackageIds(outFile.substring(0, outFile.length() - 4) + "-hasOC.txt", ocPackages);
		packageIds.removeAll(ocPackages);
		MiscUtil.dumpPackageIds(outFile.substring(0, outFile.length() - 4) + "-noOC.txt", packageIds);
		billingPackageDao.close();
		outboundOrdCrtEvntStatDao.close();
	}

	private static Collection<String> extractPackageIds(List<BillingPackage> billingPackages) {
		return billingPackages.stream().map(BillingPackage::getFedexPkgId).collect(Collectors.toList());
	}

	// Notes:  For those package ids that have an OC, but is not BP - chances are that the package ids have been released
	// previously, and therefore were not replayed.  Also, there needs to be a OC available for the RODeS applications to
	// make 'TU' events available for creating BPs.
	public static void main(String[] args) throws IOException, SQLException {
		ReplayCheck replayCheck = new ReplayCheck();
		replayCheck.process("/Support/2016.07.29/replayUnmanifested.txt",
							"/Support/2016.07.29/missingPackages.txt");
	}
}
