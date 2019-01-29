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

package com.liferay.dynamic.data.mapping.form.field.type.file.upload.internal;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=file_upload",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		FileUploadDDMFormFieldTemplateContextContributor.class
	}
)
public class FileUploadDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		Map<String, Object> parameters = new HashMap<>();

		HttpServletRequest request =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (ddmFormFieldRenderingContext.isReadOnly() &&
			Validator.isNotNull(ddmFormFieldRenderingContext.getValue())) {

			JSONObject valueJSONObject = _getValueJSONObject(
				ddmFormFieldRenderingContext.getValue());

			long formInstanceId = ParamUtil.getLong(request, "formInstanceId");

			try {
				DDMFormInstance ddmFormInstance =
					_ddmFormInstanceLocalService.getDDMFormInstance(
						formInstanceId);

				if ((valueJSONObject != null) &&
					(valueJSONObject.length() > 0)) {

					FileEntry fileEntry = _getFileEntry(
						ddmFormInstance.getGroupId(), formInstanceId,
						valueJSONObject);

					parameters.put("fileEntryTitle", fileEntry.getFileName());
					parameters.put(
						"fileEntryURL", _getFileEntryURL(request, fileEntry));
				}
			}
			catch (Exception e) {
				_log.error(e, e);

				throw new RuntimeException();
			}
		}

		parameters.put(
			"lexiconIconsPath",
			_getLexiconIconsPath(ddmFormFieldRenderingContext));

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		parameters.put("portletNamespace", portletDisplay.getNamespace());

		parameters.put(
			"uploadURL", _getUploadURL(request, ddmFormFieldRenderingContext));

		return parameters;
	}

	private FileEntry _getFileEntry(
		long groupId, long formInstanceId, JSONObject valueJSONObject) {

		try {
			Repository repository =
				PortletFileRepositoryUtil.getPortletRepository(
					groupId, "com.liferay.dynamic.data.mapping.form.web");

			Folder folder = PortletFileRepositoryUtil.getPortletFolder(
				repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				String.valueOf(formInstanceId));

			return PortletFileRepositoryUtil.getPortletFileEntry(
				groupId, folder.getFolderId(),
				valueJSONObject.getString("name"));
		}
		catch (PortalException pe) {
			_log.error("Unable to retrieve file entry ", pe);

			return null;
		}
	}

	private Object _getFileEntryURL(
		HttpServletRequest request, FileEntry fileEntry) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletFileRepositoryUtil.getPortletFileEntryURL(
			themeDisplay, fileEntry, StringPool.BLANK, true);
	}

	private String _getLexiconIconsPath(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		HttpServletRequest request =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		StringBundler sb = new StringBundler(3);

		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/lexicon/icons.svg");
		sb.append(StringPool.POUND);

		return sb.toString();
	}

	private String _getUploadURL(
		HttpServletRequest request,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(request);

		PortletURL portletURL = requestBackedPortletURLFactory.createActionURL(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM);

		portletURL.setParameter(
			ActionRequest.ACTION_NAME, "upload_form_attachment");

		portletURL.setParameter(
			"formInstanceId",
			String.valueOf(
				ddmFormFieldRenderingContext.getProperty("formInstanceId")));

		return portletURL.toString();
	}

	private JSONObject _getValueJSONObject(String value) {
		try {
			return _jsonFactory.createJSONObject(value);
		}
		catch (JSONException jsone) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsone, jsone);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileUploadDDMFormFieldTemplateContextContributor.class);

	@Reference
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}