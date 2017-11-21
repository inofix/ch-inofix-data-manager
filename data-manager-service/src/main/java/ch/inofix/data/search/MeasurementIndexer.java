package ch.inofix.data.search;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementLocalService;
import ch.inofix.data.service.util.JSONSchemaUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-27 10:52
 * @modified 2017-11-21
 * @version 1.1.7
 *
 */
@Component(immediate = true, service = Indexer.class)
public class MeasurementIndexer extends BaseIndexer<Measurement> {

    public static final String CLASS_NAME = Measurement.class.getName();

    public MeasurementIndexer() {
        setDefaultSelectedFieldNames(Field.ASSET_TAG_NAMES, Field.COMPANY_ID, Field.ENTRY_CLASS_NAME,
                Field.ENTRY_CLASS_PK, Field.GROUP_ID, Field.MODIFIED_DATE, Field.SCOPE_GROUP_ID, Field.UID);
        setFilterSearch(true);
        setPermissionAware(true);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    public void postProcessContextBooleanFilter(BooleanFilter contextBooleanFilter, SearchContext searchContext)
            throws Exception {
        
        _log.info("postProcessContextBooleanFilter");

        addStatus(contextBooleanFilter, searchContext);        

        // id

        String id = (String) searchContext.getAttribute("id");

        if (Validator.isNotNull(id)) {

            contextBooleanFilter.addRequiredTerm("id", id);
        }

        // timestamp

        long timestamp = GetterUtil.getLong(searchContext.getAttribute("timestamp"));

        if (timestamp > 0) {
            contextBooleanFilter.addRequiredTerm("timestamp", timestamp);
        }

        // from- and until-date

        // TODO
        // Date fromDate =
        // GetterUtil.getDate(searchContext.getAttribute("fromDate"),
        // DateFormat.getDateInstance(), null);
        // Date untilDate =
        // GetterUtil.getDate(searchContext.getAttribute("untilDate"),
        // DateFormat.getDateInstance(),
        // null);
        //
        // long max = Long.MAX_VALUE;
        // long min = Long.MIN_VALUE;
        //
        // if (fromDate != null) {
        // min = fromDate.getTime();
        // }
        //
        // if (untilDate != null) {
        // max = untilDate.getTime();
        // }
        //
        // contextBooleanFilter.addRangeTerm("fromDate_Number_sortable", min,
        // max);

    }

    @Override
    public void postProcessSearchQuery(BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
            SearchContext searchContext) throws Exception {
                
        addSearchTerm(searchQuery, searchContext, "data", false);

        LinkedHashMap<String, Object> params = (LinkedHashMap<String, Object>) searchContext.getAttribute("params");

        if (params != null) {
            String expandoAttributes = (String) params.get("expandoAttributes");

            if (Validator.isNotNull(expandoAttributes)) {
                addSearchExpando(searchQuery, searchContext, expandoAttributes);
            }
        }
    }

    @Override
    protected void doDelete(Measurement measurement) throws Exception {
        
        deleteDocument(measurement.getCompanyId(), measurement.getMeasurementId());
    }

    @Override
    protected Document doGetDocument(Measurement measurement) throws Exception {

        Document document = getBaseModelDocument(CLASS_NAME, measurement);

        document.addDateSortable(Field.CREATE_DATE, measurement.getCreateDate());
        document.addTextSortable("data", measurement.getData());
        document.addTextSortable("id", measurement.getId());
        document.addTextSortable("name", measurement.getName());
        document.addNumberSortable(Field.STATUS, WorkflowConstants.STATUS_APPROVED);

        if (measurement.getTimestamp() != null) {
            document.addNumber("timestamp", measurement.getTimestamp().getTime());
        }

        PortletPreferences portletPreferences = getPreferences(measurement.getGroupId());
        String json = portletPreferences.getValue("jsonSchema", "{}");
        JSONObject jsonSchema = JSONFactoryUtil.createJSONObject(json);
        List<String> schemaFields = JSONSchemaUtil.getFields(jsonSchema);

        String data = measurement.getData();
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject(data);

        // Index JSON data according to schema

        for (String schemaField : schemaFields) {

            if (jsonObject != null) {

                String value = jsonObject.getString(schemaField);

                if (Validator.isNotNull(value)) {
                    document.addTextSortable("json_" + schemaField, value);

                }
            }
        }

        return document;

    }

    @Override
    protected Summary doGetSummary(Document document, Locale locale, String snippet, PortletRequest portletRequest,
            PortletResponse portletResponse) throws Exception {
        
        Summary summary = createSummary(document, Field.TITLE, Field.CONTENT);

        return summary;
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {
        
        Measurement measurement = _measurementLocalService.getMeasurement(classPK);

        doReindex(measurement);
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {

        long companyId = GetterUtil.getLong(ids[0]);
        reindexMeasurements(companyId);
    }

    @Override
    protected void doReindex(Measurement measurement) throws Exception {

        Document document = getDocument(measurement);

        IndexWriterHelperUtil.updateDocument(getSearchEngineId(), measurement.getCompanyId(), document,
                isCommitImmediately());
    }
    
    protected PortletPreferences getPreferences(long groupId) throws PortalException {

        long ownerId = groupId;
        int ownerType = PortletKeys.PREFS_OWNER_TYPE_GROUP;
        long plid = 0;

        com.liferay.portal.kernel.model.PortletPreferences portletPreferences = _portletPreferencesLocalService
                .getPortletPreferences(ownerId, ownerType, plid, ch.inofix.data.constants.PortletKeys.DATA_MANAGER);

        PortletPreferences preferences = PortletPreferencesFactoryUtil
                .fromDefaultXML(portletPreferences.getPreferences());

        return preferences;

    }

    protected void reindexMeasurements(long companyId) throws PortalException {

        final IndexableActionableDynamicQuery indexableActionableDynamicQuery = _measurementLocalService
                .getIndexableActionableDynamicQuery();

        indexableActionableDynamicQuery.setAddCriteriaMethod(new ActionableDynamicQuery.AddCriteriaMethod() {

            @Override
            public void addCriteria(DynamicQuery dynamicQuery) {

                Property statusProperty = PropertyFactoryUtil.forName("status");

                Integer[] statuses = { WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_IN_TRASH };

                dynamicQuery.add(statusProperty.in(statuses));
            }

        });
        indexableActionableDynamicQuery.setCompanyId(companyId);
        // TODO: what about the group?
        // indexableActionableDynamicQuery.setGroupId(groupId);
        indexableActionableDynamicQuery
                .setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<Measurement>() {

                    @Override
                    public void performAction(Measurement measurement) {
                        try {
                            Document document = getDocument(measurement);

                            indexableActionableDynamicQuery.addDocuments(document);
                        } catch (PortalException pe) {
                            if (_log.isWarnEnabled()) {
                                _log.warn("Unable to index measurement " + measurement.getMeasurementId(), pe);
                            }
                        }
                    }

                });
        indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

        indexableActionableDynamicQuery.performActions();
    }

    @Reference(unbind = "-")
    protected void setMeasurementLocalService(MeasurementLocalService measurementLocalService) {

        _measurementLocalService = measurementLocalService;
    }
    
    @Reference(unbind = "-")
    protected void setPortletPreferencesLocalService(PortletPreferencesLocalService portletPreferencesLocalService) {
        _portletPreferencesLocalService = portletPreferencesLocalService;
    }

    private MeasurementLocalService _measurementLocalService;
    private PortletPreferencesLocalService _portletPreferencesLocalService;
    
    private static final Log _log = LogFactoryUtil.getLog(MeasurementIndexer.class);

}
