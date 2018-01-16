<%--
    data_graph.jsp: billboard / d3 powered channel graph.

	Created:	2017-12-19 00:06 by Christian Berndt
    Modified: 	2018-01-16 13:09 by Christian Berndt
    Version:  	1.0.9
--%>

<%@ include file="/init.jsp"%>

<%
    List<TermCollector> idTermCollectors = (List<TermCollector>) request
            .getAttribute("data.jsp-idTermCollectors");
    
    // Set default id 
    
    Measurement measurement = null; 
    
    if (Validator.isNull(id)) {
        
        if (idTermCollectors.size() > 0) {
            
            id = idTermCollectors.get(0).getTerm();

        }
    }
    
    // Set default measurement
    
    if (Validator.isNotNull(id)) {
                
        Sort sort = new Sort("timestamp_Number_sortable", true);
        
        TermCollector termCollector = idTermCollectors.get(0);

        Hits hits = MeasurementServiceUtil.search(
                themeDisplay.getUserId(),
                themeDisplay.getScopeGroupId(), null,
                id, null, null, null, null, null,
                null, true, 0, 1, sort);
        
        if (hits.getLength() > 0) {

            List<Measurement> measurements = MeasurementUtil
                    .getMeasurements(hits);
            measurement = measurements.get(0);
        }
    }

%>

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

<c:if test="<%= Validator.isNotNull(measurement) %>">
    <aui:script require="data-manager-web@1.0.0">
    
        var parameters = {
                "name" : '<%= measurement.getName() %>',
                "namespace" : '<portlet:namespace/>',
                "range" : '<%= range %>',
                "unit" : '<%= measurement.getUnit() %>'
        };
    
        dataManagerWeb100.default('<%= getJSONURL %>', parameters);
        
    </aui:script>
</c:if>
