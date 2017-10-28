<%--
    view.jsp: Default view of Inofix' data-manager.
    
    Created:     2017-09-10 16:37 by Christian Berndt
    Modified:    2017-09-28 16:51 by Christian Berndt
    Version:     1.0.2
--%>

<%@ include file="/init.jsp" %>

<%
    // TODO: read view settings from configuration
    String [] columns = new String[] {"measurement-id", "data", "create-date", "user-name"};
    String displayStyle = ParamUtil.getString(request, "displayStyle");
    
    String backURL = ParamUtil.getString(request, "backURL");
    String keywords = ParamUtil.getString(request, "keywords");
 
    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameters(renderRequest.getParameterMap());
    portletURL.setParameter("redirect", ""); 
    portletURL.setParameter("tabs1", tabs1); 
    
    int status = ParamUtil.getInteger(request, "status"); 
    
    MeasurementSearch searchContainer = new MeasurementSearch(renderRequest, "cur", portletURL);
    
    boolean reverse = false; 
    if (searchContainer.getOrderByType().equals("desc")) {
        reverse = true;
    }
    
    Sort sort = new Sort(searchContainer.getOrderByCol(), reverse);
    
    MeasurementSearchTerms searchTerms = (MeasurementSearchTerms) searchContainer.getSearchTerms();
    
    Hits hits = null;

    if (searchTerms.isAdvancedSearch()) {
        
        // TODO: add advanced search
        hits = MeasurementServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, keywords,
                searchContainer.getStart(), searchContainer.getEnd(), sort);
        
    } else {
        
        hits = MeasurementServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, keywords,
                searchContainer.getStart(), searchContainer.getEnd(), sort);
    }

    List<Measurement> measurements = MeasurementUtil.getMeasurements(hits);

    searchContainer.setResults(measurements);
    searchContainer.setTotal(hits.getLength());

    request.setAttribute("view.jsp-columns", columns);

    request.setAttribute("view.jsp-displayStyle", displayStyle);

    request.setAttribute("view.jsp-searchContainer", searchContainer);

    request.setAttribute("view.jsp-total", hits.getLength());
%>

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
            <div id="<portlet:namespace />dataManagerContainer">
            
                <liferay-ui:error exception="<%= PrincipalException.class %>"
                    message="you-dont-have-the-required-permissions" />
                 
                <c:if test="<%= showSearchSpeed %>">  
                    <div class="alert alert-info">
                        <liferay-ui:search-speed hits="<%= hits %>" searchContainer="<%= searchContainer %>"/>
                    </div>
                </c:if> 
                    
                <portlet:actionURL var="editSetURL"/>
                
                <aui:form action="<%= editSetURL %>" name="fm" 
                    onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "editSet();" %>'>
                    
                    <aui:input name="<%= Constants.CMD %>" type="hidden"/>  
                    <aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
                    <aui:input name="deleteMeasurementIds" type="hidden" />
                            
                    <liferay-util:include page="/view_measurements.jsp" servletContext="<%= application %>" />
                
                </aui:form>
            </div>
        </div>
        
        <liferay-util:include page="/add_button.jsp" servletContext="<%= application %>" />    
        
    </c:otherwise>
</c:choose>
