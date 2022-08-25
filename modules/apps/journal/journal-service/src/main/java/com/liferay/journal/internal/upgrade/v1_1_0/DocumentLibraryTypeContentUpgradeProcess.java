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

package com.liferay.journal.internal.upgrade.v1_1_0;

import com.liferay.journal.internal.upgrade.helper.JournalArticleImageUpgradeHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XPath;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class DocumentLibraryTypeContentUpgradeProcess extends UpgradeProcess {

	public DocumentLibraryTypeContentUpgradeProcess(
		JournalArticleImageUpgradeHelper journalArticleImageUpgradeHelper) {

		_journalArticleImageUpgradeHelper = journalArticleImageUpgradeHelper;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_updateContent();
	}

	private String _convertContent(long id, String content) throws Exception {
		Document document = null;

		try {
			document = SAXReaderUtil.read(content);
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat("ID: ", id, "\nContent: ", content));

			throw exception;
		}

		document = document.clone();

		XPath xPath = SAXReaderUtil.createXPath(
			"//dynamic-element[@type='document_library']");

		List<Node> imageNodes = xPath.selectNodes(document);

		for (Node imageNode : imageNodes) {
			Element imageElement = (Element)imageNode;

			List<Element> dynamicContentElements = imageElement.elements(
				"dynamic-content");

			for (Element dynamicContentElement : dynamicContentElements) {
				String data =
					_journalArticleImageUpgradeHelper.getDocumentLibraryValue(
						dynamicContentElement.getText());

				dynamicContentElement.clearContent();

				dynamicContentElement.addCDATA(data);
			}
		}

		return document.formattedString();
	}

	private void _updateContent() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select content, id_ from JournalArticle where content like " +
					"'%type=\"document_library\"%'");
			ResultSet resultSet = preparedStatement1.executeQuery();
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update JournalArticle set content = ? where id_ = ?")) {

			while (resultSet.next()) {
				long id = resultSet.getLong(2);

				preparedStatement2.setString(
					1, _convertContent(id, resultSet.getString(1)));
				preparedStatement2.setLong(2, id);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentLibraryTypeContentUpgradeProcess.class);

	private final JournalArticleImageUpgradeHelper
		_journalArticleImageUpgradeHelper;

}