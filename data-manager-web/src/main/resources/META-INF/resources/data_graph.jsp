<%--
    data_graph.jsp: billboard / d3 powered channel graph.

	Created:	2017-12-19 00:06 by Christian Berndt
    Modified: 	2018-01-15 18:03 by Christian Berndt
    Version:  	1.0.8
--%>

<%@ include file="/init.jsp"%>

<%
    List<TermCollector> idTermCollectors = (List<TermCollector>) request
            .getAttribute("data.jsp-idTermCollectors");
    
    // Set default id
    
    if (Validator.isNull(id)) {
        if (idTermCollectors.size() > 0) {
            id = idTermCollectors.get(0).getTerm();
        }
    }
%>

<%-- id = <%= id %> <br/> --%>
<%-- from = <%= from %> <br/> --%>
<%-- range = <%= range %> <br/> --%>
<%-- until = <%= until %> <br/>      --%>

<portlet:resourceURL id="exportMeasurements" var="getJSONURL">
    <portlet:param name="advancedSearch" value="true" />
    <portlet:param name="<%=Constants.CMD%>" value="getJSON" />
    <portlet:param name="id" value="<%=id%>" />
    <portlet:param name="from" value="<%=String.valueOf(from)%>" />
    <portlet:param name="range" value="<%=range%>" />
    <portlet:param name="redirect" value="<%=currentURL%>" />
    <portlet:param name="until" value="<%=String.valueOf(until)%>" />
</portlet:resourceURL>

<liferay-util:include page="/data_toolbar.jsp" servletContext="<%= application %>">
    <liferay-util:param name="id" value="<%= id %>"/>
    <liferay-util:param name="jsonURL" value="<%= getJSONURL.toString() %>"/>
</liferay-util:include>

  
<!-- Temporary workaround to obtain the billboard.js library stylesheets -->
<link href="/o/data-manager-web/node_modules/billboard.js@1.2.0/dist/billboard.css" rel="stylesheet">
 
<div class="graph-wrapper">  
    <div id="<portlet:namespace />-JSONData"></div>
</div>

<aui:script require="data-manager-web@1.0.0">
    dataManagerWeb100.default('<%= getJSONURL %>', '<portlet:namespace/>', '<%= range %>');
</aui:script>
