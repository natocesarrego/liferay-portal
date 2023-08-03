/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_2;

import com.liferay.dynamic.data.mapping.internal.upgrade.Template;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Albert Gomes Cabral
 * @author Felipe Veloso
 */
public class TemplateUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		ArrayList<Template> templates = new ArrayList<>();

		templates.add(
			new Template(
				"browserSniffer",
				"\\w+\\s*\\=\\s*.+com\\.liferay\\.portal\\.kernel\\." +
					"servlet\\.BrowserSnifferUtil\\\"\\)",
				StringPool.BLANK));

		_upgradeDDMTemplates(templates);
		_upgradeFragmentEntries(templates);
	}

	private String _getVariableName(Matcher matcher) {
		String matcherGroup = matcher.group();

		String variableName = matcherGroup.substring(
			0, matcherGroup.indexOf(StringPool.EQUAL));

		return variableName.trim();
	}

	private void _upgradeDDMTemplates(ArrayList<Template> templates)
		throws Exception {

		for (Template template : templates) {
			try (PreparedStatement selectPreparedStatement =
					connection.prepareStatement(
						"select templateId, script from DDMTemplate");
				PreparedStatement updatePreparedStatement =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"update DDMTemplate set script = ? where templateId " +
							"= ?")) {

				try (ResultSet resultSet =
						selectPreparedStatement.executeQuery()) {

					while (resultSet.next()) {
						String script = resultSet.getString(2);

						Pattern templatePattern = template.getPattern();

						Matcher templateMatcher = templatePattern.matcher(
							script);

						while (templateMatcher.find()) {
							script = StringUtil.replace(
								script, templateMatcher.group(),
								template.getPatternReplacement());

							if (Validator.isNotNull(
									template.getContextVariable())) {

								script = StringUtil.replace(
									script, _getVariableName(templateMatcher),
									template.getContextVariable());
							}

							Matcher isAssignEmptyMatcher =
								_isAssignEmptyDDMTemplatePattern.matcher(
									script);

							if (isAssignEmptyMatcher.find()) {
								script = isAssignEmptyMatcher.replaceAll(
									template.getPatternReplacement());
							}
						}

						long templateId = resultSet.getLong(1);

						updatePreparedStatement.setString(1, script);
						updatePreparedStatement.setLong(2, templateId);

						updatePreparedStatement.addBatch();
					}

					updatePreparedStatement.executeBatch();
				}
			}
		}
	}

	private void _upgradeFragmentEntries(ArrayList<Template> templates)
		throws Exception {

		for (Template template : templates) {
			try (PreparedStatement selectPreparedStatement =
					connection.prepareStatement(
						"select fragmentEntryId, html from FragmentEntry");
				PreparedStatement updatePreparedStatement =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"update FragmentEntry set html = ? where " +
							"fragmentEntryId = ?")) {

				try (ResultSet resultSet =
						selectPreparedStatement.executeQuery()) {

					while (resultSet.next()) {
						String html = resultSet.getString(2);

						Pattern templatePattern = template.getPattern();

						Matcher templateMatcher = templatePattern.matcher(html);

						while (templateMatcher.find()) {
							html = StringUtil.replace(
								html, templateMatcher.group(),
								template.getPatternReplacement());

							if (Validator.isNotNull(
									template.getContextVariable())) {

								html = StringUtil.replace(
									html, _getVariableName(templateMatcher),
									template.getContextVariable());
							}

							Matcher isAssignEmptyMatcher =
								_isAssignEmptyFragmentEntryPattern.matcher(
									html);

							if (isAssignEmptyMatcher.find()) {
								html = isAssignEmptyMatcher.replaceAll(
									template.getPatternReplacement());
							}
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

	private static final Pattern _isAssignEmptyDDMTemplatePattern =
		Pattern.compile("\\<\\#assign\\s*\\/?\\>");
	private static final Pattern _isAssignEmptyFragmentEntryPattern =
		Pattern.compile("\\[\\#assign\\s*\\/?\\]");

}