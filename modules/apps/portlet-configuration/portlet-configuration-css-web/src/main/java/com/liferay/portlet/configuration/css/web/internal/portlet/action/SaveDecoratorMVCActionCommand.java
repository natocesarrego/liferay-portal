package com.liferay.portlet.configuration.css.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.configuration.css.web.internal.decorator.configuration.DecoratorConfiguration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/save_decorator/save_company_configuration"
	},
	service = MVCActionCommand.class
)
public class SaveDecoratorMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin(themeDisplay.getCompanyId())) {
			SessionErrors.add(actionRequest, PrincipalException.class);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		_configurationProvider.saveCompanyConfiguration(
			DecoratorConfiguration.class, themeDisplay.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"applicationDecorators",
				ParamUtil.getString(actionRequest, "applicationDecorators")
			).build());

		PropsUtil.set(
			PropsKeys.DEFAULT_PORTLET_DECORATOR_ID,
			ParamUtil.getString(actionRequest, "applicationDecorators"));

		PropsUtil.set(PropsValues.DEFAULT_PORTLET_DECORATOR_ID,
			PropsUtil.get(PropsKeys.DEFAULT_PORTLET_DECORATOR_ID));
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}
