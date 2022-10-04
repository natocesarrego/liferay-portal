package com.liferay.user.associated.data.web.internal.portlet.action;

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
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.user.associated.data.web.internal.configuration.AnonymousUserLayoutConfiguration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/anonymous-user-layout-configuration"
	},
	service = MVCActionCommand.class
)
public class SaveAnonymousUserLayoutConfiguration extends BaseMVCActionCommand {


		@Override
		protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
   throws Exception {
			ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();

			if (!permissionChecker.isCompanyAdmin(themeDisplay.getCompanyId())) {
				SessionErrors.add(actionRequest, PrincipalException.class);

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");

				return;
			}

			_configurationProvider.saveGroupConfiguration(
				AnonymousUserLayoutConfiguration.class, themeDisplay.getSiteGroupId(),
				HashMapDictionaryBuilder.<String, Object>put(
					"userPublicLayout",
					ParamUtil.getString(actionRequest, "userPublicLayout"))
					.put("userPrivateLayout", ParamUtil.getString(actionRequest,"userPrivateLayout")).build());

			PropsUtil.set(
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED,
				ParamUtil.getString(actionRequest, "userPublicLayout"));

			PropsValues.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED = Boolean.parseBoolean(
				PropsUtil.get(PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED));


			PropsUtil.set(
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED,
				ParamUtil.getString(actionRequest, "userPrivateLayout"));

			PropsValues.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED = Boolean.parseBoolean(
				PropsUtil.get(PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED));
		}


	@Reference
	private ConfigurationProvider _configurationProvider;
}
