package ch.inofix.data.search;

import java.util.LinkedHashMap;
import java.util.Locale;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementLocalService;
import ch.inofix.data.service.permission.MeasurementPermission;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-27 10:52
 * @modified 2017-09-27 10:52
 * @version 1.1.3
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
    public boolean hasPermission(PermissionChecker permissionChecker, String entryClassName, long entryClassPK,
            String actionId) throws Exception {
        return MeasurementPermission.contains(permissionChecker, entryClassPK, ActionKeys.VIEW);
    }

    @Override
    public void postProcessContextBooleanFilter(BooleanFilter contextBooleanFilter, SearchContext searchContext)
            throws Exception {
        
        if (_log.isDebugEnabled()) {
            _log.debug("postProcessSearchQuery()");
        }

        addStatus(contextBooleanFilter, searchContext);

        // from- and until-date

        // TODO
//        Date fromDate = GetterUtil.getDate(searchContext.getAttribute("fromDate"), DateFormat.getDateInstance(), null);
//        Date untilDate = GetterUtil.getDate(searchContext.getAttribute("untilDate"), DateFormat.getDateInstance(),
//                null);
//
//        long max = Long.MAX_VALUE;
//        long min = Long.MIN_VALUE;
//
//        if (fromDate != null) {
//            min = fromDate.getTime();
//        }
//
//        if (untilDate != null) {
//            max = untilDate.getTime();
//        }
//
//        contextBooleanFilter.addRangeTerm("fromDate_Number_sortable", min, max);

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

    private static final Log _log = LogFactoryUtil.getLog(MeasurementIndexer.class);

    private MeasurementLocalService _measurementLocalService;
}
