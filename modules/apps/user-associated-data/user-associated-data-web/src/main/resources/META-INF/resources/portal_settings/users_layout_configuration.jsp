<%--
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
AnonymousUserLayoutConfiguration anonymousUserLayoutConfiguration = (AnonymousUserLayoutConfiguration)request.getAttribute(AnonymousUserLayoutConfiguration.class.getName());
%>

<div class="row">
	<div class="col-md-12">
		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPublicLayout() %>" inlineLabel="right" label="enable-public-layouts" labelCssClass="simple-toggle-switch" name="userPublicLayout" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPublicLayout() %>" />

		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPublicLayoutAutoCreate() %>" inlineLabel="right" label="public-layouts-auto-create" labelCssClass="simple-toggle-switch" name="userPublicLayoutAutoCreate" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPublicLayoutAutoCreate() %>" />

		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPrivateLayout() %>" inlineLabel="right" label="enable-private-layouts" labelCssClass="simple-toggle-switch" name="userPrivateLayout" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPrivateLayout() %>" />

		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPrivateLayoutAutoCreate() %>" inlineLabel="right" label="private-layouts-auto-create" labelCssClass="simple-toggle-switch" name="userPrivateLayoutAutoCreate" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPrivateLayoutAutoCreate() %>" />
	</div>
</div>