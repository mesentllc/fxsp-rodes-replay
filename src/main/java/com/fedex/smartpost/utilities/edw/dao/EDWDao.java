package com.fedex.smartpost.utilities.edw.dao;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.utilities.evs.model.EDWDataRecord;
import com.fedex.smartpost.utilities.rodes.model.EDWResults;
import com.fedex.smartpost.utilities.rodes.model.Instance;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EDWDao {
	String SELECT_PACKAGES_FROM_EDW = ClassPathResourceUtil.getString("/dao/edw/selectPackagesFromEDW.sql");
	String SELECT_PACKAGES_FROM_PF = ClassPathResourceUtil.getString("/dao/edw/retrieveFromPackageFact.sql");
	String SELECT_PACKAGES_FROM_PF_USING_UPN = ClassPathResourceUtil.getString("/dao/edw/selectEDWPackagesByUPN.sql");
	String SELECT_UNRELEASED_PACKAGES_FROM_PD = ClassPathResourceUtil.getString("/dao/edw/retrieveNoRoRelPackagesFromPackageDetail.sql");
	String SELECT_RELEASED_PACKAGES_FROM_RRR = ClassPathResourceUtil.getString("/dao/edw/retrieveReleasedPackageIds.sql");
	String SELECT_UNRELEASED_PACKAGES_FROM_RRR = ClassPathResourceUtil.getString("/dao/edw/retrieveNoRoRelPackagesFromRRR.sql");
	String SELECT_UNRELEASED_UPN_FROM_RRR = ClassPathResourceUtil.getString("/dao/edw/retrieveUnlreasedUpns.sql");
	String GET_RELEASED_PACKAGES_FROM_RRR = ClassPathResourceUtil.getString("/dao/edw/retrieveRoRelPackagesFromRRR.sql");
	String GET_ORDER_CREATES_BY_PACKAGES = ClassPathResourceUtil.getString("/dao/edw/retrieveOCByPkgId.sql");
	String GET_EDW_ORDER_CREATE_BY_PACKAGES = ClassPathResourceUtil.getString("/dao/edw/retrieveEDWOCByPkgIds.sql");
	String GET_POSTAL_CODE_BY_SHARE = ClassPathResourceUtil.getString("/dao/edw/retrievePostalCodeBySHARE.sql");
	String GET_PACKAGES_FOR_REPLAY = ClassPathResourceUtil.getString("/dao/edw/straightExtractForReplay.sql");
	String GET_PACKAGES_FOR_REPLAY_W_OC_WEIGHT = ClassPathResourceUtil.getString("/dao/edw/selectEDWPackageWithOCWeight.sql");
	String GET_SHIPMENTS_USING_F_PACKAGE = ClassPathResourceUtil.getString("/dao/edw/selectShipmentsUsingFPackage.sql");
	EDWResults retrievePackageIds(List<String> packageList, String sql);
	EDWResults retrievePackageIdsViaUPN(List<Long> packageList, String sql, boolean createRecFile);
	String buildFileUsingPackageIdsAndSPEEDS(List<String> packageList, boolean createFile) throws IOException;
    String buildFileUsingPackageIdsAndPackageFact(List<String> packageList, boolean createFile) throws IOException;
    String buildFileUsingUnreleasedPackageIds(List<String> packageList, boolean createFile) throws IOException;
    String buildFileUsingUPNsAndPackageFact(List<Long> upnList, boolean createFile) throws IOException;
	EDWResults retrieveUnreleasedPackageIdsAndUPNs(List<String> packageList);
	List<Instance> retrieveReleasedPackages(List<String> packageList);
	Set<Long> retrieveUnreleasedUPNs(Set<Long> upnSet);
	List<String> getReleasedPackages(Set<String> packageList) throws SQLException;
	EDWResults retrieveOCByPackageIds(List<String> packageList);
	List<EDWDataRecord> retrieveEDWOCByPackageIds(List<String> packageList);
	EDWResults retrieveEDWResultOCByPackageIds(List<String> packageList);
	Map<String, String> retrieveSHAREInformation(Map<Long, String> shareMap);
	Map<String, String> retrieveSHAREInformation(Set<String> shareMap);
	EDWResults retrieveMessagesViaSQL(String sql);
	EDWResults retrieveMessagesForReplayWOC(List<String> packageIds);
	void close() throws SQLException;
}
