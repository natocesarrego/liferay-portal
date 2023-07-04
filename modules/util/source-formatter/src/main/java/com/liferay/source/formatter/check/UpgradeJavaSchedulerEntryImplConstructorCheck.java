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
import com.liferay.petra.string.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Cavalcanti
 */
public class UpgradeJavaSchedulerEntryImplConstructorCheck
	extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".java")) {
			return content;
		}

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		List<String> extendedClassNames = javaClass.getExtendedClassNames();

		if (!extendedClassNames.contains("SchedulerEntryImpl")) {
			return content;
		}

		return _checkConstructor(content, javaClass);
	}

	private String _checkConstructor(String content, JavaClass javaClass) {
		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaConstructor()) {
				continue;
			}

			String constructorContent = childJavaTerm.getContent();

			Matcher superConstructorMatcher = _superConstructorPattern.matcher(
				constructorContent);

			if (superConstructorMatcher.find()) {
				JavaSignature signature = childJavaTerm.getSignature();

				constructorContent = StringUtil.replace(
					constructorContent, superConstructorMatcher.group(0),
					_replaceSuper(signature.getParameters()));
			}

			content = StringUtil.replace(
				content, childJavaTerm.getContent(), constructorContent);
		}

		return content;
	}

	private String _getNewSuperImplementation(String parameterName) {
		StringBundler sb = new StringBundler(7);

		sb.append("super(");
		sb.append(parameterName);
		sb.append(".getEventListenerClass(), ");
		sb.append(parameterName);
		sb.append(".getTrigger(), ");
		sb.append(parameterName);
		sb.append(".getDescription());");

		return sb.toString();
	}

	private String _replaceSuper(List<JavaParameter> parameters) {
		for (JavaParameter parameter : parameters) {
			String parameterType = parameter.getParameterType();

			if (parameterType.equals("SchedulerEntryImpl")) {
				return _getNewSuperImplementation(parameter.getParameterName());
			}
		}

		return "super(StringPool.BLANK, null, StringPool.BLANK);";
	}

	private static final Pattern _superConstructorPattern = Pattern.compile(
		"super\\(\\);");

}