<%--
    search.jsp: The search of the data-manager portlet.

    Created:     2017-11-22 23:18 by Christian Berndt
    Modified:    2017-12-03 16:47 by Christian Berndt
    Version:     1.0.2
--%>

<%@ include file="/init.jsp" %>

<%
    MeasurementDisplayTerms displayTerms = new MeasurementDisplayTerms(renderRequest);

    boolean ignoreFromDate = ParamUtil.getBoolean(request, "ignoreFromDate", true); 
    boolean ignoreUntilDate = ParamUtil.getBoolean(request, "ignoreUntilDate", true); 
%>

<liferay-ui:search-toggle
    autoFocus="<%=windowState.equals(WindowState.MAXIMIZED)%>"
    buttonLabel="search" displayTerms="<%=displayTerms%>"
    id="toggle_id_measurement_search" markupView="<%=markupView%>">

    <aui:fieldset>

        <aui:field-wrapper inlineField="true">
            <liferay-ui:input-date 
                dayParam="fromDateDay"
                dayValue="<%=fromDateDay%>"
                disabled="<%=ignoreFromDate%>"
                monthParam="fromDateMonth"
                monthValue="<%=fromDateMonth%>" 
                name="fromDate"
                yearParam="fromDateYear" 
                yearValue="<%=fromDateYear%>"
                formName="searchFm" />
            <aui:input name="ignoreFromDate" type="checkbox"
                label="ignore-from-date" value="<%=ignoreFromDate%>" />
        </aui:field-wrapper>

        <aui:field-wrapper inlineField="true">
            <liferay-ui:input-date 
                dayParam="untilDateDay"
                dayValue="<%=untilDateDay%>"
                disabled="<%=ignoreUntilDate%>"
                monthParam="untilDateMonth"
                monthValue="<%=untilDateMonth%>" 
                name="untilDate"
                yearParam="untilDateYear" 
                yearValue="<%=untilDateYear%>"
                formName="searchFm" />
            <aui:input name="ignoreUntilDate" type="checkbox"
                label="ignore-until-date" value="<%=ignoreUntilDate%>" />
        </aui:field-wrapper>

    </aui:fieldset>

</liferay-ui:search-toggle>
