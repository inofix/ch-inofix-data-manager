<%--
    data.jsp: Data panel of Inofix' data-manager.
    
    Created:     2017-12-11 22:14 by Christian Berndt
    Modified:    2017-12-11 22:14 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp" %>

<%
    String redirect = ParamUtil.getString(request, "redirect");

    PortletURL portletURL = renderResponse.createRenderURL();

    portletURL.setParameter("mvcPath", "/view.jsp");
    portletURL.setParameter("redirect", redirect);
    portletURL.setParameter("tabs1", "data");
%>

<aui:nav-bar cssClass="navbar-collapse-absolute" markupView="<%= markupView %>">
    <aui:nav cssClass="navbar-nav">

        <%
            portletURL.setParameter("tabs2", "latest");
        %>

        <aui:nav-item
            href="<%= portletURL.toString() %>"
            label="latest"
            selected='<%= tabs2.equals("latest") %>'
        />

        <%
            portletURL.setParameter("tabs2", "list");
        %>

        <aui:nav-item
            href="<%= portletURL.toString() %>"
            label="list"
            selected='<%= tabs2.equals("list") %>'
        />
        
    </aui:nav>
</aui:nav-bar>

<c:choose>
    <c:when test="<%= "list".equals(tabs2) %>">
        <liferay-util:include page="/measurement_list.jsp" servletContext="<%= application %>"/>
    </c:when>
    <c:otherwise>
        <liferay-util:include page="/latest_measurements.jsp" servletContext="<%= application %>"/>
    </c:otherwise>
</c:choose>
