package ch.inofix.data.web.internal.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import ch.inofix.data.model.Measurement;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-27 11:57
 * @modified 2017-11-21 18:11
 * @version 1.0.2
 *
 */
public class MeasurementSearch extends SearchContainer<Measurement> {

    public static final String EMPTY_RESULTS_MESSAGE = "no-measurements-were-found";

    static List<String> headerNames = new ArrayList<String>();
    static Map<String, String> orderableHeaders = new HashMap<String, String>();

    static {
        headerNames.add("create-date");
        headerNames.add("data");
        headerNames.add("from");
        headerNames.add("id");
        headerNames.add("measurement-id");
        headerNames.add("modified-date");
        headerNames.add("name");
        headerNames.add("status"); 
        headerNames.add("timestamp"); 
        headerNames.add("user-name");
        headerNames.add("until");

        orderableHeaders.put("create-date", "createDate_Number_sortable");
        orderableHeaders.put("data", "data_sortable");
        orderableHeaders.put("from", "from_Number_sortable");
        orderableHeaders.put("id", "id_sortable");
        orderableHeaders.put("measurement-id", "taskRecordId_Number_sortable");
        orderableHeaders.put("modified-date", "modified_sortable");
        orderableHeaders.put("name", "name");
        orderableHeaders.put("status", "status_Number_sortable");
        orderableHeaders.put("timestamp", "timestamp_sortable");
        orderableHeaders.put("until", "until_Number_sortable");
        orderableHeaders.put("user-name", "userName_sortable");
    }

    public MeasurementSearch(PortletRequest portletRequest, PortletURL iteratorURL) {
        this(portletRequest, DEFAULT_CUR_PARAM, iteratorURL);
    }

    public MeasurementSearch(PortletRequest portletRequest, String curParam, PortletURL iteratorURL) {

        super(portletRequest, new MeasurementDisplayTerms(portletRequest), new MeasurementSearchTerms(portletRequest),
                curParam, DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

        PortletConfig portletConfig = (PortletConfig) portletRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);

        MeasurementDisplayTerms displayTerms = (MeasurementDisplayTerms) getDisplayTerms();
        MeasurementSearchTerms searchTerms = (MeasurementSearchTerms) getSearchTerms();

        String portletId = PortletProviderUtil.getPortletId(User.class.getName(), PortletProvider.Action.VIEW);
        String portletName = portletConfig.getPortletName();

        if (!portletId.equals(portletName)) {
            displayTerms.setStatus(WorkflowConstants.STATUS_APPROVED);
            searchTerms.setStatus(WorkflowConstants.STATUS_APPROVED);
        }

        iteratorURL.setParameter(MeasurementDisplayTerms.CREATE_DATE, displayTerms.getCreateDate());
        iteratorURL.setParameter(MeasurementDisplayTerms.DATA, displayTerms.getData());
        iteratorURL.setParameter(MeasurementDisplayTerms.MEASUREMENT_ID,
                String.valueOf(displayTerms.getMeasurementId()));
        iteratorURL.setParameter(MeasurementDisplayTerms.STATUS, String.valueOf(displayTerms.getStatus()));
        iteratorURL.setParameter(MeasurementDisplayTerms.USER_NAME, displayTerms.getUserName());
        iteratorURL.setParameter(MeasurementDisplayTerms.MODIFIED_DATE, displayTerms.getModifiedDate());
        iteratorURL.setParameter(MeasurementDisplayTerms.FROM, String.valueOf(displayTerms.getFrom()));
        iteratorURL.setParameter(MeasurementDisplayTerms.UNTIL, String.valueOf(displayTerms.getUntil()));

        try {
            PortalPreferences preferences = PortletPreferencesFactoryUtil.getPortalPreferences(portletRequest);

            String orderByCol = ParamUtil.getString(portletRequest, "orderByCol");
            String orderByType = ParamUtil.getString(portletRequest, "orderByType");

            if (Validator.isNotNull(orderByCol) && Validator.isNotNull(orderByType)) {
                preferences.setValue(portletId, "measurements-order-by-col", orderByCol);
                preferences.setValue(portletId, "measurements-order-by-type", orderByType);
            } else {
                orderByCol = preferences.getValue(portletId, "measurements-order-by-col", "modified-date");
                orderByType = preferences.getValue(portletId, "measurements-order-by-type", "asc");
            }

            setOrderableHeaders(orderableHeaders);

            if (Validator.isNotNull(orderableHeaders.get(orderByCol))) {
                setOrderByCol(orderableHeaders.get(orderByCol));
            } else {
                _log.error(orderByCol + " is not an orderable header.");
                setOrderByCol(orderByCol);
            }

            setOrderByType(orderByType);

        } catch (Exception e) {
            _log.error(e);
        }
    }

    public static final Log _log = LogFactoryUtil.getLog(MeasurementSearch.class.getName());
}
