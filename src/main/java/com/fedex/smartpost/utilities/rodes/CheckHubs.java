package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageException;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.rodes.dao.BillingGroupDao;
import com.fedex.smartpost.utilities.rodes.dao.BillingPackageDao;
import com.fedex.smartpost.utilities.rodes.dao.PackageDetailXfer;
import com.fedex.smartpost.utilities.rodes.model.BillingPackage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/* Looking in SPEeDS at the RODeS Release messages, every single package sent with FXSP_ORIG_HUB_CD=5837
   is sending down a blank orig_fxsp_hub_cd. There are 1,237 packages over the past two days. These are
   causing some issues in Rating and the Rating IT team needs to manually update these records. */
public class CheckHubs {
	private static final Log logger = LogFactory.getLog(CheckHubs.class);
	private BillingPackageDao billingPackageDao;
	private BillingGroupDao billingGroupDao;
	private PackageDetailXfer packageDetailXfer;

	public CheckHubs() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		billingPackageDao = (BillingPackageDao)context.getBean("billingPackageDao");
		billingGroupDao = (BillingGroupDao)context.getBean("billingGroupDao");
		packageDetailXfer = (PackageDetailXfer)context.getBean("packageDetailXfer");
	}

	public static void main(String[] args) {
		CheckHubs checkHubs = new CheckHubs();
		checkHubs.process("D:/Support/2018-02-23/failedPackageIds.txt");
	}

	private void process(String filename) {
		List<String> packageIds = runThroughBusinessCommon(MiscUtil.retrievePackageIdRecordsFromFile(filename));
		List<BillingPackage> bps = billingPackageDao.retrieveDups(packageIds);
		logger.info(countEmptyHubs(bps) + " billing packages with blank origin hub codes.");
		logger.info(countEmptyFxspHubs(bps) + " billing packages with blank FXSP origin hub codes.");
		List<Integer> racXferCntlSeqs = billingGroupDao.getRacXferCntlSeqs(packageIds);
		dumpRacXferCtlSeqs(racXferCntlSeqs);
		List<BillingPackage> bpsFromXferList = packageDetailXfer.getReleasedPackages(packageIds, racXferCntlSeqs);
		logger.info(countEmptyHubs(bpsFromXferList) + " billing packages from the Xfer table with blank origin hub codes.");
		logger.info(countEmptyFxspHubs(bpsFromXferList) + " billing packages from the Xfer table with blank FXSP origin hub codes.");
	}

	private void dumpRacXferCtlSeqs(List<Integer> racXferCtrlSeqs) {
		for (Integer racXferCtrlSeq : racXferCtrlSeqs) {
			logger.info("RAC Xfer Control Sequence: " + racXferCtrlSeq);
		}
	}

	private int countEmptyHubs(List<BillingPackage> bps) {
		int counter = 0;
		for (BillingPackage bp : bps) {
			if (StringUtils.isEmpty(bp.getOriginHubCd())) {
				counter++;
			}
		}
		return counter;
	}

	private int countEmptyFxspHubs(List<BillingPackage> bps) {
		int counter = 0;
		for (BillingPackage bp : bps) {
			if (StringUtils.isEmpty(bp.getFxspOriginLocCd())) {
				counter++;
			}
		}
		return counter;
	}

	private List<String> runThroughBusinessCommon(List<String> packageIds) {
		List<String> processedList = new ArrayList<>();

		for (String packageId : packageIds) {
			try {
				FxspPackage fxspPackage = FxspPackageFactory.createFromUnknown(packageId.trim());
				processedList.add(fxspPackage.getUspsBarcode().getPackageIdentificationCode().substring(2));
			}
			catch (FxspPackageException e) {
				logger.debug("Exception found: ", e);
			}
		}
		return processedList;
	}
}
