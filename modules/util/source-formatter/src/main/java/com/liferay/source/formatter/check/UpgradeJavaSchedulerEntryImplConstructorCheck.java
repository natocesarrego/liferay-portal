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

			Matcher constructorMatcher = _constructorPattern.matcher(
				constructorContent);

			if (constructorMatcher.find()) {
				JavaSignature signature = childJavaTerm.getSignature();

				constructorContent = StringUtil.replace(
					constructorContent, constructorMatcher.group(0),
					_replaceConstructor(signature.getParameters()));
			}

			content = StringUtil.replace(
				content, childJavaTerm.getContent(), constructorContent);
		}

		return content;
	}

	private String _newConstructor(String parameter) {
		StringBundler sb = new StringBundler(7);

		sb.append("super(");
		sb.append(parameter);
		sb.append(".getEventListenerClass(), ");
		sb.append(parameter);
		sb.append(".getTrigger(), ");
		sb.append(parameter);
		sb.append(".getDescription());");

		return sb.toString();
	}

	private String _replaceConstructor(List<JavaParameter> parameters) {
		for (JavaParameter parameter : parameters) {
			String parameterType = parameter.getParameterType();

			if (parameterType.equals("SchedulerEntryImpl")) {
				return _newConstructor(parameter.getParameterName());
			}
		}

		return "super(StringPool.BLANK, null, StringPool.BLANK);";
	}

	private static final Pattern _constructorPattern = Pattern.compile(
		"super\\(\\);");

}