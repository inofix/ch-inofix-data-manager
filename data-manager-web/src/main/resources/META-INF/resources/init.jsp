<%--
    init.jsp: Common imports and initialization code.

    Created:     2017-09-10 16:39 by Christian Berndt
    Modified:    2017-09-17 23:59 by Christian Berndt
    Version:     1.0.3
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend"%>
<%@taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@taglib uri="http://liferay.com/tld/security" prefix="liferay-security"%>
<%@taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@page import="ch.inofix.data.constants.MeasurementActionKeys"%>
<%@page import="ch.inofix.data.service.permission.DataManagerPermission"%>
<%@page import="ch.inofix.data.web.configuration.DataManagerConfiguration"%>

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.PortalPreferences"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.security.auth.PrincipalException"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>

<%@page import="java.util.Date"%>

<%@page import="javax.portlet.PortletURL"%>
<%@page import="javax.portlet.ResourceURL"%>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
    PortalPreferences portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(request);

    String dataURL = portletPreferences.getValue("dataURL", "");

    String markupView = "lexicon";
    
    Date now = new Date(); 
    
    String password = portletPreferences.getValue("password", "");
    
    boolean showSearchSpeed = false;
    
    String tabs1 = ParamUtil.getString(request, "tabs1", "data");
    String tabs2 = ParamUtil.getString(request, "tabs2", "export");
    
    long until = ParamUtil.getLong(request, "until", now.getTime());
    
    long userId = GetterUtil.getLong(portletPreferences.getValue("userId", "0"));
    
    String userName = portletPreferences.getValue("userName", "");
    
    DataManagerConfiguration dataManagerConfiguration = (DataManagerConfiguration) request
            .getAttribute(DataManagerConfiguration.class.getName());
    
    if (Validator.isNotNull(dataManagerConfiguration)) {
        
        markupView = portletPreferences.getValue("markup-view", dataManagerConfiguration.markupView());
        showSearchSpeed = GetterUtil.getBoolean(portletPreferences.getValue("show-search-speed", String.valueOf(dataManagerConfiguration.showSearchSpeeed())));
        
        // because of current checkbox configuration
        if ("false".equals(markupView)) {
            markupView = ""; 
        }
    }
%>