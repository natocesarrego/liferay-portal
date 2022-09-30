package com.liferay.portlet.configuration.css.web.internal.decorator.configuration;


import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "decorator-portlet", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.portlet.configuration.css.web.internal.decorator.configuration.DecoratorConfiguration",
	localization = "content/Language", name = "decorator-configuration"
)
public interface DecoratorConfiguration {

	public String applicationDecorators();

}
