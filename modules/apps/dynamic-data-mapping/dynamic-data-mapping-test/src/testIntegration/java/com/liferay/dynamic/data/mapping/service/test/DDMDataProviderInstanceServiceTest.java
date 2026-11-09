/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mateus Xavier
 */
@RunWith(Arquillian.class)
public class DDMDataProviderInstanceServiceTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				new String[] {ActionKeys.VIEW}, null));

		_ddmDataProviderInstance =
			_ddmDataProviderInstanceLocalService.addDataProviderInstance(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(), null,
				DDMFormValuesTestUtil.createDDMFormValuesWithRandomValues(
					DDMFormTestUtil.createDDMForm("password", "username")),
				"rest", serviceContext);
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstanceWithUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addGroupAdminUser(group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertGetDataProviderInstance(true);
		}
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstanceWithoutUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertGetDataProviderInstance(false);
		}

		_assertDDMDataProviderInstance(
			_ddmDataProviderInstanceLocalService.getDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()),
			true);
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstances() throws Exception {
		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				_ddmDataProviderInstanceService.getDataProviderInstances(
					group.getCompanyId(), new long[] {group.getGroupId()},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}
	}

	@Test
	@TestInfo("LPD-110121")
	public void testSearch() throws Exception {
		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				_ddmDataProviderInstanceService.search(
					group.getCompanyId(), new long[] {group.getGroupId()}, null,
					null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
		}
	}

	private void _assertDDMDataProviderInstance(
			DDMDataProviderInstance ddmDataProviderInstance,
			boolean expectedAuthenticationData)
		throws Exception {

		JSONObject definitionJSONObject = JSONFactoryUtil.createJSONObject(
			ddmDataProviderInstance.getDefinition());

		Set<String> names = JSONUtil.toStringSet(
			definitionJSONObject.getJSONArray("fieldValues"), "name");

		Assert.assertEquals(
			expectedAuthenticationData, names.contains("password"));
		Assert.assertEquals(
			expectedAuthenticationData, names.contains("username"));
	}

	private void _assertDDMDataProviderInstances(
			List<DDMDataProviderInstance> ddmDataProviderInstances)
		throws Exception {

		AssertUtils.assertEquals(
			Collections.singletonList(_ddmDataProviderInstance),
			ddmDataProviderInstances);

		for (DDMDataProviderInstance ddmDataProviderInstance :
				ddmDataProviderInstances) {

			_assertDDMDataProviderInstance(ddmDataProviderInstance, false);
		}
	}

	private void _assertGetDataProviderInstance(
			boolean expectedAuthenticationData)
		throws Exception {

		_assertDDMDataProviderInstance(
			_ddmDataProviderInstanceService.fetchDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()),
			expectedAuthenticationData);
		_assertDDMDataProviderInstance(
			_ddmDataProviderInstanceService.fetchDataProviderInstanceByUuid(
				_ddmDataProviderInstance.getUuid()),
			expectedAuthenticationData);
		_assertDDMDataProviderInstance(
			_ddmDataProviderInstanceService.getDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()),
			expectedAuthenticationData);
		_assertDDMDataProviderInstance(
			_ddmDataProviderInstanceService.getDataProviderInstanceByUuid(
				_ddmDataProviderInstance.getUuid()),
			expectedAuthenticationData);
	}

	@DeleteAfterTestRun
	private DDMDataProviderInstance _ddmDataProviderInstance;

	@Inject
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Inject
	private DDMDataProviderInstanceService _ddmDataProviderInstanceService;

}