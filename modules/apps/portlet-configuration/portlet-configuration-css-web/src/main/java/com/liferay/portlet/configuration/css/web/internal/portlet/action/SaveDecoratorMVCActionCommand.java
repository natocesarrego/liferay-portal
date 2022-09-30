package com.liferay.portlet.configuration.css.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

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

		long companyId = themeDisplay.getCompanyId();

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin(companyId)) {
			SessionErrors.add(actionRequest, PrincipalException.class);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		PropsUtil.set(
			PropsKeys.DEFAULT_PORTLET_DECORATOR_ID,
			ParamUtil.getString(actionRequest, "applicationDecorators"));

		PropsValues.DEFAULT_PORTLET_DECORATOR_ID =
			PropsUtil.get(PropsKeys.DEFAULT_PORTLET_DECORATOR_ID);

		PortalPreferences portalPreferences =
			_portalPreferencesLocalService.fetchPortalPreferences(
				companyId,
				PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		com.liferay.portal.kernel.portlet.PortalPreferences
			newPortalPreferences =
				_portalPreferencesValueLocalService.getPortalPreferences(
					portalPreferences, false);

		newPortalPreferences.setValue(
			null, "applicationDecorators",
			ParamUtil.getString(actionRequest, "applicationDecorators"));

		_portalPreferencesLocalService.updatePreferences(
			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			newPortalPreferences);
	}

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@Reference
	private PortalPreferenceValueLocalService
		_portalPreferencesValueLocalService;

}