<%--
    error.jsp: error page of the data-manager portlet.

    Created:     2017-11-13 20:41 by Christian Berndt
    Modified:    2017-11-13 20:41 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:error-header />

<liferay-ui:error exception="<%= NoSuchMeasurementException.class %>" message="the-task-record-could-not-be-found" />

<liferay-ui:error-principal />
