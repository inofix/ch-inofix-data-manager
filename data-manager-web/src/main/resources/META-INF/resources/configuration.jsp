<%--
    configuration.jsp: configuration of the data-manager portlet.
    
    Created:    2017-09-14 17:33 by Christian Berndt
    Modified:   2017-09-14 17:33 by Christian Berndt
    Version:    1.0.0
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
    var="configurationRenderURL">
    <portlet:param name="tabs1" value="<%=tabs1%>" />
</liferay-portlet:renderURL>

<aui:form action="<%=configurationActionURL%>" method="post" name="fm"
    onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveConfigurations();" %>'>
    
    <aui:input name="<%=Constants.CMD%>" type="hidden"
        value="<%=Constants.UPDATE%>" />
    <aui:input name="redirect" type="hidden"
        value="<%=configurationRenderURL%>" />

    <div class="portlet-configuration-body-content">
        <div class="container-fluid-1280">
            <aui:fieldset-group markupView="<%= markupView %>">
                <aui:fieldset collapsible="<%= true %>" id="displaySettingsPanel" label="display-settings">

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
                </aui:fieldset>                
            </aui:fieldset-group>
        </div>
    </div>
    
    <aui:button-row>
        <aui:button cssClass="btn-lg" type="submit" />
    </aui:button-row>
</aui:form>

<aui:script use="aui-base">
    var form = A.one('#<portlet:namespace />fm');

    var modified = function(panel) {
        var modifiedNotice = panel.one('.panel-heading .panel-title a .modified-notice');

        if (modifiedNotice == null) {
            var displayTitle = panel.one('.panel-heading .panel-title a');

            displayTitle.append('<span class="modified-notice"> (<liferay-ui:message key="modified" />) </span>');
        }
    };

    var selected = form.all('.left-selector');

    var selectedHTML = '';

    for (var i = selected._nodes.length - 1; i >= 0; --i) {
        selectedHTML = selectedHTML.concat(selected._nodes[i].innerHTML);
    }

</aui:script>

<aui:script>
    Liferay.provide(
        window,
        '<portlet:namespace />saveConfigurations',
        function() {

            submitForm(document.<portlet:namespace />fm);
        },
        ['liferay-util-list-fields']
    );
</aui:script>
