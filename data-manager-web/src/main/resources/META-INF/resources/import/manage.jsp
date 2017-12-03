<%--
    manage.jsp: manually trigger imports from configured data-urls
    
    Created:    2017-12-01 19:36 by Christian Berndt
    Modified:   2017-12-01 19:36 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<%
    boolean hasExportImportPermission = DataManagerPortletPermission.contains(permissionChecker, scopeGroupId,
            MeasurementActionKeys.EXPORT_IMPORT_MEASUREMENTS);
%>

<div class="container-fluid-1280">

    <portlet:actionURL name="importMeasurements"
        var="importMeasurementsURL">
        <portlet:param name="<%=Constants.CMD%>"
            value="importDataFromURL" />
        <portlet:param name="tabs1" value="export-import" />
        <portlet:param name="tabs2" value="manage" />
    </portlet:actionURL>

    <aui:form action="<%=importMeasurementsURL%>" method="post"
        name="fm1">

        <portlet:renderURL var="portletURL">
            <portlet:param name="tabs1" value="export-import" />
            <portlet:param name="tabs2" value="manage" />
        </portlet:renderURL>

        <aui:input name="redirect" type="hidden"
            value="<%=portletURL.toString()%>" />
            
        <aui:input name="groupId" type="hidden"
            value="<%=scopeGroupId%>" />
            
        <div class="lfr-form-content">
        
            <aui:fieldset-group markupView="<%= markupView %>">
 
                <aui:fieldset>
        
                    <aui:input name="dataURL" type="hidden"
                        value="<%=dataURL%>" />
                        
                    <aui:input name="dataURL" disabled="<%=true%>"
                        helpMessage="data-url-help" value="<%=dataURL%>" />
                
                </aui:fieldset>
                
            </aui:fieldset-group>

            <%
                boolean disabled = Validator.isNull(dataURL); 
            %>

            <aui:button-row>
                <aui:button cssClass="btn-lg" disabled="<%= !hasExportImportPermission || disabled %>" type="submit"
                    value="import-measurements" />
            </aui:button-row>
        </div>
    </aui:form>

    <%-- 
    <aui:button-row>
        <c:if test="<%=hasExportImportPermission%>">
            <liferay-ui:icon-menu>
                <liferay-ui:icon cssClass="btn btn-primary"
                    message="import-measurements"
                    url="<%=importMeasurementsURL%>" />
            </liferay-ui:icon-menu>
        </c:if>
        <c:if test="<%=!hasExportImportPermission%>">
            <aui:button cssClass="btn btn-primary" disabled="<%=true%>"
                value="import-measurements" />
        </c:if>
    </aui:button-row>
    --%>

</div>
