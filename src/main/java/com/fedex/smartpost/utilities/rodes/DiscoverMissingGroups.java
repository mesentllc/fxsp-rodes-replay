package com.fedex.smartpost.utilities.rodes;

import com.fedex.smartpost.utilities.MiscUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

// EDW SQL to Implement: SELECT DISTINCT blng_grp_nbr FROM SMARTPOST_EDS_PROD_VIEW_DB.fxsp_rodes_rating_release WHERE CAST ((rtng_relse_gmt_tmstp - INTERVAL '10' HOUR) AS DATE) =  '2015-11-13'
// RODeS SQL to Implement: select BILLING_GROUP_ID from SPRODS_SCHEMA.RAC_XFER_BILL_GRP_XREF where RAC_XFER_CTRL_SEQ = 14497;
public class DiscoverMissingGroups {
	private static final Log logger = LogFactory.getLog(DiscoverMissingGroups.class);

	private void process(String rodesFile, String edwFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("/missingBgs.txt"));
		List<String> rodesBGs = MiscUtil.retreivePackageIdRecordsFromFile(rodesFile);
		List<String> edwBGs = MiscUtil.retreivePackageIdRecordsFromFile(edwFile);
		rodesBGs.removeAll(edwBGs);
		logger.info("Total records that are missing: " + rodesBGs.size());
		for (String record : rodesBGs) {
			bw.write(record + "\r\n");
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			args = new String[2];
			args[0] = "/fromRodes.txt";
			args[1] = "/fromEdw.txt";
		}
		DiscoverMissingGroups discoverMissingGroups = new DiscoverMissingGroups();
		discoverMissingGroups.process(args[0], args[1]);
	}
}
