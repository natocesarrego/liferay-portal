package com.liferay.portlet.configuration.css.web.internal.display.context;

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
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.configuration.css.web.internal.constants.PortletConfigurationCSSPortletKeys;
import com.liferay.portlet.configuration.css.web.internal.decorator.configuration.DecoratorConfiguration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.ResourceBundle;

@Component(service = PortalSettingsConfigurationScreenContributor.class)
public class DecoratorConfigurationScreenContributor implements
	PortalSettingsConfigurationScreenContributor {
	@Override
	public String getCategoryKey() {
		return"decorator-portlet";
	}

	@Override
	public String getJspPath() {
		return "/portal_settings/decorator_configuration.jsp";
	}

	@Override
	public String getKey() {
		return "decorator-configuration";
	}

	@Override
	public String getSaveMVCActionCommandName() {
		return "/save_decorator/save_company_configuration";
	}

	@Override
	public String getName(Locale locale){
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, "decorator-configuration");
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}


	@Override
	public void setAttributes(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		DecoratorConfiguration decoratorConfiguration = null;

		try {
			decoratorConfiguration =
				_configurationProvider.getCompanyConfiguration(
					DecoratorConfiguration.class,
					CompanyThreadLocal.getCompanyId());

		}
		catch (PortalException portalException) {
			ReflectionUtil.throwException(portalException);
		}

		if (Validator.isNotNull(
			decoratorConfiguration.applicationDecorators())) {


			PropsUtil.set(
				PropsKeys.DEFAULT_PORTLET_DECORATOR_ID,
				decoratorConfiguration.applicationDecorators());

			PropsUtil.set(PropsValues.DEFAULT_PORTLET_DECORATOR_ID
				,PropsUtil.get(PropsKeys.DEFAULT_PORTLET_DECORATOR_ID));

		}

		httpServletRequest.setAttribute(
			DecoratorConfiguration.class.getName(),
			decoratorConfiguration);

	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portlet.configuration.css.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;
}
