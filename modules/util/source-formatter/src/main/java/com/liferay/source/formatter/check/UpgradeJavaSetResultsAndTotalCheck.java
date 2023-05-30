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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaSetResultsAndTotalCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if ((content == null) || !fileName.endsWith(".java")) {
			return content;
		}

		content = _replaceSetResults(content);
		content = _removeSetTotal(content);

		return content;
	}

	private String _removeSetTotal(String content) {
		String newContent = content;
		Matcher setTotalMatcher = _setTotalPattern.matcher(content);

		while (setTotalMatcher.find()) {
			String methodCall =
				JavaSourceUtil.getMethodCall(content, setTotalMatcher.start()) +
					StringPool.SEMICOLON + StringPool.NEW_LINE;

			if (_validateClassName(methodCall, "SearchContainer", content)) {
				newContent = StringUtil.removeSubstring(
					content, "\t\t" + methodCall);
			}
		}

		return newContent;
	}

	private String _replaceSetResults(String content) {
		String newContent = content;
		Matcher setResultsMatcher = _setResultsPattern.matcher(content);

		while (setResultsMatcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				content, setResultsMatcher.start());

			if (_validateClassName(methodCall, "SearchContainer", content)) {
				newContent = StringUtil.replace(
					content, methodCall,
					StringUtil.replace(
						methodCall, ".setResults(", ".setResultsAndTotal("));
			}
		}

		return newContent;
	}

	private boolean _validateClassName(
		String methodCall, String className, String content) {

		String variable = methodCall.substring(
			0, methodCall.indexOf(CharPool.PERIOD));

		String variableDeclaration = getVariableTypeName(
			content, content, variable);

		if (variableDeclaration.equals(className)) {
			return true;
		}

		return false;
	}

	private static final Pattern _setResultsPattern = Pattern.compile(
		"\\w+\\.setResults\\(");
	private static final Pattern _setTotalPattern = Pattern.compile(
		"\\w+\\.setTotal\\(");

}