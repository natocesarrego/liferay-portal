<%@ include file="/init.jsp" %>
<%
	AnonymousUserLayoutConfiguration anonymousUserLayoutConfiguration =
		(AnonymousUserLayoutConfiguration)request.getAttribute(AnonymousUserLayoutConfiguration.class.getName());
%>


<div class="row">
	<div class="col-md-12">
		<aui:input checked="<%= anonymousUserLayoutConfiguration.userPublicLayout() %>" inlineLabel="right" label="enable-public-layouts"  labelCssClass="simple-toggle-switch" name="userPublicLayout" type="toggle-switch" value="<%= anonymousUserLayoutConfiguration.userPublicLayout() %>" />
	</div>
</div>

