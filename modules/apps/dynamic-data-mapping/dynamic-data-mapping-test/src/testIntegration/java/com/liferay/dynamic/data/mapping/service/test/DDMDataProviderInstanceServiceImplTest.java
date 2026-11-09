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
import com.liferay.portal.kernel.model.Group;
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
import com.liferay.portal.kernel.test.util.GroupTestUtil;
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
public class DDMDataProviderInstanceServiceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				new String[] {ActionKeys.VIEW}, null));

		_ddmDataProviderInstance =
			_ddmDataProviderInstanceLocalService.addDataProviderInstance(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(), null,
				DDMFormValuesTestUtil.createDDMFormValuesWithRandomValues(
					DDMFormTestUtil.createDDMForm("password", "username")),
				"rest", serviceContext);
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstanceWithUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addGroupAdminUser(_group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertGetDataProviderInstance(true);
		}
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstanceWithoutUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertGetDataProviderInstance(false);
		}

		_assertDDMDataProviderInstance(
			_ddmDataProviderInstanceLocalService.getDDMDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()),
			true);
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstancesWithUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addGroupAdminUser(_group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				_ddmDataProviderInstanceService.getDataProviderInstances(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				true);
		}
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstancesWithoutUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				_ddmDataProviderInstanceService.getDataProviderInstances(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				false);
		}
	}

	@Test
	@TestInfo("LPD-110121")
	public void testSearchWithUpdatePermission() throws Exception {
		User user = UserTestUtil.addGroupAdminUser(_group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				_ddmDataProviderInstanceService.search(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					null, null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null),
				true);
			_assertDDMDataProviderInstances(
				_ddmDataProviderInstanceService.search(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				true);
		}
	}

	@Test
	@TestInfo("LPD-110121")
	public void testSearchWithoutUpdatePermission() throws Exception {
		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				_ddmDataProviderInstanceService.search(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					null, null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null),
				false);
			_assertDDMDataProviderInstances(
				_ddmDataProviderInstanceService.search(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				false);
		}
	}

	private void _assertDDMDataProviderInstance(
			DDMDataProviderInstance ddmDataProviderInstance, boolean expected)
		throws Exception {

		JSONObject definitionJSONObject = JSONFactoryUtil.createJSONObject(
			ddmDataProviderInstance.getDefinition());

		Set<String> names = JSONUtil.toStringSet(
			definitionJSONObject.getJSONArray("fieldValues"), "name");

		Assert.assertEquals(expected, names.contains("password"));
		Assert.assertEquals(expected, names.contains("username"));
	}

	private void _assertDDMDataProviderInstances(
			List<DDMDataProviderInstance> ddmDataProviderInstances,
			boolean expected)
		throws Exception {

		AssertUtils.assertEquals(
			Collections.singletonList(_ddmDataProviderInstance),
			ddmDataProviderInstances);

		for (DDMDataProviderInstance ddmDataProviderInstance :
				ddmDataProviderInstances) {

			_assertDDMDataProviderInstance(ddmDataProviderInstance, expected);
		}
	}

	private void _assertGetDataProviderInstance(boolean expected)
		throws Exception {

		_assertDDMDataProviderInstance(
			_ddmDataProviderInstanceService.fetchDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()),
			expected);
		_assertDDMDataProviderInstance(
			_ddmDataProviderInstanceService.getDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()),
			expected);
	}

	@DeleteAfterTestRun
	private DDMDataProviderInstance _ddmDataProviderInstance;

	@Inject
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Inject
	private DDMDataProviderInstanceService _ddmDataProviderInstanceService;

	@DeleteAfterTestRun
	private Group _group;

}