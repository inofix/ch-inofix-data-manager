<%--
    configuration.jsp: configuration of the data-manager portlet.
    
    Created:    2017-09-14 17:33 by Christian Berndt
    Modified:   2017-10-15 22:03 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp"%>

<%@page import="com.liferay.portal.kernel.model.User"%>
<%@page import="com.liferay.portal.kernel.service.UserServiceUtil"%>

<%@page import="java.util.List"%>

<%
    List<User> users = UserServiceUtil.getGroupUsers(scopeGroupId);
%>

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
        
            <liferay-ui:panel collapsible="<%=true%>"
                id="dataSourcePanel"
                markupView="<%=markupView%>" persistState="<%=true%>"
                title="data-source">

                <aui:input name="preferences--dataURL--"
                    helpMessage="data-url-help" inlineField="<%=true%>"
                    value="<%=dataURL%>" />
                    
                <aui:input name="preferences--userName--"
                    helpMessage="user-name-help" inlineField="<%=true%>"
                    value="<%=userName%>" />
                    
                <aui:input name="preferences--password--"
                    helpMessage="password-help" inlineField="<%=true%>"
                    type="password"
                    value="<%=password%>" />
    
                <aui:select name="preferences--userId--"
                    helpMessage="user-id-help" inlineField="<%=true%>">
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
                    inlineField="true" value="<%=scopeGroupId%>" />

            </liferay-ui:panel>

            <liferay-ui:panel collapsible="<%=true%>"
                id="dataSchemaPanel"
                markupView="<%=markupView%>" persistState="<%=true%>"
                title="data-schema">

                <aui:input name="preferences--jsonSchema--"
                    helpMessage="json-schema-help" inlineField="true"
                    type="textarea" value="<%=jsonSchema%>" />

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
