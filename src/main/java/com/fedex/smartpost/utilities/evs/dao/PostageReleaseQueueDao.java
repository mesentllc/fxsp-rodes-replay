package com.fedex.smartpost.utilities.evs.dao;

import java.util.Set;

public interface PostageReleaseQueueDao {
	Set<String> getPackageIds(Set<String> packageIds);
}
