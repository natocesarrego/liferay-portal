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

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Albert Gomes Cabral
 */
public abstract class BaseTemplateUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeDDMTemplateRemoveOldVariablesInject();
		upgradeFragmentEntryRemoveOldVariablesInject();
	}

	protected void upgradeDDMTemplateRemoveOldVariablesInject() throws Exception {
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

					Matcher oldRemoveVariableInjectMatcher =
						getTemplatePattern().matcher(script);

					if (oldRemoveVariableInjectMatcher.find()) {
						script = oldRemoveVariableInjectMatcher.replaceAll(
							StringPool.BLANK);

						Matcher isAssignEmptyMatcher =
							_isAssignEmptyDDMTEmplatePattern.matcher(script);

						if (isAssignEmptyMatcher.find()) {
							script = isAssignEmptyMatcher.replaceAll(StringPool.BLANK);
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

	protected void upgradeFragmentEntryRemoveOldVariablesInject()
		throws Exception {

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

					Matcher oldRemoveVariableInjectMatcher =
						getTemplatePattern().matcher(html);

					if (oldRemoveVariableInjectMatcher.find()) {
						html = oldRemoveVariableInjectMatcher.replaceAll(
							StringPool.BLANK);

						Matcher isAssignEmptyMatcher =
							_isAssignEmptyFragmentEntryPattern.matcher(html);

						if (isAssignEmptyMatcher.find()) {
							html = isAssignEmptyMatcher.replaceAll(StringPool.BLANK);
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

	protected abstract Pattern getTemplatePattern() throws Exception;

	private static final Pattern _isAssignEmptyDDMTEmplatePattern = Pattern.compile(
		"\\<\\#assign\\s*\\/?\\>");
	private static final Pattern _isAssignEmptyFragmentEntryPattern = Pattern.compile(
		"\\[\\#assign\\s*\\/?\\]");
}