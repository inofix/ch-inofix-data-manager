package ch.inofix.data.controller;

import static ch.inofix.data.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_MEASUREMENTS_IMPORT_FAILED;
import static ch.inofix.data.internal.exportimport.util.ExportImportLifecycleConstants.PROCESS_FLAG_MEASUREMENTS_IMPORT_IN_PROCESS;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletPreferences;

import org.apache.commons.lang.time.StopWatch;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.controller.ImportController;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import ch.inofix.data.exception.FileFormatException;
import ch.inofix.data.internal.exportimport.util.ExportImportThreadLocal;
import ch.inofix.data.service.MeasurementLocalService;

/**
 *
 * @author Christian Berndt
 * @created 2017-06-04 18:07
 * @modified 2017-11-21 18:57
 * @version 1.1.1
 *
 */
@Component(
    immediate = true, 
    property = { "model.class.name=ch.inofix.data.model.Measurement" }, 
    service = {
        ExportImportController.class, 
        MeasurementImportController.class 
    }
)
public class MeasurementImportController extends BaseExportImportController implements ImportController {

    public MeasurementImportController() {
        initXStream();
    }

    @Override
    public void importDataDeletions(ExportImportConfiguration exportImportConfiguration, File file) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void importFile(ExportImportConfiguration exportImportConfiguration, File file) throws Exception {

        PortletDataContext portletDataContext = null;

        try {
            ExportImportThreadLocal.setMeasurementImportInProcess(true);

            // TODO: process import-settings
            // Map<String, Serializable> settingsMap =
            // exportImportConfiguration.getSettingsMap();

            doImportFile(file, exportImportConfiguration.getUserId(), exportImportConfiguration.getGroupId());
            ExportImportThreadLocal.setMeasurementImportInProcess(false);

        } catch (Throwable t) {
            ExportImportThreadLocal.setMeasurementImportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_MEASUREMENTS_IMPORT_FAILED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext), t);

