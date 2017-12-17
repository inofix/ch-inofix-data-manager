package ch.inofix.data.web.internal.messaging;

import java.io.File;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;
import com.liferay.portal.kernel.scheduler.SchedulerEntryImpl;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.service.MeasurementLocalService;
import ch.inofix.data.web.configuration.ExportImportConfigurationSettingsMapFactory;



/**
 * 
 * @author Christian Berndt
 * @created 2017-12-14 21:03
 * @modified 2017-12-16 19:40
 * @version 1.0.1
 *
 */
@Component(immediate = true, service = ImportDataMessageListener.class)
public class ImportDataMessageListener extends BaseMessageListener {
    
    @Activate
    protected void activate() {
        Class<?> clazz = getClass();

        String className = clazz.getName();

        // TODO: read importInterval from properties / configuration
        int importInterval = 15;

        Trigger trigger = _triggerFactory.createTrigger(className, className, null, null, importInterval,
                TimeUnit.MINUTE);

        SchedulerEntry schedulerEntry = new SchedulerEntryImpl(className, trigger);

        _schedulerEngineHelper.register(this, schedulerEntry, DestinationNames.SCHEDULER_DISPATCH);
    }

    @Override
    protected void doReceive(Message message) throws Exception {

        _log.info("doReceive()");
        _log.info("message = " + message);

        // Loop over the preferences of all deployed data-manager portlets

        String portletId = PortletKeys.DATA_MANAGER;
        int ownerType = PortletKeys.PREFS_OWNER_TYPE_GROUP;
        long plid = 0;

        List<PortletPreferences> portletPreferencesList = PortletPreferencesLocalServiceUtil
                .getPortletPreferences(ownerType, plid, portletId);

        for (PortletPreferences portletPreferences : portletPreferencesList) {

            long groupId = portletPreferences.getOwnerId();

            javax.portlet.PortletPreferences preferences = PortletPreferencesFactoryUtil
                    .fromDefaultXML(portletPreferences.getPreferences());

            // Retrieve the configuration parameter

            String dataURL = PrefsPropsUtil.getString(preferences, "dataURL");
            String[] dataURLs = getPreferenceValues(dataURL);
            
            String idField = PrefsPropsUtil.getString(preferences, "idField");
            String[] idFields = getPreferenceValues(idField);
            
            String nameField = PrefsPropsUtil.getString(preferences, "nameField");
            String[] nameFields = getPreferenceValues(nameField);
            
            String timestampField = PrefsPropsUtil.getString(preferences, "timestampField");
            String[] timestampFields = getPreferenceValues(timestampField);
            
            String password = PrefsPropsUtil.getString(preferences, "password");
            String[] passwords = getPreferenceValues(password);
            
            String userId = PrefsPropsUtil.getString(preferences, "userId");
            String[] userIds = getPreferenceValues(userId);
            
            String userName = PrefsPropsUtil.getString(preferences, "userName");
            String[] userNames = getPreferenceValues(userName);

            // loop over configured data sources

            for (int i = 0; i < dataURLs.length; i++) {

                final String user = userNames[i];
                final String pw = passwords[i];

                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pw.toCharArray());
                    }
                });

                File file = null;

                if (Validator.isNotNull(dataURLs[i])) {

                    String tmpDir = SystemProperties.get(SystemProperties.TMP_DIR) + StringPool.SLASH
                            + Time.getTimestamp();

                    URL url = new URL(dataURLs[i]);

                    String fileName = url.getFile();

                    String extension = FileUtil.getExtension(fileName);

                    file = new File(tmpDir + "/data." + extension);

                    FileUtils.copyURLToFile(url, file);

                    Map<String, String[]> parameterMap = new HashMap<String, String[]>();
                    parameterMap.put("idField", new String[] { idFields[i] });
                    parameterMap.put("nameField", new String[] { nameFields[i] });
                    parameterMap.put("timestampField", new String[] { timestampFields[i] });
                    
                    long importUserId = GetterUtil.getLong(userIds[i]);
                    
                    Map<String, Serializable> importMeasurementSettingsMap = ExportImportConfigurationSettingsMapFactory
                            .buildImportMeasurementsSettingsMap(importUserId, groupId, parameterMap,
                                    null, null);
                    
                    ExportImportConfiguration exportImportConfiguration = _exportImportConfigurationLocalService
                            .addDraftExportImportConfiguration(importUserId,
                                    ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT, importMeasurementSettingsMap);

                    _measurementLocalService.importMeasurementsInBackground(importUserId, exportImportConfiguration, file);
                    
                }
            }
        }
    }
    
    @Reference(unbind = "-")
    protected void setExportImportConfigurationLocalService(
            ExportImportConfigurationLocalService exportImportConfigurationLocalService) {

        _exportImportConfigurationLocalService = exportImportConfigurationLocalService;
    }

    @Reference(unbind = "-")
    protected void setMeasurementLocalService(MeasurementLocalService measurementLocalService) {
        _measurementLocalService = measurementLocalService;
    }

    @Reference(unbind = "-")
    protected void setSchedulerEngineHelper(SchedulerEngineHelper schedulerEngineHelper) {

        _schedulerEngineHelper = schedulerEngineHelper;
    }
    
    
    private static String[] getPreferenceValues(String str) {

        String[] preferences = null;

        // Multiple preferences with empty values are stored correctly with
        // trailing comma, but the split method does not return empty strings
        // into the array.

        if (str.endsWith(StringPool.COMMA)) {
            preferences = (str + StringPool.SPACE).split(StringPool.COMMA);
        } else {
            preferences = str.split(StringPool.COMMA);
        }

        return preferences;
    }

    private ExportImportConfigurationLocalService _exportImportConfigurationLocalService;
    private static final Log _log = LogFactoryUtil.getLog(ImportDataMessageListener.class.getName());
    private MeasurementLocalService _measurementLocalService;

    private SchedulerEngineHelper _schedulerEngineHelper;

    @Reference
    private TriggerFactory _triggerFactory;
}
