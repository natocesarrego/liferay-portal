/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.io.InputStream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nícolas Moura
 */
public class UpgradeReplacementCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		String newContent = content;

		JSONArray jsonArray = _getReplacementPatternsJSONArray(
			"replacement-patterns.json");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (!_isValidExtension(fileName, jsonObject)) {
				continue;
			}

			boolean replaced = false;

			Pattern pattern = Pattern.compile(jsonObject.getString("regex"));

			Matcher matcher = pattern.matcher(content);

			while (matcher.find()) {
				String methodCall = matcher.group();

				if (fileName.endsWith(".java")) {
					String className = jsonObject.getString("className");

					if (!className.equals(StringPool.BLANK) &&
						!className.endsWith("Util") &&
						!hasClassOrVariableName(
							className, content, content, methodCall)) {

						continue;
					}
				}

				newContent = StringUtil.replace(
					newContent, methodCall,
					StringUtil.replace(
						methodCall, jsonObject.getString("original"),
						jsonObject.getString("originalReplacement")));

				replaced = true;
			}

			if (replaced) {
				newContent = _addCheckDependencies(
					fileName, newContent, jsonObject);
			}
		}

		return newContent;
	}

	private String _addCheckDependencies(
		String fileName, String newContent, JSONObject jsonObject) {

		if (fileName.endsWith(".java")) {
			newContent = _addNewImports(
				newContent,
				JSONUtil.toStringArray(jsonObject.getJSONArray("newImports")));

			return _addNewReference(
				newContent, jsonObject.getString("newReference"));
		}

		return newContent;
	}

	private String _addNewImports(String newContent, String[] newImports) {
		if (newImports.length != 0) {
			newContent = JavaSourceUtil.addImports(newContent, newImports);
		}

		return newContent;
	}

	private String _addNewReference(String content, String newReference) {
		if (!newReference.equals(StringPool.BLANK)) {
			content = _addNewImports(
				content,
				new String[] {
					"org.osgi.service.component.annotations.Reference"
				});

			return StringUtil.replaceLast(
				content, CharPool.CLOSE_CURLY_BRACE,
				"\n\t@Reference\n\tprivate " + newReference + ";\n\n}");
		}

		return content;
	}

	private JSONArray _getReplacementPatternsJSONArray(String fileName)
		throws Exception {

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"dependencies/" + fileName);

		if (inputStream == null) {
			return new JSONArrayImpl();
		}

		return new JSONArrayImpl(StringUtil.read(inputStream));
	}

	private boolean _isValidExtension(String fileName, JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.getJSONArray("validExtensions");

		for (String validExtension : JSONUtil.toStringArray(jsonArray)) {
			if (fileName.endsWith(CharPool.PERIOD + validExtension)) {
				return true;
			}
		}

		return false;
	}

}