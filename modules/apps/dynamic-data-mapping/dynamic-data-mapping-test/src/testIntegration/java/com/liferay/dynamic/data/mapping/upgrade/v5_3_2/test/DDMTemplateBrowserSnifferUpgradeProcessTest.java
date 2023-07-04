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

package com.liferay.dynamic.data.mapping.upgrade.v5_3_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.dynamic.data.mapping.service.test.BaseTemplateUpgradeProcessTest;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Albert Gomes Cabral
 */
@RunWith(Arquillian.class)
public class DDMTemplateBrowserSnifferUpgradeProcessTest extends
	BaseTemplateUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradeProcessDDMTemplateRemoveBrowserSniffer()
		throws Exception {

		DDMTemplate ddmTemplate = addDDMTemplateUpgradeProcessTest(
			"v5_3_2/",
			"ddm-template-browser-sniffer-content.ftl"
		);

		_runUpgradeTemplate();

		long templateId = ddmTemplate.getTemplateId();

		String script = _ddmTemplateService.getTemplate(
			templateId
		).getScript();

		Assert.assertEquals(
			_read("v5_3_2/",
				"expected-ddm-template-browser-sniffer-content.ftl"),
			script);
	}

	@Test
	public void testUpgradeFragmentEntryRemoveBrowserSniffer()
		throws Exception {

		FragmentEntry fragmentEntry = addFragmentEntryUpgradeProcessTest(
			"v5_3_2/",
			"fragment-entry-browser-sniffer-content.html"
		);

		_runUpgradeTemplate();

		long fragmentEntryId = fragmentEntry.getFragmentEntryId();

		String html = _fragmentEntryService.fetchFragmentEntry(
			fragmentEntryId
		).getHtml();

		Assert.assertEquals(
			_read("v5_3_2/",
				"expected-fragment-entry-browser-sniffer-content.html"),
			html);
	}

	@Override
	protected String classNameTemplateUpgradeProcessTest() throws Exception {
		return _CLASS_NAME;
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_2." +
		"DDMTemplateBrowserSnifferUpgradeProcess";

	@Inject
	private DDMTemplateService _ddmTemplateService;

	@Inject
	private FragmentEntryService _fragmentEntryService;

}
