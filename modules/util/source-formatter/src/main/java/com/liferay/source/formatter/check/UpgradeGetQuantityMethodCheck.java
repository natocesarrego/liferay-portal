/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeGetQuantityMethodCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		Matcher matcher = _getQuantityPattern.matcher(content);

		while (matcher.find()) {
			if (!hasClassOrVariableName(
					"CommerceOrderItem", content, fileName, matcher.group())) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Unable to format  getQuantity method from ",
					"CommerceOrderItem. The method has been changed to return ",
					"BigDecimal type, see LPS-197931."));
		}

		return content;
	}

	@Override
	protected String[] getValidExtensions() {
		return new String[] {"java", "jsp"};
	}

	private static final Pattern _getQuantityPattern = Pattern.compile(
		"\\w+\\.getQuantity\\(");

}