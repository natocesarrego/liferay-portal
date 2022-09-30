<%@ include file="/init.jsp" %>

<%
DecoratorConfiguration decoratorConfiguration =
   (DecoratorConfiguration)request.getAttribute(DecoratorConfiguration.class.getName());
%>


<div class="row">
   <div class="col-md-12">
      <br />
      <aui:input label="select-properties-application-decorators" type="text" name="applicationDecorators" value="<%= decoratorConfiguration.applicationDecorators() %>" />
   </div>
</div>