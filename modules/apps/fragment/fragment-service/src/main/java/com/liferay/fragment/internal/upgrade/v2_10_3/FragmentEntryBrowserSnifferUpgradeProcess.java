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

package com.liferay.fragment.internal.upgrade.v2_10_3;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Albert Gomes Cabral
 */
public class FragmentEntryBrowserSnifferUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeFragmentEntryRemoveBrowserSniffer();
	}

	private void _upgradeFragmentEntryRemoveBrowserSniffer() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select fragmentEntryId, html from FragmentEntry");
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update FragmentEntry set html = ? where fragmentEntryId " +
						"= ?")) {

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String html = resultSet.getString(2);

					Matcher browserSnifferMatcher =
						_browserSnifferPattern.matcher(html);

					if (browserSnifferMatcher.find()) {
						html = browserSnifferMatcher.replaceAll("");

						Matcher isAssignEmptyMatcher =
							_isAssignEmptyPattern.matcher(html);

						if (isAssignEmptyMatcher.find()) {
							html = isAssignEmptyMatcher.replaceAll("");
						}

						long fragmentEntryId = resultSet.getLong(1);

						updatePreparedStatement.setString(1, html);
						updatePreparedStatement.setLong(2, fragmentEntryId);

						updatePreparedStatement.addBatch();
					}

					updatePreparedStatement.executeBatch();
				}
			}
		}
	}

	private static final Pattern _browserSnifferPattern = Pattern.compile(
		"\\w*\\s*\\=\\s*.+com\\.liferay\\.portal\\.kernel\\.servlet\\." +
			"BrowserSnifferUtil\\\"\\)");
	private static final Pattern _isAssignEmptyPattern = Pattern.compile(
		"\\[\\#assign\\s*\\/?\\]");

}