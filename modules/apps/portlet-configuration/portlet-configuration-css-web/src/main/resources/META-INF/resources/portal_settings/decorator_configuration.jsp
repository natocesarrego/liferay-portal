<%@ include file="/init.jsp" %>

<%
DecoratorConfiguration decoratorConfiguration =
   (DecoratorConfiguration)request.getAttribute(DecoratorConfiguration.class.getName());
%>

<div class="row">
   <div class="col-md-12">
      <br />
         <aui:select label="select-properties-application-decorators" type="text" name="applicationDecorators" required="<%= true %>" value="<%= decoratorConfiguration.applicationDecorators() %>">
        	 	<aui:option label="Barebone" value="barebone" />
         		<aui:option label="Borderless" value="borderless" />
         		<aui:option label="Decorate" value="decorate" />
         </aui:select>
   </div>
</div>