<%--
    edit_measurement.jsp: edit a single measurement.

    Created:     2017-09-18 11:17 by Christian Berndt
    Modified:    2017-11-21 12:36 by Christian Berndt
    Version:     1.0.9
--%>

<%@ include file="/init.jsp"%>

<%
    String cmd = Constants.ADD; 

    Measurement measurement = (Measurement) request.getAttribute(DataManagerWebKeys.MEASUREMENT);

    String namespace = portletDisplay.getNamespace();

    String title = LanguageUtil.get(request, "new-measurement");

    boolean hasUpdatePermission = false;
    boolean hasViewPermission = false;
    boolean hasDeletePermission = false;
    boolean hasPermissionsPermission = false;

    if (measurement != null) {
        
        cmd = Constants.UPDATE;

        title = LanguageUtil.format(request, "edit-measurement-x",
                String.valueOf(measurement.getMeasurementId()));

        hasUpdatePermission = MeasurementPermission.contains(permissionChecker, measurement,
                MeasurementActionKeys.UPDATE);
        hasViewPermission = MeasurementPermission.contains(permissionChecker, measurement,
                MeasurementActionKeys.VIEW);
        hasDeletePermission = MeasurementPermission.contains(permissionChecker, measurement,
                MeasurementActionKeys.DELETE);
        hasPermissionsPermission = MeasurementPermission.contains(permissionChecker, measurement,
                MeasurementActionKeys.PERMISSIONS);

    } else {

        measurement = MeasurementServiceUtil.createMeasurement();
        hasUpdatePermission = true;

    }
    String redirect = ParamUtil.getString(request, "redirect");

    String backURL = ParamUtil.getString(request, "backURL", redirect);

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(redirect);

    renderResponse.setTitle(title);

    request.setAttribute("showTitle", "true"); // used by inofix-theme
%>

<div class="container-fluid-1280">
    
    <portlet:actionURL name="editMeasurement" var="updateMeasurementURL">
        <portlet:param name="mvcRenderCommandName" value="editMeasurement" />
    </portlet:actionURL>

    <aui:form method="post" action="<%=updateMeasurementURL%>" name="fm">
    
        <aui:input name="cmd" type="hidden" 
            value="<%= cmd  %>"/>
    
        <aui:model-context bean="<%=measurement%>"
            model="<%=Measurement.class%>" />
    
        <div class="lfr-form-content">
        
            <aui:fieldset-group markupView="<%= markupView %>">
 
                <aui:fieldset>
    
                    <aui:input name="backURL" type="hidden"
                        value="<%=backURL%>" />
    
                    <aui:input name="id" type=""
                        value="<%= measurement.getId() %>" />
    
                    <aui:input name="name" type=""
                        value="<%= measurement.getName() %>" />
    
                    <aui:input name="timestamp" type=""
                        value="<%= measurement.getTimestamp() %>" />
    
                    <aui:input name="measurementId" type="hidden"
                        disabled="<%=!hasUpdatePermission%>" />
                    
                    <c:choose>
                        <c:when test="<%= fields.size() > 0 %>">
                        <%
                            String data = measurement.getData();
                            Object value = null;
                            
                            JSONObject dataObj = JSONFactoryUtil.createJSONObject(data); 
                            
                            for (String field : fields) {
                                
                                if (dataObj != null) {
                                    value = dataObj.get(field);
                                }
                                
                                boolean required = false;
                                
                                if (requiredFields != null) {
                                    
                                    required = ArrayUtil.contains(requiredFields, field);
                                    
                                }
                        %>
                            <aui:input name="<%= field %>" required="<%= required %>" type="text" value="<%= value %>" />                       
                        <%
                            }
                        %>
                        </c:when>
                        <c:otherwise>
                            <aui:input name="data"
                                disabled="<%=!hasUpdatePermission%>"
                                helpMessage="data-help" />                        
                        </c:otherwise>
                    </c:choose>                   
    
                </aui:fieldset>
        
            </aui:fieldset-group>
        </div>
                           
        <aui:button-row>
            <aui:button cssClass="btn-lg" disabled="<%= !hasUpdatePermission %>" type="submit" />           
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
        
    </aui:form>
</div>
