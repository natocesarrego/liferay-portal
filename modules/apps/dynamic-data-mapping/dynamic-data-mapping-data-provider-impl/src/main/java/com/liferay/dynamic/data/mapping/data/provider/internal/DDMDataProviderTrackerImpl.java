/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.data.provider.internal;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderTracker;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.Set;

/**
 * @author Marcellus Tavares
 * @author Gabriel Albuquerque
 */
@Component(immediate = true, service = DDMDataProviderTracker.class)
public class DDMDataProviderTrackerImpl implements DDMDataProviderTracker{

	public DDMDataProvider getDDMDataProvider(String type) {
		return _ddmDataProviderTypeTrackerMap.getService(type);
	}

	public DDMDataProvider getDDMDataProviderByInstanceId(String instanceId) {
		return _ddmDataProviderInstanceIdTrackerMap.getService(instanceId);
	}

	public Set<String> getDDMDataProviderTypes() {
		return _ddmDataProviderTypeTrackerMap.keySet();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_ddmDataProviderInstanceIdTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DDMDataProvider.class,
				"ddm.data.provider.instance.id");

		_ddmDataProviderTypeTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DDMDataProvider.class, "ddm.data.provider.type");
	}

	@Deactivate
	protected void deactivate() {
		_ddmDataProviderInstanceIdTrackerMap.close();

		_ddmDataProviderTypeTrackerMap.close();
	}

	private ServiceTrackerMap<String, DDMDataProvider>
		_ddmDataProviderInstanceIdTrackerMap;
	private ServiceTrackerMap<String, DDMDataProvider>
		_ddmDataProviderTypeTrackerMap;

}
