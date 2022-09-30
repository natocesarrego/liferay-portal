package com.liferay.portlet.configuration.css.web.internal.decorator.configuration;

import org.osgi.service.component.annotations.Component;
import com.liferay.configuration.admin.category.ConfigurationCategory;

@Component(service = ConfigurationCategory.class)
public class DecoratorConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return "chatbot";
	}

	@Override
	public String getCategoryKey() {
		return "decorator-portlet";
	}

	@Override
	public String getCategorySection() {
		return "content-and-data";
	}

	private static final String _CATEGORY_ICON = "chatbot";

	private static final String _CATEGORY_KEY = "decorator-portlet";

	private static final String _CATEGORY_SECTION = "content-and-data";

}