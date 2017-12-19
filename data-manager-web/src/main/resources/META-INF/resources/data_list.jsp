<%--
    data_list.jsp: List view of Inofix' data-manager.
    
    Created:     2017-12-11 22:33 by Christian Berndt
    Modified:    2017-12-11 22:33 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp" %>

<%
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
    
    boolean ignoreFromDate = ParamUtil.getBoolean(request, "ignoreFromDate", true);

    Date fromDate = null;

    if (!ignoreFromDate) {

        fromDateDay = ParamUtil.getInteger(request, "fromDateDay");
        fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth");
        fromDateYear = ParamUtil.getInteger(request, "fromDateYear");
        fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear);
    }
    
    boolean ignoreUntilDate = ParamUtil.getBoolean(request, "ignoreUntilDate", true);
    
    Date untilDate = null;

    if (!ignoreUntilDate) {

        untilDateDay = ParamUtil.getInteger(request, "untilDateDay");
        untilDateMonth = ParamUtil.getInteger(request, "untilDateMonth");
        untilDateYear = ParamUtil.getInteger(request, "untilDateYear");
        untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear);
    }
    
    Hits hits = null;

    if (searchTerms.isAdvancedSearch()) {

        hits = MeasurementServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), null,
                searchTerms.getId(), null, null, fromDate, untilDate, null, searchTerms.isAdvancedSearch(),
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
            
        <portlet:actionURL name="editMeasurement" var="editMeasurementURL"/>
        
        <aui:form action="<%= editMeasurementURL %>" name="fm" 
            onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "editSet();" %>'>
            
            <aui:input name="<%= Constants.CMD %>" type="hidden"/>  
            <aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
            <aui:input name="deleteMeasurementIds" type="hidden" />
                    
            <liferay-util:include page="/view_measurements.jsp" servletContext="<%= application %>" />
        
        </aui:form>
    </div>
</div>

<liferay-util:include page="/add_button.jsp" servletContext="<%= application %>" />    
