<%--
    delete_measurements.jsp: Delete all measurements of this group
    
    Created:    2017-11-03 23:43 by Christian Berndt
    Modified:   2017-12-12 17:31 by Christian Berndt
    Version:    1.0.2
--%>

<%@ include file="/init.jsp"%>

<style>
<!--
    .btn-danger a {
        color: white;
    }
-->
</style>

<%
    boolean hasDeletePermission = DataManagerPortletPermission.contains(permissionChecker, scopeGroupId,
            MeasurementActionKeys.DELETE_GROUP_MEASUREMENTS);
%>

<div class="container-fluid-1280">

    <portlet:actionURL name="editMeasurement"
        var="deleteGroupMeasurementsURL">
        <portlet:param name="<%=Constants.CMD%>"
            value="deleteGroupMeasurements" />
        <portlet:param name="mvcPath" value="/view.jsp" />
        <portlet:param name="tabs1" value="export-import" />
        <portlet:param name="tabs2" value="manage" />
    </portlet:actionURL>

    <aui:button-row>
        <c:if test="<%=hasDeletePermission%>">
            <liferay-ui:icon-menu>
                <liferay-ui:icon-delete cssClass="btn btn-danger"
                    message="delete-group-measurements"
                    url="<%=deleteGroupMeasurementsURL%>" />
            </liferay-ui:icon-menu>
        </c:if>
        <c:if test="<%=!hasDeletePermission%>">
            <aui:button cssClass="btn-danger" disabled="<%=true%>"
                value="delete-group-measurements" />
        </c:if>
    </aui:button-row>

    <div>Afterwards run "Reindex all search indexes" from the
        Server Configuration</div>
</div>
