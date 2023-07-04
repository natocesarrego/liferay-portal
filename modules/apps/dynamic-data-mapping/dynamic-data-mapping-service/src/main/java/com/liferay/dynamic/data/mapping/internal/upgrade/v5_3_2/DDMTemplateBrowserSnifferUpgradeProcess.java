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

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_2;

import com.liferay.portal.kernel.upgrade.BaseTemplateUpgradeProcess;

import java.util.regex.Pattern;

/**
 * @author Albert Gomes Cabral
 */
public class DDMTemplateBrowserSnifferUpgradeProcess
	extends BaseTemplateUpgradeProcess {

	@Override
	protected Pattern getTemplatePattern() throws Exception {
		return Pattern.compile(
			"\\w*\\s*\\=\\s*.+com\\.liferay\\.portal\\.kernel\\.servlet\\." +
				"BrowserSnifferUtil\\\"\\)");
	}

}