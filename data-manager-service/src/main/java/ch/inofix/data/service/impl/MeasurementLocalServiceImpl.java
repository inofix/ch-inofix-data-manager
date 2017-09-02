package ch.inofix.data.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.exportimport.kernel.controller.ExportController;
import com.liferay.exportimport.kernel.controller.ExportImportControllerRegistryUtil;
import com.liferay.exportimport.kernel.controller.ImportController;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;

import ch.inofix.data.background.task.MeasurementExportBackgroundTaskExecutor;
import ch.inofix.data.background.task.MeasurementImportBackgroundTaskExecutor;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.base.MeasurementLocalServiceBaseImpl;

/**
 * The implementation of the measurement local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.data.service.MeasurementLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author Christian Berndt
 * @created 2017-03-08 19:46
 * @modified 2017-09-02 07:26
 * @version 1.1.1
 * @see MeasurementLocalServiceBaseImpl
 * @see ch.inofix.data.service.MeasurementLocalServiceUtil
 */
public class MeasurementLocalServiceImpl extends MeasurementLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.data.service.MeasurementLocalServiceUtil} to access the
     * measurement local service.
     */

    @Override
    @Indexable(type = IndexableType.REINDEX)
    public Measurement addMeasurement(long userId, String data, ServiceContext serviceContext) throws PortalException {

        // Measurement

        User user = userPersistence.findByPrimaryKey(userId);
        long groupId = serviceContext.getScopeGroupId();

        long measurementId = counterLocalService.increment();

        Measurement measurement = measurementPersistence.create(measurementId);

        measurement.setUuid(serviceContext.getUuid());
        measurement.setGroupId(groupId);
        measurement.setCompanyId(user.getCompanyId());
        measurement.setUserId(user.getUserId());
        measurement.setUserName(user.getFullName());
        measurement.setExpandoBridgeAttributes(serviceContext);

        measurement.setData(data);

        measurementPersistence.update(measurement);

        return measurement;

    }

    @Override
    public List<Measurement> deleteGroupMeasurements(long groupId) throws PortalException {

        List<Measurement> measurements = measurementPersistence.findByGroupId(groupId);

        for (Measurement measurement : measurements) {
            deleteMeasurement(measurement);
        }

        return measurements;

    }

    @Indexable(type = IndexableType.DELETE)
    @Override
    @SystemEvent(type = SystemEventConstants.TYPE_DELETE)
    public Measurement deleteMeasurement(Measurement measurement) throws PortalException {

        // Measurement

        measurementPersistence.remove(measurement);

        return measurement;
    }

    @Override
    public Measurement deleteMeasurement(long measurementId) throws PortalException {

        Measurement measurement = measurementPersistence.findByPrimaryKey(measurementId);

        return measurementLocalService.deleteMeasurement(measurement);
    }

    @Override
    public File exportMeasurementsAsFile(ExportImportConfiguration exportImportConfiguration) throws PortalException {

        try {
            ExportController measurementExportController = ExportImportControllerRegistryUtil
                    .getExportController(Measurement.class.getName());

            return measurementExportController.export(exportImportConfiguration);

        } catch (PortalException pe) {
            _log.error(pe);
            throw pe;
        } catch (Exception e) {
            _log.error(e);
            throw new SystemException(e);
        }
    }

    @Override
    public long exportMeasurementsAsFileInBackground(long userId, ExportImportConfiguration exportImportConfiguration)
            throws PortalException {

        // TODO: The export may have different file types / extensions:
        // - .csv
        // - .xml
        // - .txt
        // - .json
        // if
        // (!DLValidatorUtil.isValidName(exportImportConfiguration.getName())) {
        // throw new LARFileNameException(exportImportConfiguration.getName());
        // }

        Map<String, Serializable> taskContextMap = new HashMap<>();

        taskContextMap.put("exportImportConfigurationId", exportImportConfiguration.getExportImportConfigurationId());

        BackgroundTask backgroundTask = BackgroundTaskManagerUtil.addBackgroundTask(userId,
                exportImportConfiguration.getGroupId(), exportImportConfiguration.getName(),
                MeasurementExportBackgroundTaskExecutor.class.getName(), taskContextMap, new ServiceContext());

        return backgroundTask.getBackgroundTaskId();
    }

    @Override
    public void importMeasurements(ExportImportConfiguration exportImportConfiguration, File file)
            throws PortalException {

        try {
            ImportController measurementImportController = ExportImportControllerRegistryUtil
                    .getImportController(Measurement.class.getName());

            measurementImportController.importFile(exportImportConfiguration, file);

        } catch (PortalException pe) {
            Throwable cause = pe.getCause();

            if (cause instanceof LocaleException) {
                throw (PortalException) cause;
            }

            throw pe;
        } catch (SystemException se) {
            throw se;
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void importMeasurements(ExportImportConfiguration exportImportConfiguration, InputStream inputStream)
            throws PortalException {

        File file = null;

        try {

            // TODO: use extension from upload
            file = FileUtil.createTempFile("lar");

            FileUtil.write(file, inputStream);

            importMeasurements(exportImportConfiguration, file);

        } catch (IOException ioe) {
            throw new SystemException(ioe);
        } finally {
            FileUtil.delete(file);
        }
    }

    @Override
    public long importMeasurementsInBackground(long userId, ExportImportConfiguration exportImportConfiguration,
            File file) throws PortalException {

        Map<String, Serializable> taskContextMap = new HashMap<>();

        taskContextMap.put("exportImportConfigurationId", exportImportConfiguration.getExportImportConfigurationId());

        BackgroundTask backgroundTask = BackgroundTaskManagerUtil.addBackgroundTask(userId,
                exportImportConfiguration.getGroupId(), exportImportConfiguration.getName(),
                MeasurementImportBackgroundTaskExecutor.class.getName(), taskContextMap, new ServiceContext());

        backgroundTask.addAttachment(userId, file.getName(), file);

        return backgroundTask.getBackgroundTaskId();
    }

    @Override
    public long importMeasurementsInBackground(long userId, ExportImportConfiguration exportImportConfiguration,
            InputStream inputStream) throws PortalException {

        _log.info("importMeasurementsInBackground()");

        File file = null;

        try {

            file = FileUtil.createTempFile("lar");

            FileUtil.write(file, inputStream);

            return importMeasurementsInBackground(userId, exportImportConfiguration, file);

        } catch (IOException ioe) {
            throw new SystemException(ioe);
        } finally {
            FileUtil.delete(file);
        }
    }
    
    /**
     * Returns an ordered range of all the measurements whose channelId, or
     * timestamp fields match the keywords specified for them, using the
     * indexer.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end -
     * start</code> instances. <code>start</code> and <code>end</code> are not
     * primary keys, they are indexes in the result set. Thus, <code>0</code>
     * refers to the first result in the set. Setting both <code>start</code>
     * and <code>end</code> to
     * {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return
     * the full result set.
     * </p>
     *
     *
     * @param companyId
     * @param channelId
     * @param timestamp
     * @param andSearch
     * @param start
     * @param end
     * @param sort
     * @return
     * @throws SystemException
     */
    @Override
    public Hits search(long companyId, long groupId, String channelId,
            String channelName, String timestamp, boolean andSearch, int start,
            int end, Sort sort) throws PortalException {

        return search(companyId, groupId, channelId, channelName, timestamp, 0,
                0, andSearch, start, end, sort);

    }

    /**
     *
     * @param companyId
     * @param groupId
     * @param channelId
     * @param channelName
     * @param from
     * @param until
     * @param andSearch
     * @param start
     * @param end
     * @param sort
     * @return
     * @throws SystemException
     */
    @Override
    public Hits search(long companyId, long groupId, String channelId,
            String channelName, long from, long until, boolean andSearch,
            int start, int end, Sort sort) throws PortalException {

        return search(companyId, groupId, channelId, channelName, null, from,
                until, andSearch, start, end, sort);
    }

    @Override
    public Hits search(long companyId, long groupId, String channelId,
            String channelName, String timestamp, long from, long until,
            boolean andSearch, int start, int end, Sort sort)
            throws PortalException {

        if (Validator.isNull(sort)) {
            sort = new Sort("timestamp", true);
        }

        try {

            SearchContext searchContext = new SearchContext();

            searchContext.setCompanyId(companyId);
            searchContext.setGroupIds(new long[] { groupId });

            searchContext.setAttribute("paginationType", "more");
            searchContext.setStart(start);
            searchContext.setEnd(end);

            searchContext.setAndSearch(andSearch);

            Map<String, Serializable> attributes = new HashMap<String, Serializable>();

            attributes.put("channelId", channelId);
            attributes.put("channelName", channelName);
            attributes.put("timestamp", timestamp);
            attributes.put("from", from);
            attributes.put("until", until);

            searchContext.setAttributes(attributes);

            // Always add facets as late as possible so that the search context
            // fields can be considered by the facets

            List<Facet> facets = new ArrayList<Facet>();

            if (Validator.isNotNull(channelId)) {
                Facet facet = new MultiValueFacet(searchContext);
                facet.setFieldName("channelId");
                facets.add(facet);
            }

            if (Validator.isNotNull(channelName)) {
                Facet facet = new MultiValueFacet(searchContext);
                facet.setFieldName("channelName");
                facets.add(facet);
            }

            if (Validator.isNotNull(timestamp)) {
                Facet facet = new MultiValueFacet(searchContext);
                facet.setFieldName("timestamp");
                facets.add(facet);
            }

            searchContext.setFacets(facets);
            searchContext.setSorts(sort);

            Indexer<Measurement> indexer = IndexerRegistryUtil
                    .nullSafeGetIndexer(Measurement.class);

            return indexer.search(searchContext);

        } catch (Exception e) {
            throw new SystemException(e);
        }

    }

    @Override
    @Indexable(type = IndexableType.REINDEX)
    public Measurement updateMeasurement(long measurementId, long userId,
            String data, ServiceContext serviceContext) throws PortalException {

        // Measurement

        User user = userPersistence.findByPrimaryKey(userId);

        Measurement measurement = measurementPersistence
                .findByPrimaryKey(measurementId);

        long groupId = serviceContext.getScopeGroupId();

        measurement.setUuid(serviceContext.getUuid());
        measurement.setGroupId(groupId);
        measurement.setCompanyId(user.getCompanyId());
        measurement.setUserId(user.getUserId());
        measurement.setUserName(user.getFullName());
        measurement.setExpandoBridgeAttributes(serviceContext);

        measurement.setData(data);

        measurementPersistence.update(measurement);

        return measurement;

    }

    private static final Log _log = LogFactoryUtil.getLog(MeasurementLocalServiceImpl.class.getName());

}
