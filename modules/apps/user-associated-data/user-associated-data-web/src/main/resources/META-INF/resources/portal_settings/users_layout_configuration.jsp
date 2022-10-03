
<%@ include file="/init.jsp" %>

<%
	AnonymousUserConfiguration anonymoususerConfiguration	 =
		(AnonymousUserConfiguration)request.getAttribute(AnonymousUserConfiguration.class.getName());
%>


<div class="row">
	<div class="col-md-12">
		<br />
		<aui:input label="select-properties-application-decorators" type="text" name="applicationDecorators" value="<%= anonymoususerConfiguration.userId()  %>" />
	</div>
</div>