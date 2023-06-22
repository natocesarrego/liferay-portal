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
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

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
public class DDMTemplateBrowserSnifferUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_addDDMTemplateContentInDatabase();
	}

	@Test
	public void testUpgradeDDMTemplateRemoveBrowserSniffer() throws Exception {
		List<DDMTemplate> ddmTemplateList = _addDDMTemplateContentInDatabase();

		for (DDMTemplate ddmTemplateIndex : ddmTemplateList) {
			long templateId = ddmTemplateList.get(
				(int)ddmTemplateIndex.getTemplateId()
			).getTemplateId();

			_runUpgrade();

			String scriptResult = _ddmTemplateService.getTemplate(
				templateId
			).getScript();

			Assert.assertEquals(
				_read("updated-ddm-template-browser-sniffer-content.ftl"),
				scriptResult);
		}
	}

	private List<DDMTemplate> _addDDMTemplateContentInDatabase()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		long classPK = ParamUtil.getLong(serviceContext, "classPK");

		_ddmTemplateService.addTemplate(
			_group.getGroupId(), _portal.getClassNameId(DDMTemplate.class),
			classPK, _portal.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, "DDMTemplate Name"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "DDMTemplate Description"
			).build(),
			DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, StringPool.BLANK,
			TemplateConstants.LANG_TYPE_FTL,
			_read("ddm-template-browser-sniffer-content.ftl"),
			serviceContext);

		return _ddmTemplateService.getTemplates(
			_group.getCompanyId(), _group.getClassNameId(),
			_portal.getClassNameId(DDMTemplate.class),
			_portal.getClassNameId(JournalArticle.class), 1);
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/dynamic/data/mapping/dependencies/upgrade.v5_3_2/" +
				fileName);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_2." +
			"DDMTemplateBrowserSnifferUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private DDMTemplateService _ddmTemplateService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

}