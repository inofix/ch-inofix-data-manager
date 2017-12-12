<%--
    channels.jsp: channels panel of Inofix' data-manager.
    
    Created:     2017-12-03 19:21 by Christian Berndt
    Modified:    2017-12-12 16:36 by Christian Berndt
    Version:     1.0.1
--%>


<%@ include file="/init.jsp"%>

<%
    Format dateFormat = FastDateFormatFactoryUtil.getDateTime(
            DateFormat.MEDIUM, DateFormat.MEDIUM, locale, timeZone);

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
%>

<div class="container-fluid-1280">
    <aui:row>
        <%
            for (TermCollector termCollector : idTermCollectors) {

                Sort sort = new Sort(DataManagerField.TIMESTAMP, true);

                Hits hits = MeasurementServiceUtil.search(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
                        null, termCollector.getTerm(), null, null, null, null, null, true, 0, 1, sort);
                
                if (hits.getLength() > 0) {
                    
                    List<Measurement> measurements = MeasurementUtil.getMeasurements(hits);
                    Measurement measurement = measurements.get(0);
                    
        %>
                <aui:col md="3" sm="4" xs="6">
                    <div class="display">
                        <div class="name"><%= measurement.getName() %></div> 
<%--                         <a href="<%= graphURL.toString() %>">           --%>
                            <span class="face">
                                <span class="value-wrapper">
                                    <span class="value"><%= measurement.getValue() %></span><br/>
                                    <span class="unit"><%= measurement.getUnit() %></span> 
                                </span>
                            </span>
<%--                        </a> --%>
                        <div class="caption">
                            <%= dateFormat.format(measurement.getTimestamp()) %> 
                        </div>
                    </div>
                </aui:col>

        <%
                }                
            }
        %>
    </aui:row>
    <c:if test="<%= idTermCollectors.size() == 0 %>">
        <aui:alert closeable="<%= false %>" type="info">
            <liferay-ui:message key="there-are-no-channels-to-display"/>
        </aui:alert>
    </c:if>
</div>