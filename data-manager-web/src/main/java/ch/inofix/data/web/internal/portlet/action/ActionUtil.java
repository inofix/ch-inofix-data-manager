package ch.inofix.data.web.internal.portlet.action;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementServiceUtil;
import ch.inofix.data.web.internal.portlet.action.ActionUtil;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-13 16:34
 * @modified 2017-11-13 16:34
 * @version 1.0.0
 *
 */
public class ActionUtil {

    public static Measurement getMeasurement(HttpServletRequest request) throws Exception {

        long measurementId = ParamUtil.getLong(request, "measurementId");

        Measurement measurement = null;

        if (measurementId > 0) {
            measurement = MeasurementServiceUtil.getMeasurement(measurementId);
        }

        return measurement;
    }

    public static Measurement getMeasurement(PortletRequest portletRequest) throws Exception {

        HttpServletRequest request = PortalUtil.getHttpServletRequest(portletRequest);

        return getMeasurement(request);
    }

}
