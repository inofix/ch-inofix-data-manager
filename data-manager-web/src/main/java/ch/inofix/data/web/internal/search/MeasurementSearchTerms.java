package ch.inofix.data.web.internal.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

import ch.inofix.data.web.internal.search.MeasurementDisplayTerms;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-27 11:54
 * @modified 2017-09-27 11:54
 * @version 1.0.0
 *
 */
public class MeasurementSearchTerms extends MeasurementDisplayTerms {

    public MeasurementSearchTerms(PortletRequest portletRequest) {

        super(portletRequest);

        data = DAOParamUtil.getString(portletRequest, DATA);
        from = DAOParamUtil.getLong(portletRequest, FROM);
        groupId = DAOParamUtil.getLong(portletRequest, GROUP_ID);
        status = DAOParamUtil.getInteger(portletRequest, STATUS);
        until = DAOParamUtil.getLong(portletRequest, UNTIL);

    }
}
