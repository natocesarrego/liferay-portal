package com.liferay.portlet.configuration.css.web.internal.decorator.configuration;

import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

@Component(service = ConfigurationCategory.class)
public class DecoratorConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return "cog";
	}

	@Override
	public String getCategoryKey() {
		return "decorator-portlet";
	}

	@Override
	public String getCategorySection() {
		return "other";
	}

}