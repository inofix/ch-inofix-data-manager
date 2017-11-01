<%--
    measurement_action.jsp: The action menu of the data-manager's default view.
    
    Created:    2017-09-27 13:54 by Christian Berndt
    Modified:   2017-11-01 23:49 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp"%>

<%
    ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

    Measurement measurement = (Measurement) row.getObject();
    
    String editURL = (String) request.getAttribute("editURL");
    String viewURL = (String) request.getAttribute("viewURL");
    
    editURL = HttpUtil.setParameter(editURL, renderResponse.getNamespace() + "measurementId", measurement.getMeasurementId()); 
    viewURL = HttpUtil.setParameter(viewURL, renderResponse.getNamespace() + "measurementId", measurement.getMeasurementId()); 

    boolean hasUpdatePermission = MeasurementPermission.contains(permissionChecker, measurement,
            MeasurementActionKeys.UPDATE);
    boolean hasViewPermission = MeasurementPermission.contains(permissionChecker, measurement,
            MeasurementActionKeys.VIEW);
    boolean hasDeletePermission = MeasurementPermission.contains(permissionChecker, measurement,
            MeasurementActionKeys.DELETE);
    boolean hasPermissionsPermission = MeasurementPermission.contains(permissionChecker, measurement, 
            MeasurementActionKeys.PERMISSIONS);
%>

<liferay-ui:icon-menu showWhenSingleIcon="true">

    <c:if test="<%=hasViewPermission%>">

        <liferay-ui:icon iconCssClass="icon-eye-open" message="view" 
            url="<%= editURL %>" />

    </c:if>

    <c:if test="<%=hasUpdatePermission%>">

        <liferay-ui:icon iconCssClass="icon-edit" message="edit" 
            url="<%= viewURL %>" />

    </c:if>

    <c:if test="<%=hasDeletePermission%>">

        <portlet:actionURL name="editMeasurement" var="deleteURL">
            <portlet:param name="cmd" value="<%= Constants.DELETE %>"/>
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="measurementId"
                value="<%=String.valueOf(measurement.getMeasurementId())%>" />
        </portlet:actionURL>

        <liferay-ui:icon-delete message="delete" url="<%=deleteURL%>" />

    </c:if>

    <c:if test="<%= hasPermissionsPermission %>">

        <liferay-security:permissionsURL
            modelResource="<%= Measurement.class.getName() %>"
            modelResourceDescription="<%= String.valueOf(measurement.getMeasurementId()) %>"
            resourcePrimKey="<%= String.valueOf(measurement.getMeasurementId()) %>"
            var="permissionsEntryURL"
            windowState="<%= LiferayWindowState.POP_UP.toString() %>" />

        <liferay-ui:icon iconCssClass="icon-cog" message="permissions"
            method="get" url="<%= permissionsEntryURL %>"
            useDialog="<%= true %>" />
    </c:if>

</liferay-ui:icon-menu>
