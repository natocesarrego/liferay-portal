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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaGetFileMethodCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		String newContent = content;

		if (!fileName.endsWith(".java")) {
			return content;
		}

		boolean replaced = false;

		Matcher getFileMatcher = _getFilePattern.matcher(newContent);

		while (getFileMatcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				content, getFileMatcher.start());

			if (methodCall.contains("DLFileEntryLocalServiceUtil")) {
				newContent = _format(newContent, methodCall);
			}
			else if (_hasClassName(
						"DLFileEntryLocalService", newContent, methodCall)) {

				newContent = _format(newContent, methodCall);
			}

			replaced = true;
		}

		if (replaced) {
			newContent = JavaSourceUtil.addImport(
				newContent, "import com.liferay.portal.kernel.util.FileUtil;");
		}

		return newContent;
	}

	private String _format(String content, String methodCall) {
		String[] lines = StringUtil.splitLines(content);

		for (String line : lines) {
			if (line.contains(methodCall)) {
				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				if (parameterList.size() != 3) {
					return content;
				}

				String parameters = StringUtil.merge(
					parameterList
				).replace(
					StringPool.COMMA, StringPool.COMMA_AND_SPACE
				);

				StringBundler newLineSB = new StringBundler(8);

				newLineSB.append(SourceUtil.getIndent(line));
				newLineSB.append("InputStream inputStream = ");
				newLineSB.append("dlFileEntryLocalService.getFileAsStream(");
				newLineSB.append(parameters);
				newLineSB.append(StringPool.CLOSE_PARENTHESIS);
				newLineSB.append(StringPool.SEMICOLON);
				newLineSB.append(StringPool.NEW_LINE);
				newLineSB.append(StringPool.NEW_LINE);

				String lastLine = StringUtil.replace(
					line, methodCall,
					StringUtil.replace(
						methodCall, methodCall,
						"FileUtil.createTempFile(inputStream)"));

				content = StringUtil.replace(
					content, line, newLineSB + lastLine);
			}
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

	private static final Pattern _getFilePattern = Pattern.compile(
		"\\t*?\\w+\\.getFile\\(");

}