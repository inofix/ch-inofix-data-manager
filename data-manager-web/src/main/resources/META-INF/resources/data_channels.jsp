<%--
    data_channels.jsp: channels panel of Inofix' data-manager.
    
    Created:     2017-12-03 19:21 by Christian Berndt
    Modified:    2018-01-15 23:48 by Christian Berndt
    Version:     1.0.4
--%>


<%@ include file="/init.jsp"%>

<%
    Format dateFormat = FastDateFormatFactoryUtil.getDateTime(
            DateFormat.MEDIUM, DateFormat.MEDIUM, locale, timeZone);

    List<TermCollector> idTermCollectors = (List<TermCollector>) request
            .getAttribute("data.jsp-idTermCollectors");
%>

<div class="container-fluid-1280">
    <aui:row>
        <%
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

                    PortletURL graphsURL = renderResponse.createRenderURL();
                    graphsURL.setParameter("tabs2", "graphs");
                    graphsURL.setParameter("id", measurement.getId());
                    
        %>
        <aui:col md="3" sm="4" xs="6">
            <div class="display">
                <div class="name"><%=measurement.getName()%></div>
                <a href="<%=graphsURL.toString()%>"> 
                    <span class="face"> 
                        <span class="value-wrapper">
                            <span class="value"><%=measurement.getValue()%></span><br />
                            <span class="unit"><%=measurement.getUnit()%></span>
                        </span>
                    </span>
                </a>
                <div class="caption">
                    <%= dateFormat.format(measurement.getTimestamp())%>
                </div>
            </div>
        </aui:col>

        <%
            }
                }
        %>
    </aui:row>
    <c:if test="<%=idTermCollectors.size() == 0%>">
        <aui:alert closeable="<%=false%>" type="info">
            <liferay-ui:message key="there-are-no-channels-to-display" />
        </aui:alert>
    </c:if>
</div>
