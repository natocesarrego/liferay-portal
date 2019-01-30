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

package com.liferay.dynamic.data.mapping.form.web.internal.upload;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.constants.DDMFormConstants;
import com.liferay.dynamic.data.mapping.form.web.internal.security.permission.resource.DDMFormInstancePermission;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.user.associated.data.util.UADAnonymizerHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = FormsUploadFileEntryHandler.class)
public class FormsUploadFileEntryHandler implements UploadFileEntryHandler {

	@Override
	public FileEntry upload(UploadPortletRequest uploadPortletRequest)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)uploadPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long formInstanceId = ParamUtil.getLong(
			uploadPortletRequest, "formInstanceId");

		File file = null;

		try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
				"file")) {

			String fileName = uploadPortletRequest.getFileName("file");

			file = FileUtil.createTempFile(inputStream);

			String contentType = MimeTypesUtil.getContentType(file, fileName);

			return addFileEntry(
				formInstanceId, fileName, contentType, file, themeDisplay);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	protected FileEntry addFileEntry(
			long formInstanceId, String fileName, String contentType, File file,
			ThemeDisplay themeDisplay)
		throws PortalException {

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceLocalService.getDDMFormInstance(formInstanceId);

		if (!DDMFormInstancePermission.contains(
				themeDisplay.getPermissionChecker(), ddmFormInstance,
				DDMActionKeys.ADD_FORM_INSTANCE_RECORD)) {

			throw new PrincipalException();
		}

		long userId = 0;

		if (themeDisplay.isSignedIn()) {
			userId = themeDisplay.getUserId();
		}
		else {
			try {
				User user = _uadAnonymizerHelper.getAnonymousUser(
					themeDisplay.getDefaultUserId());

				userId = user.getUserId();
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn("Could not create anonymous user");
				}

				userId = _userLocalService.getDefaultUserId(
					themeDisplay.getCompanyId());
			}
		}

		Folder folder = addFolder(ddmFormInstance, themeDisplay);

		String uniqueFileName = PortletFileRepositoryUtil.getUniqueFileName(
			ddmFormInstance.getGroupId(), folder.getFolderId(), fileName);

		return PortletFileRepositoryUtil.addPortletFileEntry(
			ddmFormInstance.getGroupId(), userId,
			DDMFormInstance.class.getName(), 0, DDMFormConstants.SERVICE_NAME,
			folder.getFolderId(), file, uniqueFileName, contentType, true);
	}

	protected Folder addFolder(
			DDMFormInstance ddmFormInstance, ThemeDisplay themeDisplay)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			ddmFormInstance.getGroupId(), DDMFormConstants.SERVICE_NAME,
			serviceContext);

		return PortletFileRepositoryUtil.addPortletFolder(
			ddmFormInstance.getUserId(), repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			String.valueOf(ddmFormInstance.getFormInstanceId()),
			serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FormsUploadFileEntryHandler.class);

	@Reference
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Reference
	private UADAnonymizerHelper _uadAnonymizerHelper;

	@Reference
	private UserLocalService _userLocalService;

}