<%--
    view.jsp: Default view of Inofix' data-manager.
    
    Created:     2017-09-10 16:37 by Christian Berndt
    Modified:    2017-12-11 22:34 by Christian Berndt
    Version:     1.0.6
--%>

<%@ include file="/init.jsp" %>

<%

%>

<liferay-util:include page="/navigation.jsp"
    servletContext="<%=application%>"/>
    
<c:choose>
    <c:when test="<%= "export-import".equals(tabs1) %>">
        <liferay-util:include page="/export_import.jsp" servletContext="<%= application %>"/>
    </c:when>
    <c:otherwise>
        <liferay-util:include page="/data.jsp" servletContext="<%= application %>"/>
    </c:otherwise>
</c:choose>
