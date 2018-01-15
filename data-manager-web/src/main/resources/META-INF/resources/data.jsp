<%--
    data.jsp: Data panel of Inofix' data-manager.
    
    Created:     2017-12-11 22:14 by Christian Berndt
    Modified:    2018-01-15 17:53 by Christian Berndt
    Version:     1.0.2
--%>

<%@ include file="/init.jsp" %>

<%
    String redirect = ParamUtil.getString(request, "redirect");

    PortletURL portletURL = renderResponse.createRenderURL();

    portletURL.setParameter("mvcPath", "/view.jsp");
    portletURL.setParameter("redirect", redirect);
    portletURL.setParameter("tabs1", "data");
    
    // The list of availabe channels
    
    SearchContext searchContext = SearchContextFactory.getInstance(request);

    Facet idFacet = new MultiValueFacet(searchContext);
    idFacet.setFieldName(DataManagerField.ID);

    searchContext.addFacet(idFacet);

    // Remove facet attributes from context, since we need the field's index here

    searchContext.setAttribute(DataManagerField.ID, null);
    searchContext.setAttribute("from", 0);
    searchContext.setAttribute("until", 0);

    Indexer<Measurement> indexer = IndexerRegistryUtil.getIndexer(Measurement.class);
    indexer.search(searchContext);

    FacetCollector idFacetCollector = idFacet.getFacetCollector();
    List<TermCollector> idTermCollectors = idFacetCollector.getTermCollectors();
    
    request.setAttribute("data.jsp-idTermCollectors", idTermCollectors);
    
    // Set default id
    
    if (Validator.isNull(id)) {
        if (idTermCollectors.size() > 0) {
            id = idTermCollectors.get(0).getTerm();
        }
    }
%>

<aui:nav-bar cssClass="navbar-collapse-absolute" markupView="<%= markupView %>">
    <aui:nav cssClass="navbar-nav">

        <%
            portletURL.setParameter("tabs2", "channels");
        %>

        <aui:nav-item
            href="<%= portletURL.toString() %>"
            label="channels"
            selected='<%= tabs2.equals("channels") %>'
        />
        
        <%
            portletURL.setParameter("tabs2", "graphs");
        %>

        <aui:nav-item
            href="<%= portletURL.toString() %>"
            label="graphs"
            selected='<%= tabs2.equals("graphs") %>'
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
    <c:when test="<%= "graphs".equals(tabs2) %>">
        <liferay-util:include page="/data_graph.jsp" servletContext="<%= application %>"/>
    </c:when>
    <c:when test="<%= "list".equals(tabs2) %>">
        <liferay-util:include page="/data_list.jsp" servletContext="<%= application %>"/>
    </c:when>
    <c:otherwise>
        <liferay-util:include page="/data_channels.jsp" servletContext="<%= application %>"/>
    </c:otherwise>
</c:choose>
