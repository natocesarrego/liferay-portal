/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.model.Entry;
import com.liferay.portal.reports.engine.console.service.DefinitionLocalService;
import com.liferay.portal.reports.engine.console.service.EntryLocalService;
import com.liferay.portal.reports.engine.console.service.test.EntryServiceTest;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceServingPortlet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubens Soares
 */
@RunWith(Arquillian.class)
@Sync
public class AdminPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		String fileName = "reports_admin_template_source_sample_list_type";

		try (InputStream inputStream =
				EntryServiceTest.class.getResourceAsStream(
					"dependencies/" + fileName + ".jrxml")) {

			_definition = _definitionLocalService.addDefinition(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(), null, 0, null, fileName,
				inputStream, ServiceContextTestUtil.getServiceContext());
		}
	}

	@Test
	public void testServeResourceWithEntryFileName() throws Exception {
		Entry publicEntry = _addEntry(new String[] {ActionKeys.VIEW});

		String[] attachmentsFileNames = publicEntry.getAttachmentsFileNames();

		Assert.assertArrayEquals(
			FileUtil.getBytes(
				_store.getFileAsStream(
					publicEntry.getCompanyId(), CompanyConstants.SYSTEM,
					attachmentsFileNames[0], StringPool.BLANK)),
			_serveResource(publicEntry.getEntryId(), attachmentsFileNames[0]));
	}

	@Test
	public void testServeResourceWithFileNameOfAnotherEntry() throws Exception {
		Entry privateEntry = _addEntry(null);

		String[] attachmentsFileNames = privateEntry.getAttachmentsFileNames();

		Entry publicEntry = _addEntry(new String[] {ActionKeys.VIEW});

		Throwable throwable = _assertServeResourceCause(
			NoSuchFileException.class, publicEntry.getEntryId(),
			attachmentsFileNames[0]);

		Assert.assertEquals(
			StringBundler.concat(
				"Entry ", publicEntry.getEntryId(),
				" does not have attachment ", attachmentsFileNames[0]),
			throwable.getMessage());
	}

	@Test
	public void testServeResourceWithoutViewPermission() throws Exception {
		Entry privateEntry = _addEntry(null);

		String[] attachmentsFileNames = privateEntry.getAttachmentsFileNames();

		_assertServeResourceCause(
			PrincipalException.MustHavePermission.class,
			privateEntry.getEntryId(), attachmentsFileNames[0]);
	}

	private Entry _addEntry(String[] guestPermissions) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				new String[] {
					ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.VIEW
				},
				guestPermissions, Entry.class.getName()));

		return _entryLocalService.addEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_definition.getDefinitionId(), "txt", false, null, null, false,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, RandomTestUtil.randomString(),
			StringPool.BLANK, serviceContext);
	}

	private Throwable _assertServeResourceCause(
		Class<?> exceptionClass, long entryId, String fileName) {

		PortletException portletException = Assert.assertThrows(
			PortletException.class, () -> _serveResource(entryId, fileName));

		Throwable throwable = portletException.getCause();

		Assert.assertEquals(exceptionClass, throwable.getClass());

		return throwable;
	}

	private byte[] _serveResource(long entryId, String fileName)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(
				_userLocalService.getGuestUser(
					TestPropsValues.getCompanyId())));

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter(
			"entryId", String.valueOf(entryId));
		mockLiferayResourceRequest.setParameter("fileName", fileName);
		mockLiferayResourceRequest.setResourceID("download");

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		ResourceServingPortlet resourceServingPortlet =
			(ResourceServingPortlet)_portlet;

		resourceServingPortlet.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return byteArrayOutputStream.toByteArray();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Definition _definition;

	@Inject
	private DefinitionLocalService _definitionLocalService;

	@Inject
	private EntryLocalService _entryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.portal.reports.engine.console.web.internal.admin.portlet.AdminPortlet"
	)
	private Portlet _portlet;

	@Inject(filter = "default=true")
	private Store _store;

	@Inject
	private UserLocalService _userLocalService;

}