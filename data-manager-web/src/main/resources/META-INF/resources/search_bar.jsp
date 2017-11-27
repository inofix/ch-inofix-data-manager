<%--
    search.jsp: The search of the data-manager portlet.

    Created:     2017-11-22 23:18 by Christian Berndt
    Modified:    2017-11-27 23:12 by Christian Berndt
    Version:     1.0.1
--%>

<%@ include file="/init.jsp" %>

<%
    MeasurementDisplayTerms displayTerms = new MeasurementDisplayTerms(renderRequest);

    long week = 1000 * 60 * 60 * 24 * 7;
    Calendar calendar = CalendarFactoryUtil.getCalendar(now.getTime() - week, timeZone);

    int fromDateDay = ParamUtil.getInteger(request, "fromDateDay", calendar.get(Calendar.DAY_OF_MONTH));
    int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth", calendar.get(Calendar.MONTH));
    int fromDateYear = ParamUtil.getInteger(request, "fromDateYear", calendar.get(Calendar.YEAR));
    Date fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay, fromDateYear);
    boolean ignoreFromDate = ParamUtil.getBoolean(request, "ignoreFromDate", true); 

    calendar = CalendarFactoryUtil.getCalendar(now.getTime(), timeZone);

    int untilDateDay = ParamUtil.getInteger(request, "untilDateDay", calendar.get(Calendar.DAY_OF_MONTH));
    int untilDateMonth = ParamUtil.getInteger(request, "untilDateMonth", calendar.get(Calendar.MONTH));
    int untilDateYear = ParamUtil.getInteger(request, "untilDateYear", calendar.get(Calendar.YEAR));
    Date untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay, untilDateYear);
    boolean ignoreUntilDate = ParamUtil.getBoolean(request, "ignoreUntilDate", true); 
%>

<liferay-ui:search-toggle
    autoFocus="<%=windowState.equals(WindowState.MAXIMIZED)%>"
    buttonLabel="search" displayTerms="<%=displayTerms%>"
    id="toggle_id_measurement_search" markupView="<%=markupView%>">

    <aui:fieldset>

        <aui:field-wrapper inlineField="true">
            <liferay-ui:input-date dayValue="<%=fromDateDay%>"
                disabled="<%=ignoreFromDate%>"
                monthValue="<%=fromDateMonth%>"
                yearValue="<%=fromDateYear%>" formName="searchFm" />
            <aui:input name="ignoreFromDate" type="checkbox" label="ignore-from-date"
                value="<%= ignoreFromDate %>"/>
        </aui:field-wrapper>        
        
        <aui:field-wrapper inlineField="true">
            <liferay-ui:input-date dayValue="<%=untilDateDay%>"
                disabled="<%=ignoreUntilDate%>"
                monthValue="<%=untilDateMonth%>"
                name="untilDate"
                yearValue="<%=untilDateYear%>" formName="searchFm" />
            <aui:input name="ignoreUntilDate" type="checkbox" label="ignore-until-date"
                value="<%= ignoreUntilDate %>"/>
        </aui:field-wrapper>

    </aui:fieldset>

</liferay-ui:search-toggle>
