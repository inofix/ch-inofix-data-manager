package ch.inofix.data.controller;

import static ch.inofix.data.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_MEASUREMENTS_EXPORT_FAILED;
import static ch.inofix.data.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_MEASUREMENTS_EXPORT_STARTED;
import static ch.inofix.data.internal.exportimport.util.ExportImportLifecycleConstants.EVENT_MEASUREMENTS_EXPORT_SUCCEEDED;
import static ch.inofix.data.internal.exportimport.util.ExportImportLifecycleConstants.PROCESS_FLAG_MEASUREMENTS_EXPORT_IN_PROCESS;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.exportimport.kernel.controller.ExportController;
import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.zip.ZipWriter;

import ch.inofix.data.internal.exportimport.util.ExportImportThreadLocal;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementLocalService;

/**
 * @author Christian Berndt
 * @created 2017-11-02 23:04
 * @modified 2017-11-02 23:04
 * @version 1.0.0
 */
@Component(
    immediate = true, 
    property = { "model.class.name=ch.inofix.data.model.Measurement" }, 
    service = {
        ExportImportController.class, 
        MeasurementExportController.class 
    }
)
public class MeasurementExportController extends BaseExportImportController implements ExportController {

    public MeasurementExportController() {
        initXStream();
    }

    @Override
    public File export(ExportImportConfiguration exportImportConfiguration) throws Exception {

        PortletDataContext portletDataContext = null;

        try {

            ExportImportThreadLocal.setMeasurementExportInProcess(true);

            portletDataContext = getPortletDataContext(exportImportConfiguration);

            exportImportConfiguration.getSettingsMap();

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_MEASUREMENTS_EXPORT_STARTED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext));

            File file = doExport(portletDataContext);

            ExportImportThreadLocal.setMeasurementExportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_MEASUREMENTS_EXPORT_SUCCEEDED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext));

            return file;

        } catch (Throwable t) {

            _log.error(t);

            ExportImportThreadLocal.setMeasurementExportInProcess(false);

            _exportImportLifecycleManager.fireExportImportLifecycleEvent(EVENT_MEASUREMENTS_EXPORT_FAILED,
                    getProcessFlag(), PortletDataContextFactoryUtil.clonePortletDataContext(portletDataContext), t);

            throw t;
        }
    }

    protected File doExport(PortletDataContext portletDataContext) throws Exception {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        final StringBuilder sb = new StringBuilder();

        sb.append("<Measurements>");
        sb.append(StringPool.NEW_LINE);

        ActionableDynamicQuery actionableDynamicQuery = _measurementLocalService.getActionableDynamicQuery();

        actionableDynamicQuery.setGroupId(portletDataContext.getGroupId());

        // TODO: process date-range of portletDataContext

        actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<Measurement>() {

            @Override
            public void performAction(Measurement measurement) {
                String xml = _xStream.toXML(measurement);
                sb.append(xml);
                sb.append(StringPool.NEW_LINE);
            }

        });

        actionableDynamicQuery.performActions();

        sb.append("</Measurements>");

        if (_log.isInfoEnabled()) {
            _log.info("Exporting measurements takes " + stopWatch.getTime() + " ms");
        }

        portletDataContext.addZipEntry("/Measurements.xml", sb.toString());

        ZipWriter zipWriter = portletDataContext.getZipWriter();

        return zipWriter.getFile();

    }

    protected PortletDataContext getPortletDataContext(ExportImportConfiguration exportImportConfiguration)
            throws PortalException {

        Map<String, Serializable> settingsMap = exportImportConfiguration.getSettingsMap();

        // String fileName = MapUtil.getString(settingsMap, "fileName");

        long sourcePlid = MapUtil.getLong(settingsMap, "sourcePlid");
        long sourceGroupId = MapUtil.getLong(settingsMap, "sourceGroupId");
        String portletId = MapUtil.getString(settingsMap, "portletId");
        Map<String, String[]> parameterMap = (Map<String, String[]>) settingsMap.get("parameterMap");
        DateRange dateRange = ExportImportDateUtil.getDateRange(exportImportConfiguration);

        Layout layout = _layoutLocalService.getLayout(sourcePlid);
        ZipWriter zipWriter = ExportImportHelperUtil.getPortletZipWriter(portletId);

        PortletDataContext portletDataContext = PortletDataContextFactoryUtil.createExportPortletDataContext(
                layout.getCompanyId(), sourceGroupId, parameterMap, dateRange.getStartDate(), dateRange.getEndDate(),
                zipWriter);

        portletDataContext.setOldPlid(sourcePlid);
        portletDataContext.setPlid(sourcePlid);
        portletDataContext.setPortletId(portletId);

        return portletDataContext;
    }

    protected int getProcessFlag() {
        return PROCESS_FLAG_MEASUREMENTS_EXPORT_IN_PROCESS;
    }

    @Reference(unbind = "-")
    protected void setExportImportLifecycleManager(ExportImportLifecycleManager exportImportLifecycleManager) {
        _exportImportLifecycleManager = exportImportLifecycleManager;
    }

    @Reference(unbind = "-")
    protected void setLayoutLocalService(LayoutLocalService layoutLocalService) {
        _layoutLocalService = layoutLocalService;
    }

    @Reference(unbind = "-")
    protected void setMeasurementLocalService(MeasurementLocalService measurementLocalService) {

        _measurementLocalService = measurementLocalService;
    }

    private static final Log _log = LogFactoryUtil.getLog(MeasurementExportController.class);

    private ExportImportLifecycleManager _exportImportLifecycleManager;
    private LayoutLocalService _layoutLocalService;
    private MeasurementLocalService _measurementLocalService;

}
