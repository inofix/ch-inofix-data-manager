package ch.inofix.data.web.internal.portlet.action;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;

import ch.inofix.data.exception.NoSuchMeasurementException;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.web.internal.constants.DataManagerWebKeys;
import ch.inofix.data.web.internal.portlet.action.ActionUtil;
import ch.inofix.data.web.internal.portlet.action.GetMeasurementMVCRenderCommand;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-13 16:37
 * @modified 2017-11-13 16:37
 * @version 1.0.0
 *
 */
public abstract class GetMeasurementMVCRenderCommand implements MVCRenderCommand {

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        _log.info("render()");

        try {
            Measurement measurement = ActionUtil.getMeasurement(renderRequest);

            renderRequest.setAttribute(DataManagerWebKeys.MEASUREMENT, measurement);
        } catch (Exception e) {
            if (e instanceof NoSuchMeasurementException || e instanceof PrincipalException) {

                SessionErrors.add(renderRequest, e.getClass());

                return "/error.jsp";

            } else {
                throw new PortletException(e);
            }
        }

        return getPath();
    }

    protected abstract String getPath();

    private static Log _log = LogFactoryUtil.getLog(GetMeasurementMVCRenderCommand.class.getName());

}
