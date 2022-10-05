<%@ include file="/init.jsp" %>
<%
	AnonymousUserLayoutConfiguration anonymousUserLayoutConfiguration =
		(AnonymousUserLayoutConfiguration)request.getAttribute(AnonymousUserLayoutConfiguration.class.getName());
%>


<div class="row">
	<div class="col-md-12">
		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPublicLayout() %>" inlineLabel="right" label="enable-public-layouts"  labelCssClass="simple-toggle-switch" name="userPublicLayout" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPublicLayout() %>" />

		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPublicLayoutAutoCreate() %>" inlineLabel="right" label="public-layouts-auto-create"  labelCssClass="simple-toggle-switch" name="userPublicLayoutAutoCreate" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPublicLayoutAutoCreate() %>" />

		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPrivateLayout() %>" inlineLabel="right" label="enable-private-layouts"  labelCssClass="simple-toggle-switch" name="userPrivateLayout" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPrivateLayout() %>" />

		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPrivateLayoutAutoCreate() %>" inlineLabel="right" label="private-layouts-auto-create"  labelCssClass="simple-toggle-switch" name="userPrivateLayoutAutoCreate" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPrivateLayoutAutoCreate() %>" />

	</div>
</div>