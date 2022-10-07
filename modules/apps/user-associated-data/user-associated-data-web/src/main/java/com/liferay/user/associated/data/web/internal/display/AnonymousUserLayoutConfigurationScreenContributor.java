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

package com.liferay.user.associated.data.web.internal.display;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.util.PropsUtil;
import com.liferay.user.associated.data.web.internal.configuration.AnonymousUserLayoutConfiguration;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fernando Vilela
 */
@Component(service = PortalSettingsConfigurationScreenContributor.class)
public class AnonymousUserLayoutConfigurationScreenContributor
	implements PortalSettingsConfigurationScreenContributor {

	@Override
	public String getCategoryKey() {
		return "users";
	}

	@Override
	public String getJspPath() {
		return "/portal_settings/users_layout_configuration.jsp";
	}

	@Override
	public String getKey() {
		return "anonymous-user-layout-configuration";
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle, "anonymous-user-layout-configuration-name");
	}

	@Override
	public String getSaveMVCActionCommandName() {
		return "/anonymous-user-layout-configuration";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void setAttributes(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		AnonymousUserLayoutConfiguration anonymousUserLayoutConfiguration =
			null;

		try {
			anonymousUserLayoutConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AnonymousUserLayoutConfiguration.class,
					CompanyThreadLocal.getCompanyId());
		}
		catch (PortalException portalException) {
			ReflectionUtil.throwException(portalException);
		}

		if (Validator.isNotNull(
				anonymousUserLayoutConfiguration.userPublicLayout())) {

			PropsUtil.set(
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED,
				anonymousUserLayoutConfiguration.userPublicLayout());
		}

		if (Validator.isNotNull(
				anonymousUserLayoutConfiguration.userPublicLayout())) {

			PropsUtil.set(
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_AUTO_CREATE,
				anonymousUserLayoutConfiguration.userPublicLayoutAutoCreate());
		}

		if (Validator.isNotNull(
				anonymousUserLayoutConfiguration.userPrivateLayout())) {

			PropsUtil.set(
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED,
				anonymousUserLayoutConfiguration.userPrivateLayout());
		}

		if (Validator.isNotNull(
				anonymousUserLayoutConfiguration.
					userPrivateLayoutAutoCreate())) {

			PropsUtil.set(
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_AUTO_CREATE,
				anonymousUserLayoutConfiguration.userPrivateLayoutAutoCreate());
		}

		httpServletRequest.setAttribute(
			AnonymousUserLayoutConfiguration.class.getName(),
			anonymousUserLayoutConfiguration);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.user.associated.data.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}