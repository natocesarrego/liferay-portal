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

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaHashUtilImportCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		List<String> importNames = JavaSourceUtil.getImportNames(content);

		if (!importNames.contains("com.liferay.portal.kernel.util.HashUtil") ||
			importNames.contains("com.liferay.petra.lang.HashUtil")) {

			return content;
		}

		return StringUtil.replace(
			content, "import com.liferay.portal.kernel.util.HashUtil;",
			"import com.liferay.petra.lang.HashUtil;");
	}

}