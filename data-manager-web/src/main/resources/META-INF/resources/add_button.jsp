<%--
    add_button.jsp: add a measurement 
    
    Created:    2017-09-17 23:58 by Christian Berndt
    Modified:   2017-09-17 23:58 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<%-- 
<c:if test="<%=DataManagerPermission.contains(permissionChecker, scopeGroupId,
                        MeasurementActionKeys.ADD_MEASUREMENT)%>">
--%>
    <liferay-frontend:add-menu>

        <portlet:renderURL var="addMeasurementURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="mvcPath" value="/edit_measurement.jsp" />
        </portlet:renderURL>

        <liferay-frontend:add-menu-item
            title='<%=LanguageUtil.get(request, "add-measurement")%>'
            url="<%=addMeasurementURL.toString()%>" />

    </liferay-frontend:add-menu>

<%-- </c:if> --%>
