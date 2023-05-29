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
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class UpgradeJavaCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".java")) {
			return content;
		}

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		return _fixImports(javaClass, content);
	}

	private String _fixImports(JavaClass javaClass, String content)
		throws Exception {

		Map<String, String> importsMap = _getImportsMap();

		for (String importName : javaClass.getImportNames()) {
			String newImportName = importsMap.get(importName);

			if (newImportName != null) {
				String className = _getClassNameByImport(importName);
				String newClassName = _getClassNameByImport(newImportName);

				if (className.equals(newClassName)) {
					content = _replaceImport(
						content, importName, newImportName);
				}
				else {
					content = _replaceImport(
						content, importName, newImportName);

					content = _replaceImpl(content, className, newClassName);
				}
			}
		}

		return content;
	}

	private String _getClassNameByImport(String importName) {
		String[] splitImport = StringUtil.split(importName, StringPool.PERIOD);

		return splitImport[splitImport.length - 1];
	}

	private synchronized Map<String, String> _getImportsMap() throws Exception {
		if (_importsMap == null) {
			_importsMap = _getMap("/imports.txt");
		}

		return _importsMap;
	}

	private Map<String, String> _getMap(String fileName) throws Exception {
		Map<String, String> map = new HashMap<>();

		File importsFile = SourceFormatterUtil.getFile(
			getBaseDirName(),
			"modules/util/source-formatter/src/main/resources/dependencies/" +
				fileName,
			getMaxDirLevel());

		if (importsFile == null) {
			return map;
		}

		String[] lines = StringUtil.splitLines(FileUtil.read(importsFile));

		for (String line : lines) {
			int separatorIndex = line.indexOf(StringPool.EQUAL);

			map.put(
				line.substring(0, separatorIndex),
				line.substring(separatorIndex + 1));
		}

		return map;
	}

	private String _replaceImpl(
		String content, String className, String newClassName) {

		String[] lines = content.split(StringPool.NEW_LINE);

		for (String line : lines) {
			if (line.contains(className) && !line.contains("import")) {
				String newLine = StringUtil.replace(
					line, className, newClassName);

				content = StringUtil.replace(content, line, newLine);
			}

			String implClass = StringUtil.lowerCaseFirstLetter(className);

			if (line.contains(implClass)) {
				String newLine = StringUtil.replace(
					line, implClass,
					StringUtil.lowerCaseFirstLetter(newClassName));

				content = StringUtil.replace(content, line, newLine);
			}
		}

		return content;
	}

	private String _replaceImport(
		String content, String importName, String newImportName) {

		return StringUtil.replace(
			content,
			StringBundler.concat("import ", importName, StringPool.SEMICOLON),
			StringBundler.concat(
				"import ", newImportName, StringPool.SEMICOLON));
	}

	private Map<String, String> _importsMap;

}