            throw t;
        }
    }

    @Override
    public MissingReferences validateFile(ExportImportConfiguration exportImportConfiguration, File file)
            throws Exception {

        throw new UnsupportedOperationException();

    }

    protected void doImportFile(File file, long userId, long groupId) throws Exception {
        
        _log.info("doImportFile");

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setScopeGroupId(groupId);
        serviceContext.setUserId(userId);
        
        String extension = FileUtil.getExtension(file.getName().toLowerCase());        
        
        _log.info("extension = " + extension);     

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        int numAdded = 0;
        int numProcessed = 0;
        int numImported = 0;
        int numIgnored = 0;
        
        if ("xml".equals(extension)) {

            Document document = SAXReaderUtil.read(file);

            // TODO: read xPath selector from configuration
            String selector = "//ChannelData";
            List<Node> channels = document.selectNodes(selector);
            int numValues = document.selectNodes("//VT").size();

            for (Node channel : channels) {

                Element channelElement = (Element) channel;

                String id = channelElement.attributeValue("channelId");
                String name = channelElement.attributeValue("name");
                String unit = channelElement.attributeValue("unit");

                List<Node> values = channel.selectNodes("descendant::VT");

                for (Node value : values) {

                    Element valueElement = (Element) value;
                    String timestamp = valueElement.attributeValue("t");
                    String val = valueElement.getText();

                    JSONObject jsonObject = createJSONObject(id, name, unit, timestamp, val);

                    int status = addMeasurement(serviceContext, userId, jsonObject);

                    if (status == IGNORED) {
                        numIgnored++;
                    }

                    if (status == IMPORTED) {
                        numImported++;
                    }

                    if (numProcessed % 100 == 0 && numProcessed > 0) {

                        float completed = ((Integer) numProcessed).floatValue() / numValues * 100;

                        _log.info("Processed " + numProcessed + " of " + numValues + " measurements in "
                                + stopWatch.getTime() + " ms (" + completed + "%).");
                    }

                    numProcessed++;

                }

            }

        } else if ("json".equals(extension)) {

            _log.info("import json");

            String json = new String(FileUtil.getBytes(file));
            JSONArray jsonArray = JSONFactoryUtil.createJSONArray(json);

            int numValues = jsonArray.length();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int status = addMeasurement(serviceContext, userId, jsonObject);

                if (status == IGNORED) {
                    numIgnored++;
                }

                if (status == IMPORTED) {
                    numImported++;
                }

                if (numProcessed % 100 == 0 && numProcessed > 0) {

                    float completed = ((Integer) numProcessed).floatValue() / numValues * 100;

                    _log.info("Processed " + numProcessed + " of " + numValues + " measurements in "
                            + stopWatch.getTime() + " ms (" + completed + "%).");
                }

                numProcessed++;

            }

        } else {
            throw new FileFormatException();
        }

        if (_log.isInfoEnabled()) {
            _log.info("Importing measurements takes " + stopWatch.getTime() + " ms.");
            _log.info("Added " + numAdded + " measurements as new, since they did not have a measurementId.");
            _log.info("Ignored " + numIgnored + " measurements since they already exist in this instance.");
            _log.info("Imported " + numImported + " measurements since they did not exist in this instance.");
        }

    }
    
    protected PortletPreferences getPreferences(long groupId) throws PortalException {

        long ownerId = groupId;
        int ownerType = PortletKeys.PREFS_OWNER_TYPE_GROUP;
        long plid = 0;
        PortletPreferences preferences = null;

        try {

            com.liferay.portal.kernel.model.PortletPreferences portletPreferences = _portletPreferencesLocalService
                    .getPortletPreferences(ownerId, ownerType, plid, ch.inofix.data.constants.PortletKeys.DATA_MANAGER);

            preferences = PortletPreferencesFactoryUtil.fromDefaultXML(portletPreferences.getPreferences());

        } catch (NoSuchPortletPreferencesException e) {
            _log.warn(e.getMessage());
        }

        return preferences;

    }

    protected int getProcessFlag() {

        return PROCESS_FLAG_MEASUREMENTS_IMPORT_IN_PROCESS;
    }

    @Reference(unbind = "-")
    protected void setExportImportLifecycleManager(ExportImportLifecycleManager exportImportLifecycleManager) {

        _exportImportLifecycleManager = exportImportLifecycleManager;
    }

    @Reference(unbind = "-")
    protected void setMeasurementLocalService(MeasurementLocalService measurementLocalService) {
        this._measurementLocalService = measurementLocalService;
    }
    
    @Reference(unbind = "-")
    protected void setPortletPreferencesLocalService(PortletPreferencesLocalService portletPreferencesLocalService) {
        _portletPreferencesLocalService = portletPreferencesLocalService;
    }

    private int addMeasurement(ServiceContext serviceContext, long userId, JSONObject jsonObject) throws Exception {

        // Read timestamp field name from configuration

        PortletPreferences preferences = getPreferences(serviceContext.getScopeGroupId());

        String timestampField = "timestamp"; 
        
        if (preferences != null) {
            timestampField = preferences.getValue("timestampField", "timestamp");
        }

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        Date timestamp = getDate(jsonObject.getString(timestampField)); 
        
        _log.info("id = " + id);
        _log.info("name = " + name);
        _log.info("timestamp = " + timestamp);

        Hits hits = _measurementLocalService.search(userId, serviceContext.getScopeGroupId(), null, id, timestamp,
                WorkflowConstants.STATUS_ANY, null, null, null, true, 0, Integer.MAX_VALUE, null);
        
        _log.info("hits.getLength()  = " + hits.getLength());

        if (hits.getLength() == 0) {

            _measurementLocalService.addMeasurement(userId, jsonObject.toString(), id, name, timestamp, serviceContext);

            return IMPORTED;

        } else {

            return IGNORED;
        }
    }
    
    private static JSONObject createJSONObject(String id, String name, String unit, String timestamp, String value) {

        // TODO: read json-schema from portlet configuration
        // (see above)
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
        jsonObject.put("channelId", id);
        jsonObject.put("channelName", name);
        jsonObject.put("channelUnit", unit);
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("value", value);

        return jsonObject;

    }
    
    private static Date getDate(String str) {

        Date date = null;

        if (Validator.isNotNull(str)) {

            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");
                date = dateFormat.parse(str);
            } catch (ParseException e) {
                _log.error(e.getMessage());
            }

        }

        return date;

    }
    
    private static int IGNORED = 0;
    private static int IMPORTED = 1;

    private ExportImportLifecycleManager _exportImportLifecycleManager;
    private MeasurementLocalService _measurementLocalService;
    private PortletPreferencesLocalService _portletPreferencesLocalService;

    private static final Log _log = LogFactoryUtil.getLog(MeasurementImportController.class.getName());

}
