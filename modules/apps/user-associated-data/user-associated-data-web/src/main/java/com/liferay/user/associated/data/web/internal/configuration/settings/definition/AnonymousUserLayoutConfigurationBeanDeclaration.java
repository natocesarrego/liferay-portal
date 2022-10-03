package com.liferay.user.associated.data.web.internal.configuration.settings.definition;

import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;
import com.liferay.user.associated.data.web.internal.configuration.AnonymousUserLayoutConfiguration;

import org.osgi.service.component.annotations.Component;

@Component(service = ConfigurationBeanDeclaration.class)
public class AnonymousUserLayoutConfigurationBeanDeclaration implements ConfigurationBeanDeclaration {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return AnonymousUserLayoutConfiguration.class;
	}
}