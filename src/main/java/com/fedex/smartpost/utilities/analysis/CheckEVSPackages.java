package com.fedex.smartpost.utilities.analysis;

import com.fedex.smartpost.utilities.MiscUtil;
import com.fedex.smartpost.utilities.edw.dao.EDWDao;
import com.fedex.smartpost.utilities.evs.dao.PackageDao;
import com.fedex.smartpost.utilities.evs.model.Package;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
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
	private PackageDao packageDao;
	private EDWDao edwDao;

	private CheckEVSPackages() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dbOnly.xml");
		packageDao = (PackageDao)context.getBean("evsPackageDao");
		edwDao = (EDWDao)context.getBean("edwDao");
	}

	private void process(String filename) {
		List<String> packageIds = MiscUtil.runThroughBusinessCommon(MiscUtil.retreivePackageIdRecordsFromFile(filename));
		List<Package> packageList = packageDao.retrievePackages(packageIds);
		printMap(packageList);
		dumpEfns(getEfns(packageList));
		checkEDW(packageIds, getEfns(packageList));
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
			throw new RuntimeException("Unable to walk down " + pathToWalk);
		}
		efns.removeAll(found);
		log.info(efns.size() + " EFNs missing from archive.");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("/Support/2019-11-12/missingEfn.txt"))) {
			for (String efn : efns) {
				bw.write(efn + "\n");
			}
		}
		catch (IOException e) {
			log.error("Unable to write efn file.", e);
		}
	}

	private void checkEDW(List<String> packageIds, Set<String> efns) {
		Map<String, List<String>> efnMap = edwDao.retrieveManifests(packageIds);
		dumpManifests(efnMap);
		for (String efn : efnMap.keySet()) {
			efns.remove(efn);
		}
		log.info(efns.size() + " EFNs missing from EDW.");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("/Support/2019-11-12/missingEfn.txt", true))) {
			for (String efn : efns) {
				bw.write(efn + "\n");
			}
		}
		catch (IOException e) {
			log.error("Unable to write efn file.", e);
		}
	}

	private void dumpManifests(Map<String, List<String>> efnMap) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("/Support/2020-03-16-SPEEDS/pkgId_Efn.txt", false))) {
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
		checkEVSPackages.process("/Support/2020-03-16-SPEEDS/NotInSPEEDS.txt");
	}
}
