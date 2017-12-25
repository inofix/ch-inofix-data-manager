<%--
    data_graph.jsp:  d3 powered channel graph.

	Created:	2017-12-19 00:06 by Christian Berndt
    Modified: 	2017-12-25 17:57 by Christian Berndt
    Version:  	1.0.3
--%>

<%@ include file="/init.jsp"%>

<%
	String id = ParamUtil.getString(request, "id");

	SearchContext searchContext = SearchContextFactory
	        .getInstance(request);

	Facet idFacet = new MultiValueFacet(searchContext);
	idFacet.setFieldName(DataManagerField.ID);
    
    searchContext.addFacet(idFacet);

	Indexer<Measurement> indexer = IndexerRegistryUtil
	        .getIndexer(Measurement.class);
	indexer.search(searchContext);

	FacetCollector idFacetCollector = idFacet.getFacetCollector();
	List<TermCollector> idTermCollectors = idFacetCollector
	        .getTermCollectors();

    // Set default id
    
	if (Validator.isNull(id)) {
		if (idTermCollectors.size() > 0) {
			id = idTermCollectors.get(0).getTerm();
		}
	}
%>

<div class="container-fluid-1280">   

    <portlet:resourceURL id="exportMeasurements" var="getJSONURL">
        <portlet:param name="advancedSearch" value="true" />
        <portlet:param name="<%= Constants.CMD %>" value="getJSON" />
        <portlet:param name="id" value="<%=id%>" />
        <portlet:param name="from" value="<%=String.valueOf(from)%>" />
        <portlet:param name="interval" value="<%=String.valueOf(interval)%>" />
        <portlet:param name="redirect" value="<%=currentURL%>" />
        <portlet:param name="until" value="<%=String.valueOf(until)%>" />
    </portlet:resourceURL>
    
    <aui:a cssClass="pull-right" href="<%= getJSONURL %>" label="download-json" target="_blank"/>
    
    <!-- Temporary workaround to obtain the library stylesheets -->
    <link href="/o/data-manager-web/node_modules/billboard.js@1.1.1/dist/billboard.css" rel="stylesheet">
    
<!--      <svg width="960" height="500"></svg> -->
    
    
    <%-- 
    <ol>
        <li>id = <%= id %></li>
        <li>interval = <%= interval %></li>
        <li>from = <%= from %></li>
        <li>until = <%= until %></li>
    </ol>

    <p class="hover-me">Hover over me!</p>

    <p class="hover-me">OK now hover over here!</p>

    <p class="hover-me">Hover here too!</p>
    
    --%>
    
    data_graph.jsp v.1
    
    <div id="<portlet:namespace />-BarChart"></div>
    
    <aui:script require="data-manager-web@1.0.0">
        dataManagerWeb100.default('<%= getJSONURL %>', '<portlet:namespace/>');
    </aui:script>

</div>
