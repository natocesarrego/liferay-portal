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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Cavalcanti
 */
public class UpgradeJavaGetLayoutDisplayPageProviderCheck
	extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".java")) {
			return content;
		}

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		List<String> importNames = javaClass.getImportNames();

		if (!importNames.contains(
				"com.liferay.layout.display.page.LayoutDisplayPageProvider")) {

			return content;
		}

		return _replaceGetLayoutDisplayPageProvider(content, javaClass);
	}

	private boolean _isLayoutDisplayPageProviderRegistry(
		String content, String fileContent, String methodCall) {

		String variableTypeName = getVariableTypeName(
			content, fileContent,
			methodCall.substring(0, methodCall.indexOf(StringPool.PERIOD)),
			true);

		return variableTypeName.contains("LayoutDisplayPageProvider");
	}

	private String _replaceGetLayoutDisplayPageProvider(
		String content, JavaClass javaClass) {

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			Matcher matcher = _pattern.matcher(javaMethodContent);

			while (matcher.find()) {
				javaMethodContent = StringUtil.replace(
					javaMethodContent, matcher.group(0),
					_replaceGetLayoutDisplayPageProvider(
						javaMethodContent, content, matcher.group(0)));
			}

			content = StringUtil.replace(
				content, javaMethod.getContent(), javaMethodContent);
		}

		return content;
	}

	private String _replaceGetLayoutDisplayPageProvider(
		String content, String fileContent, String methodCall) {

		if (!_isLayoutDisplayPageProviderRegistry(
				content, fileContent, methodCall)) {

			return methodCall;
		}

		return StringUtil.replace(
			methodCall, "getLayoutDisplayPageProvider",
			"getLayoutDisplayPageProviderByClassName");
	}

	private static final Pattern _pattern = Pattern.compile(
		"\\w+\\.\\s*getLayoutDisplayPageProvider\\s*\\(\\s*.+\\s*\\)\\;");

}