<%--
    data_toolbar.jsp: The data-toolbar of the data-manager portlet

    Created:    2018-01-15 16:23 by Christian Berndt
    Modified:   2018-01-15 16:23 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp"%>

<%        
    String jsonURL = request.getParameter("jsonURL"); 

    PortletURL portletURL = liferayPortletResponse.createRenderURL();
    portletURL.setParameters(renderRequest.getParameterMap());
    
    List<ManagementBarFilterItem> rangeFilterItems = new ArrayList<ManagementBarFilterItem>();
    
    portletURL.setParameter("range", "day");
    rangeFilterItems.add(new ManagementBarFilterItem("day", portletURL.toString()));
    
    portletURL.setParameter("range", "week");
    rangeFilterItems.add(new ManagementBarFilterItem("week", portletURL.toString()));
    
    portletURL.setParameter("range", "month");
    rangeFilterItems.add(new ManagementBarFilterItem("month", portletURL.toString()));
    
    portletURL.setParameter("range", "year");
    rangeFilterItems.add(new ManagementBarFilterItem("year", portletURL.toString()));
    
    
    List<ManagementBarFilterItem> channelFilterItems = new ArrayList<ManagementBarFilterItem>();

    List<TermCollector> idTermCollectors = (List<TermCollector>) request
            .getAttribute("data.jsp-idTermCollectors");
    
    for (TermCollector termCollector : idTermCollectors) {
        
        Sort sort = new Sort("timestamp_Number_sortable", true);

        Hits hits = MeasurementServiceUtil.search(
                themeDisplay.getUserId(),
                themeDisplay.getScopeGroupId(), null,
                termCollector.getTerm(), null, null, null, null, null,
                null, true, 0, 1, sort);

        if (hits.getLength() > 0) {

            List<Measurement> measurements = MeasurementUtil
                    .getMeasurements(hits);
            Measurement measurement = measurements.get(0);
            
            String label = measurement.getId() + StringPool.SPACE + measurement.getName();
        
            portletURL.setParameter("id", measurement.getId());
            portletURL.setParameter("range", range);
            channelFilterItems.add(new ManagementBarFilterItem(measurement.getId(), label, portletURL.toString()));
            
        }       
    }

%>

<liferay-frontend:management-bar>

    <liferay-frontend:management-bar-filters>
        
        <liferay-frontend:management-bar-filter label="range" value="<%= range %>" 
             managementBarFilterItems="<%= rangeFilterItems %>"/>
             
         <liferay-frontend:management-bar-filter label="channel" value="<%= id %>" 
             managementBarFilterItems="<%= channelFilterItems %>"/>
        
    </liferay-frontend:management-bar-filters>

    <liferay-frontend:management-bar-buttons>

        <liferay-ui:icon-menu direction="down" icon="icon-download"
            message="download">

            <liferay-ui:icon message="download-json" target="_blank"
                url="<%= jsonURL %>" />

        </liferay-ui:icon-menu>

    </liferay-frontend:management-bar-buttons>

</liferay-frontend:management-bar>
