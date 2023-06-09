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

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaGetPortletGroupIdMethodCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".java") && !fileName.endsWith(".jsp") &&
			!fileName.endsWith(".jspf") && !fileName.endsWith(".ftl")) {

			return content;
		}

		Matcher getPortletGroupIdMatcher = _getPortletGroupIdPattern.matcher(
			content);

		while (getPortletGroupIdMatcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				content, getPortletGroupIdMatcher.start());

			if (fileName.endsWith(".java") &&
				!_hasClassName("ThemeDisplay", content, methodCall)) {

				return content;
			}

			content = StringUtil.replace(
				content, methodCall,
				StringUtil.replace(
					methodCall, ".getPortletGroupId()", ".getScopeGroupId()"));
		}

		return content;
	}

	private boolean _hasClassName(
		String className, String content, String methodCall) {

		String variable = methodCall.substring(
			0, methodCall.indexOf(CharPool.PERIOD));

		String variableTypeName = getVariableTypeName(
			content, content, variable.trim());

		if (variableTypeName.contains(className)) {
			return true;
		}

		return false;
	}

	private static final Pattern _getPortletGroupIdPattern = Pattern.compile(
		"\\w+\\.getPortletGroupId");

}