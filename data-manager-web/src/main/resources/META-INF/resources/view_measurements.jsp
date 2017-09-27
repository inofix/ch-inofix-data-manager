<%--
    view_measurements.jsp: search-container of Inofix' data-manager.
    
    Created:     2017-09-27 10:31 by Christian Berndt
    Modified:    2017-09-27 10:31 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp"%>

<%@page import="ch.inofix.data.web.internal.search.EntriesChecker"%>

<%
    MeasurementSearch searchContainer = (MeasurementSearch) request.getAttribute("view.jsp-searchContainer");

    EntriesChecker entriesChecker = new EntriesChecker(liferayPortletRequest, liferayPortletResponse);

    searchContainer.setRowChecker(entriesChecker);

    String displayStyle = GetterUtil.getString((String) request.getAttribute("view.jsp-displayStyle"));

    String searchContainerId = ParamUtil.getString(request, "searchContainerId");
%>

<liferay-ui:search-container
    id="measurements"
    searchContainer="<%=searchContainer%>"
    var="measurementSearchContainer">
    
    <liferay-ui:search-container-row
        className="ch.inofix.data.model.Measurement"
        modelVar="measurement" keyProperty="measurementId">

        <portlet:renderURL var="editURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="measurementId"
                value="<%=String.valueOf(measurement.getMeasurementId())%>" />
            <portlet:param name="mvcPath" value="/edit_measurement.jsp" />
        </portlet:renderURL>

        <portlet:renderURL var="viewURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="measurementId"
                value="<%=String.valueOf(measurement.getMeasurementId())%>" />
            <portlet:param name="mvcPath" value="/edit_measurement.jsp" />
        </portlet:renderURL>

        <%
            request.setAttribute("editURL", editURL.toString());
            request.setAttribute("viewURL", viewURL.toString());

            boolean hasUpdatePermission = MeasurementPermission.contains(permissionChecker,
                    measurement.getMeasurementId(), MeasurementActionKeys.UPDATE);
            boolean hasViewPermission = MeasurementPermission.contains(permissionChecker,
                    measurement.getMeasurementId(), MeasurementActionKeys.VIEW);

            String detailURL = null;

            if (hasUpdatePermission) {
                detailURL = editURL.toString();
            } else if (hasViewPermission) {
                detailURL = viewURL.toString();
            }
        %>

        <%@ include file="/search_columns.jspf"%>

        <liferay-ui:search-container-column-jsp align="right"
            cssClass="entry-action" path="/measurement_action.jsp"
            valign="top" />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator displayStyle="<%=displayStyle%>"
        markupView="<%=markupView%>" />

</liferay-ui:search-container>
