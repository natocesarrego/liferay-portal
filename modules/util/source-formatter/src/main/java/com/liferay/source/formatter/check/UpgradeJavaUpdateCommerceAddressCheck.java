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

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
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
public class UpgradeJavaUpdateCommerceAddressCheck extends BaseFileCheck {

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
				"com.liferay.commerce.model.CommerceAddress")) {

			return content;
		}

		return _replaceVariableUpdateCommerceAddress(content, javaClass);
	}

	private String _getNewUpdateCommerceAddressImplementation(
		String indent, String variable) {

		String[] methods = {
			"getCommerceAddressId", "getName", "getDescription", "getStreet1",
			"getStreet2", "getStreet3", "getCity", "getZip", "getRegionId",
			"getCountryId", "getPhoneNumber", "getType"
		};

		StringBundler sb = new StringBundler(7);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (String method : methods) {
			sb.append(StringPool.NEW_LINE);
			sb.append(indent);
			sb.append(StringPool.TAB);
			sb.append(variable);
			sb.append(StringPool.PERIOD);
			sb.append(method);
			sb.append("(),");
		}

		sb.append(StringPool.NEW_LINE);
		sb.append(indent);
		sb.append(StringPool.TAB);
		sb.append("ServiceContextThreadLocal.getServiceContext()");
		sb.append(StringPool.NEW_LINE);
		sb.append(indent);
		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private boolean _isCommerceAddress(
		String content, String fileContent, String variable) {

		String variableTypeName = getVariableTypeName(
			content, fileContent, variable);

		return variableTypeName.equals("CommerceAddress");
	}

	private String _replaceVariableUpdateCommerceAddress(
		String content, JavaClass javaClass) {

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			Matcher updateCommerceAddressMatcher =
				_updateCommerceAddressPattern.matcher(javaMethodContent);

			if (!updateCommerceAddressMatcher.find() ||
				!_isCommerceAddress(
					javaMethodContent, content,
					updateCommerceAddressMatcher.group(2))) {

				continue;
			}

			String line = updateCommerceAddressMatcher.group();

			String newLine = StringUtil.replace(
				line, updateCommerceAddressMatcher.group(1),
				_getNewUpdateCommerceAddressImplementation(
					SourceUtil.getIndent(line),
					updateCommerceAddressMatcher.group(2)));

			String newJavaMethodContent = StringUtil.replace(
				javaMethodContent, line, newLine);

			content = StringUtil.replace(
				content, javaMethodContent, newJavaMethodContent);
		}

		return content;
	}

	private static final Pattern _updateCommerceAddressPattern =
		Pattern.compile(
			"\\t+\\w+\\.\\s*updateCommerceAddress(\\(\\s*(\\w+)\\s*\\));");

}