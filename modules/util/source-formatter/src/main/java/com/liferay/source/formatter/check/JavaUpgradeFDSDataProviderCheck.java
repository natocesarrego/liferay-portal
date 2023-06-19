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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Cavalcanti
 */
public class JavaUpgradeFDSDataProviderCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		List<String> importNames = javaTerm.getImportNames();

		String javaTermContent = javaTerm.getContent();

		if (!importNames.contains(
				"com.liferay.frontend.data.set.provider.FDSDataProvider")) {

			return javaTermContent;
		}

		Matcher implementsFDSDataProviderMatcher =
			_implementsFDSDataProviderPattern.matcher(javaTermContent);

		if (implementsFDSDataProviderMatcher.find()) {
			javaTermContent = _sortVariablesMethods(javaTermContent);
		}

		Matcher variableFDSDataProviderMatcher =
			_variableFDSDataProviderPattern.matcher(javaTermContent);

		if (variableFDSDataProviderMatcher.find()) {
			javaTermContent = _sortVariablesMethodCalls(
				javaTermContent, variableFDSDataProviderMatcher.group(1));
		}

		return javaTermContent;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private boolean _hasClassName(
		String content, String fileContent, String variableName) {

		return Objects.equals(
			getVariableTypeName(content, fileContent, variableName),
			"HttpServletRequest");
	}

	private String _sortVariablesMethodCalls(
		String content, String variableName) {

		Matcher methodCallGetItemsMatcher = _methodCallGetItemsPattern.matcher(
			content);

		while (methodCallGetItemsMatcher.find()) {
			if (variableName.equals(methodCallGetItemsMatcher.group(1)) &&
				_hasClassName(
					content, content, methodCallGetItemsMatcher.group(3))) {

				String newOrderVariablesMethodCall = StringBundler.concat(
					methodCallGetItemsMatcher.group(4),
					StringPool.COMMA_AND_SPACE,
					methodCallGetItemsMatcher.group(5),
					StringPool.COMMA_AND_SPACE,
					methodCallGetItemsMatcher.group(3),
					StringPool.COMMA_AND_SPACE,
					methodCallGetItemsMatcher.group(6));

				String newLine = StringUtil.replace(
					methodCallGetItemsMatcher.group(0),
					methodCallGetItemsMatcher.group(2),
					newOrderVariablesMethodCall);

				content = StringUtil.replace(
					content, methodCallGetItemsMatcher.group(0), newLine);
			}
		}

		Matcher methodCallGetItemsCountMatcher =
			_methodCallGetItemsCountPattern.matcher(content);

		while (methodCallGetItemsCountMatcher.find()) {
			if (variableName.equals(methodCallGetItemsCountMatcher.group(1)) &&
				_hasClassName(
					content, content,
					methodCallGetItemsCountMatcher.group(3))) {

				String newOrderVariablesMethodCall = StringBundler.concat(
					methodCallGetItemsCountMatcher.group(4),
					StringPool.COMMA_AND_SPACE,
					methodCallGetItemsCountMatcher.group(3));

				String newLine = StringUtil.replace(
					methodCallGetItemsCountMatcher.group(0),
					methodCallGetItemsCountMatcher.group(2),
					newOrderVariablesMethodCall);

				content = StringUtil.replace(
					content, methodCallGetItemsCountMatcher.group(0), newLine);
			}
		}

		return content;
	}

	private String _sortVariablesMethods(String content) {
		Matcher methodGetItemsMatcher = _methodGetItemsPattern.matcher(content);

		if (methodGetItemsMatcher.find()) {
			String newOrderVariables = StringBundler.concat(
				methodGetItemsMatcher.group(3), StringPool.COMMA_AND_SPACE,
				methodGetItemsMatcher.group(4), StringPool.COMMA_AND_SPACE,
				methodGetItemsMatcher.group(2), StringPool.COMMA_AND_SPACE,
				methodGetItemsMatcher.group(5));

			content = StringUtil.replace(
				content, methodGetItemsMatcher.group(1), newOrderVariables);
		}

		Matcher methodGetItemsCountMatcher =
			_methodGetItemsCountPattern.matcher(content);

		if (methodGetItemsCountMatcher.find()) {
			String newOrderVariables = StringBundler.concat(
				methodGetItemsCountMatcher.group(3), StringPool.COMMA_AND_SPACE,
				methodGetItemsCountMatcher.group(2));

			content = StringUtil.replace(
				content, methodGetItemsCountMatcher.group(1),
				newOrderVariables);
		}

		return content;
	}

	private static final Pattern _implementsFDSDataProviderPattern =
		Pattern.compile("implements\\s*FDSDataProvider");
	private static final Pattern _methodCallGetItemsCountPattern =
		Pattern.compile(
			"(\\w+)\\.getItemsCount\\((\\s*(\\w+),\\s*(\\w+))\\s*\\)");
	private static final Pattern _methodCallGetItemsPattern = Pattern.compile(
		"(\\w+)\\.getItems\\((\\s*(\\w+),\\s*(\\w+)," +
			"\\s*(\\w+),\\s*(\\w+))\\s*\\)");
	private static final Pattern _methodGetItemsCountPattern = Pattern.compile(
		"getItemsCount\\((\\s*(HttpServletRequest \\w+)," +
			"\\s*(FDSKeywords \\w+))\\s*\\)");
	private static final Pattern _methodGetItemsPattern = Pattern.compile(
		"getItems\\((\\s*(HttpServletRequest \\w+),\\s*(FDSKeywords \\w+)," +
			"\\s*(FDSPagination \\w+),\\s*(Sort \\w+))\\s*\\)");
	private static final Pattern _variableFDSDataProviderPattern =
		Pattern.compile("\\t*FDSDataProvider\\s*\\<\\s*.*\\s*\\>\\s*(\\w+);");

}