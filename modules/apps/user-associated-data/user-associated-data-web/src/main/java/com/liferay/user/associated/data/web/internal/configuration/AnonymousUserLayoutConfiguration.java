package com.liferay.user.associated.data.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Fernando Vilela
 */
@ExtendedObjectClassDefinition(
	category = "users", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.user.associated.data.web.internal.configuration.AnonymousUserLayoutConfiguration",
	localization = "content/Language",
	name = "anonymous-user-layout-configuration-name"
)
public interface AnonymousUserLayoutConfiguration {

	public String userPublicLayout();

	public String userPublicLayoutAutoCreate();

	public String userPrivateLayout();

	public String userPrivateLayoutAutoCreate();

}

