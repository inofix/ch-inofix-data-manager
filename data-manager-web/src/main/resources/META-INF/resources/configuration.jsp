<%--
    configuration.jsp: configuration of the data-manager portlet.
    
    Created:    2017-09-14 17:33 by Christian Berndt
    Modified:   2018-01-22 23:41 by Stefan Lübbers
    Version:    1.0.7
--%>

<%@ include file="/init.jsp"%>

<%
    //String[] columns = new String[0];
    PortletURL portletURL = renderResponse.createRenderURL();
    MeasurementSearch searchContainer = new MeasurementSearch(liferayPortletRequest, portletURL);
    List<String> headerList = searchContainer.getHeaderNames();
    List<User> users = UserServiceUtil.getGroupUsers(scopeGroupId);
%>

<style>
<!--
    .portlet-data-manager .panel-title {
        position: relative;
    }
-->
</style>

<liferay-portlet:actionURL portletConfiguration="<%=true%>"
    var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%=true%>"
    var="configurationRenderURL"/>

<aui:form action="<%=configurationActionURL%>" method="post" name="fm"
    onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveConfiguration();" %>'>
    
    <aui:input name="<%=Constants.CMD%>" type="hidden"
        value="<%=Constants.UPDATE%>" />
    <aui:input name="redirect" type="hidden"
        value="<%=configurationRenderURL%>" />

    <div class="portlet-configuration-body-content">
    
        <div class="container-fluid-1280">

            <liferay-ui:panel collapsible="<%=true%>" extended="<%= true %>"
                id="dataSourcePanel" markupView="<%=markupView%>"
                persistState="<%=true%>" title="data-source">

                <aui:input name="preferences--dataURL--"
                    helpMessage="data-url-help" value="<%=dataURL%>" />

                <aui:input name="preferences--userName--"
                    helpMessage="user-name-help" value="<%=userName%>" />

                <aui:input name="preferences--password--"
                    helpMessage="password-help" type="password"
                    value="<%=password%>" />

                <aui:select name="preferences--userId--"
                    helpMessage="user-id-help">
                    <%
                        for (User user1 : users) {
                    %>
                    <aui:option value="<%=user1.getUserId()%>"
                        label="<%=user1.getFullName()%>"
                        selected="<%=user1.getUserId() == userId%>" />
                    <%
                        }
                    %>
                </aui:select>

                <aui:input name="preferences--groupId--" type="hidden"
                    value="<%=scopeGroupId%>" />

                <aui:input name="preferences--groupId--"
                    disabled="<%=true%>" helpMessage="group-id-help"
                    value="<%=scopeGroupId%>" />

            </liferay-ui:panel>

            <liferay-ui:panel collapsible="<%=true%>"
                id="dataSchemaPanel"
                markupView="<%=markupView%>" persistState="<%=true%>"
                title="data-schema">

                <aui:input name="preferences--jsonSchema--"
                    helpMessage="json-schema-help"
                    type="textarea" value="<%=jsonSchema%>" />

            </liferay-ui:panel>
            
            <liferay-ui:panel collapsible="<%=true%>" extended="<%= true %>"
                id="fieldSettingsPanel" markupView="<%=markupView%>"
                persistState="<%=true%>" title="field-settings">

                <aui:input name="preferences--idField--"
                    helpMessage="id-field-help" value="<%=idField%>" />

                <aui:input name="preferences--nameField--"
                    helpMessage="name-field-help" value="<%=nameField%>" />

                <aui:input name="preferences--timestampField--"
                    helpMessage="timestamp-field-help" value="<%=timestampField%>" />

            </liferay-ui:panel>
            
            <liferay-ui:panel collapsible="<%=true%>" extended="<%= true %>"
                id="displaySettingsPanel" markupView="<%=markupView%>"
                persistState="<%=true%>" title="display-settings">
                
                <aui:input name="columns" type="hidden"/>

                <aui:fieldset collapsible="<%=true%>" label="show-columns">
	            <%
	                Set<String> availableColumns = SetUtil.fromList(headerList);
	                // Left list
	                List leftList = new ArrayList();
	                for (String column : columns) {
	                    leftList.add(new KeyValuePair(column, LanguageUtil.get(request, column)));
	                }
	                // Right list
	                List rightList = new ArrayList();
	                Arrays.sort(columns);
	                for (String column : availableColumns) {
	                    if (Arrays.binarySearch(columns, column) < 0) {
	                        rightList.add(new KeyValuePair(column, LanguageUtil.get(request, column)));
	                    }
	                }
	                rightList = ListUtil.sort(rightList, new KeyValuePairComparator(false, true));
	            %>
    
                <liferay-ui:input-move-boxes
                    leftBoxName="currentColumns"
                    leftList="<%=leftList%>"
                    leftReorder="<%=Boolean.TRUE.toString()%>"
                    leftTitle="current"
                    rightBoxName="availableColumns"
                    rightList="<%=rightList%>" rightTitle="available" />
            </aui:fieldset>

            </liferay-ui:panel>

        </div>
    </div>
    
    <aui:button-row>
        <aui:button cssClass="btn-lg" type="submit" />
    </aui:button-row>
</aui:form>

<aui:script>
    function <portlet:namespace />saveConfiguration() {
        var Util = Liferay.Util;

        var form = AUI.$(document.<portlet:namespace />fm);
        
        form.fm('columns').val(Util.listSelect(form.fm('currentColumns')));

        submitForm(form);
    }
</aui:script>
