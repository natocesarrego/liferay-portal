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
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Cavalcanti
 */
public class JavaUpgradeFetchCPDefinitionByCProductExternalReferenceCodeCheck
	extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		List<String> importNames = javaTerm.getImportNames();

		String content = javaTerm.getContent();

		if (!importNames.contains(
				"com.liferay.commerce.product.model.CPDefinition")) {

			return content;
		}

		Matcher methodCallMatcher = _methodCallPattern.matcher(content);

		while (methodCallMatcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				content, methodCallMatcher.start());

			if (_checkMethodCall(content, fileContent, methodCall)) {
				content = StringUtil.replace(
					content, methodCallMatcher.group(),
					_reorderParameters(methodCall, methodCallMatcher.group(1)));
			}
		}

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private boolean _checkMethodCall(
		String content, String fileContent, String methodCall) {

		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		if (Objects.equals(
				getVariableTypeName(content, fileContent, parameterList.get(1)),
				"String") &&
			(hasClassOrVariableName(
				"CPDefinitionService", content, fileContent, methodCall) ||
			 hasClassOrVariableName(
				 "CPDefinitionLocalService", content, fileContent,
				 methodCall))) {

			return true;
		}

		return false;
	}

	private String _reorderParameters(String methodCall, String parameters) {
		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		return StringUtil.replace(
			methodCall, parameters,
			StringBundler.concat(
				parameterList.get(1), StringPool.COMMA_AND_SPACE,
				parameterList.get(0)));
	}

	private static final Pattern _methodCallPattern = Pattern.compile(
		"\\w+\\.\\s*fetchCPDefinitionByCProductExternalReferenceCode" +
			"\\((\\s*.+,\\s*.+)\\s*\\)");

}