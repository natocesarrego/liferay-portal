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

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_2;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Albert Gomes Cabral
 */
public class DDMTemplateBrowserSnifferUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeDDMTemplateRemoveBrowserSniffer();
	}

	protected void upgradeDDMTemplateRemoveBrowserSniffer() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select templateId, script from DDMTemplate");
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMTemplate set script = ? where templateId = ?")) {

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String script = resultSet.getString(2);

					Matcher browserSnifferMatcher =
						_browserSnifferPattern.matcher(script);

					if (browserSnifferMatcher.find()) {
						script = browserSnifferMatcher.replaceAll("");

						Matcher isAssignEmptyMatcher =
							_isAssignEmptyPattern.matcher(script);

						if (isAssignEmptyMatcher.find()) {
							script = isAssignEmptyMatcher.replaceAll("");
						}

						long templateId = resultSet.getLong(1);

						updatePreparedStatement.setString(1, script);
						updatePreparedStatement.setLong(2, templateId);

						updatePreparedStatement.addBatch();
					}
				}

				updatePreparedStatement.executeBatch();
			}
		}
	}

	private static final Pattern _browserSnifferPattern = Pattern.compile(
		"\\w*\\s*\\=\\s*.+com\\.liferay\\.portal\\.kernel\\.servlet\\." +
			"BrowserSnifferUtil\\\"\\)");
	private static final Pattern _isAssignEmptyPattern = Pattern.compile(
		"\\<\\#assign\\s*\\/?\\>");

}