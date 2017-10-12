<%--
    edit_measurement.jsp: edit a single measurement.

    Created:     2017-09-18 11:17 by Christian Berndt
    Modified:    2017-09-27 14:20 by Christian Berndt
    Version:     1.0.1
--%>

<%@ include file="/init.jsp"%>

<%
    Measurement measurement = (Measurement) request.getAttribute(DataManagerWebKeys.MEASUREMENT);

    String namespace = portletDisplay.getNamespace();

    String title = LanguageUtil.get(request, "new-measurement");

    boolean hasUpdatePermission = false;
    boolean hasViewPermission = false;
    boolean hasDeletePermission = false;
    boolean hasPermissionsPermission = false;

    if (measurement != null) {

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

    <portlet:actionURL name="updateMeasurement" var="updateMeasurementURL">
        <portlet:param name="mvcPath" value="/edit_measurement.jsp" />
    </portlet:actionURL>

    <aui:form method="post" action="<%=updateMeasurementURL%>" name="fm">
    
        <aui:input name="cmd" type="hidden" 
            value="<%= Constants.UPDATE %>"/>
    
        <aui:model-context bean="<%=measurement%>"
            model="<%=Measurement.class%>" />
    
        <div class="lfr-form-content">
        
            <aui:fieldset-group markupView="<%= markupView %>">
 
                <aui:fieldset>
    
                    <aui:input name="backURL" type="hidden"
                        value="<%=backURL%>" />
    
                    <aui:input name="untilDate" type="hidden"
                        disabled="<%=!hasUpdatePermission%>" />
    
                    <aui:input name="redirect" type="hidden"
                        value="<%=redirect%>" />
    
                    <aui:input name="measurementId" type="hidden"
                        disabled="<%=!hasUpdatePermission%>" />
    
                    <aui:input name="data"
                        disabled="<%=!hasUpdatePermission%>"
                        helpMessage="data-help" />
    
                </aui:fieldset>
        
            </aui:fieldset-group>
        </div>
                           
        <aui:button-row>
            <aui:button cssClass="btn-lg" disabled="<%= !hasUpdatePermission %>" type="submit" />           
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
        
    </aui:form>
</div>

