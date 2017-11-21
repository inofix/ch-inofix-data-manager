<%--
    configuration.jsp: configuration of the data-manager portlet.
    
    Created:    2017-09-14 17:33 by Christian Berndt
    Modified:   2017-11-20 01:02 by Christian Berndt
    Version:    1.0.6
--%>

<%@ include file="/init.jsp"%>

<%
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

                <aui:input name="preferences--timestampField--"
                    helpMessage="timestamp-field-help" value="<%=timestampField%>" />

            </liferay-ui:panel>
            
            <liferay-ui:panel collapsible="<%=true%>" extended="<%= true %>"
                id="displaySettingsPanel" markupView="<%=markupView%>"
                persistState="<%=true%>" title="display-settings">

                <aui:input name="preferences--columns--"
                    helpMessage="columns-help" value="<%= StringUtil.merge(columns) %>" />

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

        submitForm(form);
    }
</aui:script>
