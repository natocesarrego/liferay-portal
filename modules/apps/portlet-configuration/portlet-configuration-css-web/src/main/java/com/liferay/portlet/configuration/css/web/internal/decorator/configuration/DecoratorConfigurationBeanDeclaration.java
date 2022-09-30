package com.liferay.portlet.configuration.css.web.internal.decorator.configuration;

import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;
import org.osgi.service.component.annotations.Component;

@Component(service = ConfigurationBeanDeclaration.class)
public class DecoratorConfigurationBeanDeclaration
	implements ConfigurationBeanDeclaration{

	@Override
	public Class<?> getConfigurationBeanClass() {
		return DecoratorConfiguration.class;
	}
}
