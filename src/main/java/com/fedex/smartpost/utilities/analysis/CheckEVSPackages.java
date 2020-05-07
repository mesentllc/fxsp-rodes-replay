package com.fedex.smartpost.utilities.analysis;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.evs.dao.PackageDao;
import com.fedex.smartpost.utilities.evs.dao.PostageReleaseQueueDao;
import com.fedex.smartpost.utilities.evs.model.Package;
import com.fedex.smartpost.utilities.rodes.dao.DomesticEventGateway;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

public class CheckEVSPackages {
	private static final Log log = LogFactory.getLog(CheckEVSPackages.class);
	private static final String root = "/Support/2020-05-05-BC/";
	private PackageDao packageDao;
	private PostageReleaseQueueDao postageReleaseQueueDao;
	private EDWDao edwDao;
	private com.fedex.smartpost.utilities.transportation.dao.PackageDao transPackageDao;

	private CheckEVSPackages() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		packageDao = (PackageDao)context.getBean("evsPackageDao");
		postageReleaseQueueDao = (PostageReleaseQueueDao)context.getBean("postageReleaseQueueDao");
		edwDao = (EDWDao)context.getBean("edwDao");
		transPackageDao = (com.fedex.smartpost.utilities.transportation.dao.PackageDao)context.getBean("transPackageDao");
	}

	private void process(String filename) {
		Set<String> packageIds = new TreeSet<>(MiscUtil.runThroughBusinessCommon(MiscUtil.retrievePackageIdRecordsFromFile(filename)));
		Set<String> tmpSet = postageReleaseQueueDao.getPackageIds(packageIds);
		dumpIds("foundInPRQ.txt", tmpSet);
		packageIds.removeAll(tmpSet);
		tmpSet = transPackageDao.findPackageWithLC(tmpSet);
		dumpIds("prqIdsWithLC.txt", tmpSet);
		List<Package> packageList = packageDao.retrievePackages(packageIds);
		dumpIds("foundInPackage.txt", getPackageIds(packageList));
		printMap(packageList);
		dumpEfns(getEfns(packageList));
		checkEDW(new ArrayList<>(packageIds), getEfns(packageList));
		packageIds.removeAll(getPackageIds(packageList));
		log.info("Packages not found in system: " + packageIds.size());
		dumpIds("notFound.txt", packageIds);
	}

	private Set<String> getEfns(List<Package> packageList) {
		Set<String> efnSet = new TreeSet<>();
		for (Package pkg : packageList) {
			if (pkg.getMainElecFileNbr() != null) {
				efnSet.add(pkg.getMainElecFileNbr());
			}
		}
		return efnSet;
	}

	private Set<String> getPackageIds(List<Package> packageList) {
		Set<String> packageIds = new TreeSet<>();
		for (Package pkg : packageList) {
//			if (pkg.getMainElecFileNbr() != null) {
				packageIds.add(pkg.getPkgId());
//			}
		}
		return packageIds;
	}

	private void dumpEfns(Set<String> efns) {
		String pathToWalk = "\\\\prodisinas.ground.fedex.com\\fxsp-postal1prd\\PostageManifest\\archive\\";
		Set<String> found = new HashSet<>();
		FileSystem fileSystem = FileSystems.getDefault();
		Path archiveDirectory = fileSystem.getPath(pathToWalk);
		log.info("Attempting to look for " + efns.size() + " EFNs in archive.");
		try (Stream<Path> paths = Files.walk(archiveDirectory)) {
			paths.map(Path::toString).filter(name -> {
					String filename = name.substring(name.lastIndexOf(fileSystem.getSeparator()) + 1);
					for (String mainEfn : efns) {
						if (filename.contains("_" + mainEfn + "_")) {
							return true;
						}
					}
					return false;
				}).forEach(filename -> {
					String[] values = filename.split("_");
					if (values[2].startsWith("2020")) {
						log.info("Found EFN File: " + filename);
						found.add(values[3]);
					}
				});
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to walk down " + pathToWalk, e);
		}
		efns.removeAll(found);
		log.info(efns.size() + " EFNs missing from archive.");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(root + "missingArchiveEfn.txt"))) {
			for (String efn : efns) {
				bw.write(efn + "\n");
			}
		}
		catch (IOException e) {
			log.error("Unable to write efn file.", e);
		}
	}

	private void dumpIds(String filename, Set<String> packageIds) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(root + filename))) {
			for (String pkgId : packageIds) {
				bw.write(pkgId + "\n");
			}
		}
		catch (IOException e) {
			log.error("Unable to write file " + root + filename, e);
		}
	}

	private void checkEDW(List<String> packageIds, Set<String> efns) {
		Map<String, List<String>> efnMap = edwDao.retrieveManifests(packageIds);
		dumpManifests(efnMap);
		for (String efn : efnMap.keySet()) {
			efns.remove(efn);
		}
		log.info(efns.size() + " EFNs missing from EDW.");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(root + "missingEdwEfn.txt", true))) {
			for (String efn : efns) {
				bw.write(efn + "\n");
			}
		}
		catch (IOException e) {
			log.error("Unable to write efn file.", e);
		}
	}

	private void dumpManifests(Map<String, List<String>> efnMap) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(root + "pkgId_Efn.txt", false))) {
			for (String key : efnMap.keySet()) {
				for (String pkgId : efnMap.get(key)) {
					bw.write(key + "," + pkgId + "\n");
				}
			}
		}
		catch (IOException e) {
			log.error("Unable to write manifest/pkg id file.", e);
		}
	}

	private String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyy");
		return (date == null) ? "NULL" : sdf.format(date);
	}

	private void printMap(List<Package> packageList) {
		Map<String, Map<String, List<Package>>> map = new TreeMap<>();

		for (Package pkg : packageList) {
			String key = dateToString(pkg.getMailDateTmstp());
			Map<String, List<Package>> subset = map.computeIfAbsent(key, k -> new HashMap<>());
			List<Package> sublist = subset.computeIfAbsent(pkg.getEvsReleaseTypeCd(), k -> new ArrayList<>());
			sublist.add(pkg);
		}
		for (String key : map.keySet()) {
			log.info("Mail Date: " + key);
			for (String subKey : map.get(key).keySet()) {
				log.info("\t" + map.get(key).get(subKey).size() + " records with a release code of " + subKey);
			}
		}
	}

	public static void main(String[] args) {
		CheckEVSPackages checkEVSPackages = new CheckEVSPackages();
		checkEVSPackages.process(root + "pkgIds.txt");
	}
}
