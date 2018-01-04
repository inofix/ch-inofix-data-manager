package ch.inofix.data.web.internal.portlet.action;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.data.constants.DataManagerField;
import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementServiceUtil;
import ch.inofix.data.service.util.MeasurementUtil;
import ch.inofix.data.web.internal.search.MeasurementSearch;

/**
 * 
 * @author Christian Berndt
 * @created 2017-12-25 13:53
 * @modified 2018-01-04 12:20
 * @version 1.0.1
 *
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.DATA_MANAGER,
        "mvc.command.name=exportMeasurements"
    },
    service = MVCResourceCommand.class
)
public class ExportMeasurementsMVCResourceCommand
        extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(ResourceRequest resourceRequest,
	        ResourceResponse resourceResponse) throws Exception {

		_log.info("doServeResource()");

		String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

		_log.info("cmd = " + cmd);

		// TODO: catch errors and redirect to error.jsp

		if ("getJSON".equals(cmd)) {

			getJSON(resourceRequest, resourceResponse);

		} else {

			// TODO: add i18n.
			if (Validator.isNull(cmd)) {
				PortletResponseUtil.write(resourceResponse,
				        "Resource cmd is missing.".getBytes());
			} else {
				PortletResponseUtil.write(resourceResponse,
				        "Resource cmd " + cmd + " is unknown.".getBytes());
			}
		}

	}

	protected void getJSON(ResourceRequest resourceRequest,
	        ResourceResponse resourceResponse) throws Exception {
		
		List<Measurement> measurements = getMeasurements(resourceRequest);
		Iterator<Measurement> iterator = measurements.iterator();

		StringBuilder sb = new StringBuilder();

		sb.append(StringPool.OPEN_BRACKET);
		sb.append(StringPool.NEW_LINE);

		while (iterator.hasNext()) {

			// TODO: output JSON according to configured json scheme
			Measurement measurement = iterator.next();

			String json = measurement.getData();

			sb.append(json);

			if (iterator.hasNext()) {
				sb.append(StringPool.COMMA);
			}

			sb.append(StringPool.NEW_LINE);

		}

		sb.append(StringPool.CLOSE_BRACKET);

		PortletResponseUtil.write(resourceResponse, sb.toString());

	}

	protected List<Measurement> getMeasurements(PortletRequest request)
	        throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) request
		        .getAttribute(WebKeys.THEME_DISPLAY);

		PortletURL iteratorURL = PortletURLFactoryUtil.create(request,
		        PortletKeys.DATA_MANAGER, themeDisplay.getLayout(),
		        PortletRequest.RENDER_PHASE);

		boolean advancedSearch = ParamUtil.getBoolean(request, "advancedSearch",
		        false);
		boolean andOperator = ParamUtil.getBoolean(request, "andOperator",
		        true);
		int end = ParamUtil.getInteger(request, "end", 10000);

		boolean ignoreFromDate = ParamUtil.getBoolean(request,
		        "ignoreFromDate");

		Date fromDate = null;

		if (!ignoreFromDate) {

			int fromDateDay = ParamUtil.getInteger(request, "fromDateDay");
			int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth");
			int fromDateYear = ParamUtil.getInteger(request, "fromDateYear");
			fromDate = PortalUtil.getDate(fromDateMonth, fromDateDay,
			        fromDateYear);
		}

		String keywords = ParamUtil.getString(request, "keywords");
		String orderByCol = ParamUtil.getString(request, "orderByCol",
		        "modifiedDate");
		String orderByType = ParamUtil.getString(request, "orderByType",
		        "desc");
		int start = ParamUtil.getInteger(request, "start");

		MeasurementSearch MeasurementSearch = new MeasurementSearch(request,
		        iteratorURL);

		orderByCol = MeasurementSearch.getOrderByCol();

		boolean ignoreUntilDate = ParamUtil.getBoolean(request,
		        "ignoreUntilDate");

		Date untilDate = null;

		if (!ignoreUntilDate) {

			int untilDateDay = ParamUtil.getInteger(request, "untilDateDay");
			int untilDateMonth = ParamUtil.getInteger(request,
			        "untilDateMonth");
			int untilDateYear = ParamUtil.getInteger(request, "untilDateYear");
			untilDate = PortalUtil.getDate(untilDateMonth, untilDateDay,
			        untilDateYear);
		}

		String data = ParamUtil.getString(request, DataManagerField.DATA);
		String id = ParamUtil.getString(request, DataManagerField.ID);
		String name = ParamUtil.getString(request, DataManagerField.NAME);
		String range = ParamUtil.getString(request, "range");

		// TODO: retrieve the timestamp
		Date timestamp = null;

		boolean reverse = "desc".equals(orderByType);

		Sort sort = new Sort(orderByCol, reverse);

		Hits hits = null;

		if (advancedSearch) {

			hits = MeasurementServiceUtil.search(themeDisplay.getUserId(),
			        themeDisplay.getScopeGroupId(), data, id, name, range, timestamp,
			        fromDate, untilDate, null, andOperator, start, end, sort);

		} else {

			hits = MeasurementServiceUtil.search(themeDisplay.getUserId(),
			        themeDisplay.getScopeGroupId(), keywords, start, end, sort);

		}

		List<Measurement> Measurements = MeasurementUtil.getMeasurements(hits);

		return Measurements;

	}

	private static Log _log = LogFactoryUtil
	        .getLog(ExportMeasurementsMVCResourceCommand.class.getName());

}
