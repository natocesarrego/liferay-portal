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
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Torres
 */
public class UpgradeJavaOnAfterUpdateParameterCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		JavaClass javaClass = (JavaClass)javaTerm;

		if (!_checkMethods(javaClass)) {
			return fileContent;
		}

		String newContent = javaTerm.getContent();

		for (JavaTerm childJavaTerms : javaClass.getChildJavaTerms()) {
			String javaMethodContent = childJavaTerms.getContent();

			Matcher onAfterUpdateMatcher = _onAfterUpdatePattern.matcher(
				javaMethodContent);

			while (onAfterUpdateMatcher.find()) {
				newContent = _format(
					JavaSourceUtil.getMethodCall(
						javaMethodContent, onAfterUpdateMatcher.start()),
					newContent);
			}
		}

		return newContent;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private boolean _checkMethods(JavaClass javaClass) {
		List<String> extendedClassNames = javaClass.getExtendedClassNames();

		if (extendedClassNames.contains("BaseModelListener")) {
			return true;
		}

		return false;
	}

	private String _format(String methodCall, String newContent) {
		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		if (parameterList.size() != 1) {
			return newContent;
		}

		String[] split = StringUtil.split(parameterList.get(0), " ");

		StringBundler parametersSB = new StringBundler(3);

		parametersSB.append("original");
		parametersSB.append(
			StringUtil.replaceFirst(
				split[1], split[1].charAt(0),
				Character.toUpperCase(split[1].charAt(0))));
		parametersSB.append(StringPool.COMMA_AND_SPACE);

		String parameter = StringUtil.replace(
			parameterList.get(0), split[1], parametersSB.toString());

		return StringUtil.replace(
			newContent, methodCall,
			StringUtil.replace(
				methodCall, "onAfterUpdate(", "onAfterUpdate(" + parameter));
	}

	private static final Pattern _onAfterUpdatePattern = Pattern.compile(
		" void\\s*onAfterUpdate\\(");

}