<%@ page
	import="com.liferay.user.associated.data.web.internal.configuration.UserLayoutConfiguration" %><%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<%
UserLayoutConfiguration userLayoutConfiguration = (UserLayoutConfiguration)request.getAttribute(
	UserLayoutConfiguration.class.getName());
%>

<div class="row">
	<div class="col-md-12">
		<aui:input checked="<%= GetterUtil.getBoolean(userLayoutConfiguration.userPublicLayout()) %>" inlineLabel="right" label="enable-public-layouts" labelCssClass="simple-toggle-switch" name="userPublicLayout" type="toggle-switch" value="<%= userLayoutConfiguration.userPublicLayout() %>" />

		<aui:input checked="<%= GetterUtil.getBoolean(userLayoutConfiguration.userPublicLayoutAutoCreate()) %>" inlineLabel="right" label="public-layouts-auto-create" labelCssClass="simple-toggle-switch" name="userPublicLayoutAutoCreate" type="toggle-switch" value="<%= userLayoutConfiguration.userPublicLayoutAutoCreate() %>" />

		<aui:input checked="<%= GetterUtil.getBoolean(userLayoutConfiguration.userPrivateLayout()) %>" inlineLabel="right" label="enable-private-layouts" labelCssClass="simple-toggle-switch" name="userPrivateLayout" type="toggle-switch" value="<%= userLayoutConfiguration.userPrivateLayout() %>" />

		<aui:input checked="<%= GetterUtil.getBoolean(userLayoutConfiguration.userPrivateLayoutAutoCreate()) %>" inlineLabel="right" label="private-layouts-auto-create" labelCssClass="simple-toggle-switch" name="userPrivateLayoutAutoCreate" type="toggle-switch" value="<%= userLayoutConfiguration.userPrivateLayoutAutoCreate() %>" />
	</div>
</div>