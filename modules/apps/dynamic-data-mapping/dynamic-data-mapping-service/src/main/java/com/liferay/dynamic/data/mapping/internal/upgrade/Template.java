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

package com.liferay.dynamic.data.mapping.internal.upgrade;

import java.util.regex.Pattern;

/**
 * @author Felipe Veloso
 */
public class Template {

	public Template(
		String pattern, String patternReplacement, String contextVariable) {

		_pattern = Pattern.compile(pattern);
		_patternReplacement = patternReplacement;
		_contextVariable = contextVariable;
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