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

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.VelocityMigrationUtil;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nícolas Moura
 */
public class UpgradeVelocityVariableReferenceMigrationCheck
	extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith(".vm")) {
			return content;
		}

		VelocityMigrationUtil.writeMigratedContent(
			_formatContent(
				VelocityMigrationUtil.getFreeMarkerMigratedContent(
					content, fileName)),
			fileName);

		return content;
	}

	private static boolean _isAttribute(String line, String match) {
		boolean attribute = false;

		int initMatchIndex = line.indexOf(match);

		String afterMatch = line.substring(initMatchIndex + match.length());
		String beforeMatch = line.substring(0, initMatchIndex);

		for (int i = 1; i < (beforeMatch.length() + 1); i++) {
			int nextCharIndex = initMatchIndex - i;

			if ((line.charAt(nextCharIndex) == CharPool.QUOTE) &&
				((StringUtil.count(afterMatch, CharPool.QUOTE) % 2) != 0)) {

				attribute = true;

				break;
			}

			if ((line.charAt(nextCharIndex) == CharPool.APOSTROPHE) &&
				((StringUtil.count(afterMatch, CharPool.APOSTROPHE) % 2) !=
					0)) {

				attribute = true;

				break;
			}
		}

		return attribute;
	}

	private String _formatContent(String content) {
		String[] lines = content.split(StringPool.NEW_LINE);

		for (String line : lines) {
			Matcher matcher = _variableReferencePattern.matcher(line);

			String newLine = line;

			while (matcher.find()) {
				String match = matcher.group();

				if (_isJQuery(match)) {
					continue;
				}

				if (!_isValidReplacement(line, match)) {
					newLine = StringUtil.replace(
						newLine, match,
						StringUtil.removeChar(match, CharPool.DOLLAR));

					continue;
				}

				int beginCharIndex = line.indexOf(match) + match.length();

				if (line.charAt(beginCharIndex) == CharPool.OPEN_PARENTHESIS) {
					String endLine = line.substring(beginCharIndex);

					boolean newMethodCall = false;
					int stack = 0;

					for (int i = 0; i < endLine.length(); i++) {
						int nextCharIndex = beginCharIndex + i;

						if (line.charAt(nextCharIndex) == '(') {
							stack += 1;
						}
						else if (line.charAt(nextCharIndex) == ')') {
							stack -= 1;
							newMethodCall = false;

							if (line.charAt(nextCharIndex + 1) ==
									CharPool.PERIOD) {

								newMethodCall = true;
							}
						}

						if ((stack == 0) && !newMethodCall) {
							String endMatch = line.substring(
								beginCharIndex, nextCharIndex + 1);

							String fullMatch = match + endMatch;

							String newReference = StringUtil.replace(
								match, CharPool.DOLLAR, "${");

							newLine = StringUtil.replace(
								newLine, fullMatch,
								newReference + endMatch + "}");

							break;
						}
					}
				}
				else {
					newLine = StringUtil.replace(
						newLine, match,
						StringUtil.replace(match, CharPool.DOLLAR, "${") + "}");
				}
			}

			content = StringUtil.replace(content, line, newLine);
		}

		return content;
	}

	private boolean _isJQuery(String match) {
		boolean jquery = false;

		if (match.charAt(1) == CharPool.OPEN_PARENTHESIS) {
			jquery = true;
		}

		return jquery;
	}

	private boolean _isValidReplacement(String line, String match) {
		boolean valid = true;

		line = StringUtil.removeChars(line, CharPool.SPACE, CharPool.TAB);

		String lineBegin = line.substring(0, 2);

		if ((lineBegin.equals("<#") || (line.charAt(0) == CharPool.POUND)) &&
			!_isAttribute(line, match)) {

			valid = false;
		}

		if ((line.charAt(0) != CharPool.LESS_THAN) &&
			!_isAttribute(line, match)) {

			valid = false;
		}

		return valid;
	}

	private static final Pattern _variableReferencePattern = Pattern.compile(
		"\\$[\\w\\.]+");

}