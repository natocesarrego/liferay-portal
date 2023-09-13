/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradePortletDisplayCheck extends BaseUpgradeCheck {

	@Override
	protected String afterFormat(
		String fileName, String absolutePath, String content,
		String newContent) {

		return addNewImports(fileName, newContent);
	}

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (fileName.endsWith(".java")) {
			return _formatJava(content, fileName);
		}

		return _formatJSP(content);
	}

	@Override
	protected String[] getNewImports() {
		return new String[] {
			"com.liferay.portal.configuration.module.configuration." +
				"ConfigurationProviderUtil"
		};
	}

	@Override
	protected String[] getValidExtensions() {
		return new String[] {"java", "jsp"};
	}

	private String _formatJava(String content, String fileName)
		throws Exception {

		String newContent = content;

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			Matcher getPortletInstanceConfigurationMatcher =
				_getPortletInstanceConfigurationPattern.matcher(
					javaMethodContent);

			while (getPortletInstanceConfigurationMatcher.find()) {
				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent,
					getPortletInstanceConfigurationMatcher.start());

				if (!hasClassOrVariableName(
						"PortletDisplay", content, fileName, methodCall)) {

					continue;
				}

				String indent = JavaSourceUtil.getIndent(methodCall);

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				StringBundler sb = new StringBundler(3);

				sb.append(indent);
				sb.append("ConfigurationProviderUtil");
				sb.append(getPortletInstanceConfigurationMatcher.group(2));

				String newJavaMethod = null;

				Matcher themeDisplayMatcher = _themeDisplayPattern.matcher(
					javaMethodContent);

				if (themeDisplayMatcher.find()) {
					newJavaMethod = StringUtil.replace(
						javaMethodContent, methodCall,
						JavaSourceUtil.addMethodNewParameters(
							indent, new int[] {parameterList.size()},
							sb.toString(),
							new String[] {themeDisplayMatcher.group(1)},
							parameterList));
				}
				else {
					String themeDisplayCall =
						getPortletInstanceConfigurationMatcher.group(1) +
							".getThemeDisplay()";

					newJavaMethod = StringUtil.replace(
						javaMethodContent, methodCall,
						JavaSourceUtil.addMethodNewParameters(
							indent, new int[] {parameterList.size()},
							sb.toString(), new String[] {themeDisplayCall},
							parameterList));
				}

				newContent = StringUtil.replace(
					newContent, javaMethodContent, newJavaMethod);
			}
		}

		return newContent;
	}

	private String _formatJSP(String content) {
		String newContent = content;

		Matcher getPortletInstanceConfigurationMatcher =
			_getPortletInstanceConfigurationPattern.matcher(content);

		while (getPortletInstanceConfigurationMatcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				content, getPortletInstanceConfigurationMatcher.start());

			String variableName = getVariableName(methodCall);

			if (!variableName.contains("portletDisplay")) {
				continue;
			}

			String indent = JavaSourceUtil.getIndent(methodCall);

			List<String> parameterList = JavaSourceUtil.getParameterList(
				methodCall);

			StringBundler sb = new StringBundler(3);

			sb.append(indent);
			sb.append("ConfigurationProviderUtil");
			sb.append(getPortletInstanceConfigurationMatcher.group(2));

			newContent = StringUtil.replace(
				newContent, methodCall,
				JavaSourceUtil.addMethodNewParameters(
					indent, new int[] {parameterList.size()}, sb.toString(),
					new String[] {"themeDisplay"}, parameterList));
		}

		return newContent;
	}

	private static final Pattern _getPortletInstanceConfigurationPattern =
		Pattern.compile("\\t*(\\w+)(.\\s*getPortletInstanceConfiguration\\()");
	private static final Pattern _themeDisplayPattern = Pattern.compile(
		"ThemeDisplay\\s*(\\w+)");

}