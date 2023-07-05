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
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.dynamic.data.mapping.service.test.BaseTemplateUpgradeProcessTest;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Albert Gomes Cabral
 */
@RunWith(Arquillian.class)
public class TemplateBrowserSnifferUpgradeProcessTest extends
	BaseTemplateUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradeProcessTemplateRemoveBrowserSniffer()
		throws Exception {

		DDMTemplate ddmTemplate = addDDMTemplate(
			".v5_3_2/ddm-template-browser-sniffer-content.ftl");

		FragmentEntry fragmentEntry = addFragmentEntry(
			".v5_3_2/fragment-entry-browser-sniffer-content.html");

		runTemplateUpgrade();

		long templateId = ddmTemplate.getTemplateId();

		String script = _ddmTemplateService.getTemplate(
			templateId
		).getScript();

		Assert.assertEquals(read
			(".v5_3_2/expected-ddm-template-browser-sniffer-content.ftl"), script);

		long fragmentEntryId = fragmentEntry.getFragmentEntryId();

		String html = _fragmentEntryService.fetchFragmentEntry(
			fragmentEntryId
		).getHtml();

		Assert.assertEquals(read
			(".v5_3_2/expected-fragment-entry-browser-sniffer-content.html"), html);
	}

	@Override
	protected String getUpgradeStepClassName() throws Exception {
		return "com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_2." +
			   "DDMTemplateBrowserSnifferUpgradeProcess";
	}

	@Inject
	private DDMTemplateService _ddmTemplateService;

	@Inject
	private FragmentEntryService _fragmentEntryService;

}
