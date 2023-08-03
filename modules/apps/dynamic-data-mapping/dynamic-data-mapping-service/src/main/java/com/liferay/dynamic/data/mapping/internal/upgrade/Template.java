/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade;

import java.util.regex.Pattern;

/**
 * @author Felipe Veloso
 */
public class Template {

	public Template(
		String contextVariable, String pattern, String patternReplacement) {

		_contextVariable = contextVariable;
		_pattern = Pattern.compile(pattern);
		_patternReplacement = patternReplacement;
	}

	public String getContextVariable() {
		return _contextVariable;
	}

	public Pattern getPattern() {
		return _pattern;
	}

	public String getPatternReplacement() {
		return _patternReplacement;
	}

	private final String _contextVariable;
	private final Pattern _pattern;
	private final String _patternReplacement;

}