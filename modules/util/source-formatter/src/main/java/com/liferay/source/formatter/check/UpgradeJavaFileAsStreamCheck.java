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
public class UpgradeJavaFileAsStreamCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if ((content == null) || !fileName.endsWith(".java")) {
			return content;
		}

		String newContent = content;

		Matcher getFileMatcher = _getFilePattern.matcher(content);

		while (getFileMatcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				content, getFileMatcher.start());

			if (_validateClassName(
					content, "DLFileEntryLocalService", methodCall)) {

				newContent = StringUtil.replace(
					content, methodCall,
					StringUtil.replace(
						methodCall, methodCall,
						"FileUtil.createTempFile(inputStream)"));

				String[] lines = StringUtil.splitLines(newContent);

				for (String line : lines) {
					if (line.contains("FileUtil.createTempFile(inputStream)")) {
						List<String> parameterList =
							JavaSourceUtil.getParameterList(methodCall);

						if (parameterList.size() != 3) {
							return newContent;
						}

						String newLineStart =
							"InputStream inputStream = " +
								"dlFileEntryLocalService.getFileAsStream(";

						String newLine = StringBundler.concat(
							SourceUtil.getIndent(line), newLineStart,
							StringUtil.merge(
								parameterList
							).replace(
								StringPool.COMMA, StringPool.COMMA_AND_SPACE
							),
							");\n\n");

						newContent = StringUtil.replace(
							newContent, line, newLine + line);

						break;
					}
				}
			}
		}

		return newContent;
	}

	private boolean _validateClassName(
		String content, String className, String methodCall) {

		String variable = methodCall.substring(
			0, methodCall.indexOf(CharPool.PERIOD));

		String variableDeclaration = getVariableTypeName(
			content, content, variable);

		if (variableDeclaration.equals(className)) {
			return true;
		}

		return false;
	}

	private static final Pattern _getFilePattern = Pattern.compile(
		"\\w+\\.getFile\\(");

}