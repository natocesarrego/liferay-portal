package com.liferay.portlet.configuration.css.web.internal.decorator.configuration;

import org.osgi.service.component.annotations.Component;
import com.liferay.configuration.admin.category.ConfigurationCategory;

@Component(service = ConfigurationCategory.class)
public class DecoratorConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return _CATEGORY_ICON;
	}

	@Override
	public String getCategoryKey() {
		return _CATEGORY_KEY;
	}

	@Override
	public String getCategorySection() {
		return _CATEGORY_SECTION;
	}

	private static final String _CATEGORY_ICON = "cog";

	private static final String _CATEGORY_KEY = "decorator-portlet";

	private static final String _CATEGORY_SECTION = "other";

}