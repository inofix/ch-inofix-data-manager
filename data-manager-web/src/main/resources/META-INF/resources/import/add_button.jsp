<%--
    add_button.jsp: create a new import process
    
    Created:    2017-06-08 00:32 by Christian Berndt
    Modified:   2017-11-13 20:19 by Christian Berndt
    Version:    1.0.4
--%>

<%@ include file="/init.jsp"%>

<%    
    boolean hasImportPermission = DataManagerPortletPermission.contains(permissionChecker, scopeGroupId,
            MeasurementActionKeys.IMPORT_MEASUREMENTS);
%>

<liferay-frontend:add-menu>

    <portlet:renderURL var="addImportProcessURL">
        <portlet:param name="<%= Constants.CMD %>" value="<%= Constants.IMPORT %>" />
        <portlet:param name="mvcRenderCommandName" value="importMeasurements" />
        <portlet:param name="tabs1" value="export-import" />
    </portlet:renderURL>

    <c:if test="<%= hasImportPermission %>">
        <liferay-frontend:add-menu-item title='<%= LanguageUtil.get(request, "new-import-process") %>' url="<%= addImportProcessURL.toString() %>" />
    </c:if>
</liferay-frontend:add-menu>
