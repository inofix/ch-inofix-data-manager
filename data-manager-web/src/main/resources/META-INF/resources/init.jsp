<%--
    init.jsp: Common imports and initialization code.

    Created:     2017-09-10 16:39 by Christian Berndt
    Modified:    2017-11-10 17:10 by Christian Berndt
    Version:     1.1.4
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

<%@page import="ch.inofix.data.background.task.MeasurementExportBackgroundTaskExecutor"%>
<%@page import="ch.inofix.data.background.task.MeasurementImportBackgroundTaskExecutor"%>
<%@page import="ch.inofix.data.constants.MeasurementActionKeys"%>
<%@page import="ch.inofix.data.constants.PortletKeys"%>
<%@page import="ch.inofix.data.exception.FileFormatException"%>
<%@page import="ch.inofix.data.model.Measurement"%>
<%@page import="ch.inofix.data.service.MeasurementServiceUtil"%>
<%@page import="ch.inofix.data.service.permission.DataManagerPortletPermission"%>
<%@page import="ch.inofix.data.service.permission.MeasurementPermission"%>
<%@page import="ch.inofix.data.service.util.MeasurementUtil"%>
<%@page import="ch.inofix.data.web.configuration.DataManagerConfiguration"%>
<%@page import="ch.inofix.data.web.internal.constants.DataManagerWebKeys"%>
<%@page import="ch.inofix.data.web.internal.search.EntriesChecker"%>
<%@page import="ch.inofix.data.web.internal.search.MeasurementSearch"%>
<%@page import="ch.inofix.data.web.internal.search.MeasurementSearchTerms"%>

<%@page import="com.liferay.background.task.kernel.util.comparator.BackgroundTaskComparatorFactoryUtil"%>
<%@page import="com.liferay.exportimport.kernel.lar.ExportImportHelper"%>
<%@page import="com.liferay.exportimport.kernel.lar.ExportImportHelperUtil"%>
<%@page import="com.liferay.exportimport.kernel.exception.LARFileSizeException"%>
<%@page import="com.liferay.exportimport.kernel.exception.LARTypeException"%>
<%@page import="com.liferay.exportimport.kernel.lar.ManifestSummary"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTask"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus"%>
<%@page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil"%>
<%@page import="com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker"%>
<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="com.liferay.portal.kernel.json.JSONArray"%>
<%@page import="com.liferay.portal.kernel.json.JSONFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.json.JSONObject"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.model.Group"%>
<%@page import="com.liferay.portal.kernel.model.Ticket"%>
<%@page import="com.liferay.portal.kernel.model.TicketConstants"%>
<%@page import="com.liferay.portal.kernel.model.User"%>
<%@page import="com.liferay.portal.kernel.portlet.PortalPreferences"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletURLUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil"%>
<%@page import="com.liferay.portal.kernel.repository.model.FileEntry"%>
<%@page import="com.liferay.portal.kernel.search.Hits"%>
<%@page import="com.liferay.portal.kernel.search.Sort"%>
<%@page import="com.liferay.portal.kernel.security.auth.PrincipalException"%>
<%@page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil"%>
<%@page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.service.ServiceContext"%>
<%@page import="com.liferay.portal.kernel.service.TicketLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.service.UserServiceUtil"%>
<%@page import="com.liferay.portal.kernel.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.CalendarFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.util.HttpUtil"%>
<%@page import="com.liferay.portal.kernel.util.OrderByComparator"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.PrefsPropsUtil"%>
<%@page import="com.liferay.portal.kernel.util.PropsKeys"%>
<%@page import="com.liferay.portal.kernel.util.PortalUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringBundler"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@page import="com.liferay.portal.kernel.util.TextFormatter"%>
<%@page import="com.liferay.portal.kernel.util.Time"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>

<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Objects"%>
<%@page import="java.io.Serializable"%>

<%@page import="javax.portlet.PortletURL"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="javax.portlet.ResourceURL"%>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
    String dataURL = portletPreferences.getValue("dataURL", "");    
    String jsonSchema = portletPreferences.getValue("jsonSchema", "");
    String markupView = portletPreferences.getValue("markupView", "lexicon");   
    Date now = new Date();     
    String password = portletPreferences.getValue("password", "");   
    boolean showSearchSpeed = false;    
    String tabs1 = ParamUtil.getString(request, "tabs1", "data");
    String tabs2 = ParamUtil.getString(request, "tabs2", "import");    
    long until = ParamUtil.getLong(request, "until", now.getTime());    
    long userId = GetterUtil.getLong(portletPreferences.getValue("userId", "0")); 
    String userName = portletPreferences.getValue("userName", "");
    
    DataManagerConfiguration dataManagerConfiguration = (DataManagerConfiguration) liferayPortletRequest
            .getAttribute(DataManagerConfiguration.class.getName());
//     DataManagerConfiguration dataManagerConfiguration = (DataManagerConfiguration) renderRequest
//             .getAttribute(DataManagerConfiguration.class.getName());
        
    if (Validator.isNotNull(dataManagerConfiguration)) {
        
        dataURL = portletPreferences.getValue("dataURL", dataManagerConfiguration.dataURL());
        jsonSchema = portletPreferences.getValue("jsonSchema", dataManagerConfiguration.jsonSchema());
        markupView = portletPreferences.getValue("markupView", dataManagerConfiguration.markupView());
        showSearchSpeed = GetterUtil.getBoolean(portletPreferences.getValue("showSearchSpeed", String.valueOf(dataManagerConfiguration.showSearchSpeeed())));
        userId = GetterUtil.getLong(portletPreferences.getValue("userId", dataManagerConfiguration.userId()));
        userName = portletPreferences.getValue("userName", dataManagerConfiguration.userName());
        
        // because of current checkbox configuration
        if ("false".equals(markupView)) {
            markupView = ""; 
        }
    }
    
    JSONObject jsonSchemaObj = null; 
    
    try {
        jsonSchemaObj = JSONFactoryUtil.createJSONObject(jsonSchema); 
    } catch (Exception e) {
        _log.error(e);
    }
    
    Iterator<String> keys = null;

    if (jsonSchemaObj != null) {
        JSONObject itemsObj = jsonSchemaObj.getJSONObject("items");
        if (itemsObj != null) {
            JSONObject propertiesObj = itemsObj.getJSONObject("properties");            
            keys = propertiesObj.keys();
        }    
    }
%>

<%!
    private static Log _log = LogFactoryUtil.getLog("ch_inofix_data_web.init_jsp");
%>
