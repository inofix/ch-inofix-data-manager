<%--
    view.jsp: Default view of Inofix' data-manager.
    
    Created:     2017-09-10 16:37 by Christian Berndt
    Modified:    2017-09-10 16:37 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp" %>

<liferay-util:include page="/navigation.jsp"
    servletContext="<%=application%>"/>

<c:choose>
    <c:when test="<%= "export-import".equals(tabs1) %>">
        <liferay-util:include page="/export_import.jsp" servletContext="<%= application %>"/>
    </c:when>
    <c:otherwise>
        <liferay-util:include page="/toolbar.jsp" servletContext="<%= application %>">
            <liferay-util:param name="searchContainerId" value="measurements" />
        </liferay-util:include>
        
        <div class="container-fluid-1280">       
            <div id="<portlet:namespace />contactManagerContainer">
            
                <liferay-ui:error exception="<%= PrincipalException.class %>"
                    message="you-dont-have-the-required-permissions" />
                                
            </div>
        </div>
    </c:otherwise>
</c:choose>
