package ch.inofix.data.web.internal.portlet;

import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementService;
import ch.inofix.data.web.internal.constants.DataManagerWebKeys;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * DataManagerPortlet: MVC-Controller of the data-manager.
 *
 * @author Christian Berndt
 * @created 2017-09-10 16:32
 * @modified 2017-09-27 14:20
 * @version 1.0.3
 */
@Component(
    immediate = true,
    property = {
        "com.liferay.portlet.css-class-wrapper=portlet-data-manager",
        "com.liferay.portlet.display-category=category.inofix",
        "com.liferay.portlet.instanceable=false",
        "javax.portlet.display-name=Data Manager",
        "javax.portlet.init-param.template-path=/",
        "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.name=" + PortletKeys.DATA_MANAGER,
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user"
    },
    service = Portlet.class
)
public class DataManagerPortlet extends MVCPortlet {

    @Override
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        try {
            if (cmd.equals(Constants.DELETE)) {

                deleteMeasurements(actionRequest, actionResponse);
                addSuccessMessage(actionRequest, actionResponse);
                
            } else if (cmd.equals(Constants.UPDATE)) {
                
                updateMeasurement(actionRequest, actionResponse);
                addSuccessMessage(actionRequest, actionResponse);

            }

        } catch (Exception e) {

            // TODO: report errors to user
            _log.error(e);

        }
    }
    
    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        try {
            getMeasurement(renderRequest);
        } catch (Exception e) {
            if (e instanceof NoSuchResourceException || e instanceof PrincipalException) {
                SessionErrors.add(renderRequest, e.getClass());
            } else {
                throw new PortletException(e);
            }
        }

        super.render(renderRequest, renderResponse);
    }

    protected String getEditMeasurementURL(ActionRequest actionRequest, ActionResponse actionResponse,
            Measurement measurement) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        String editMeasurementURL = getRedirect(actionRequest, actionResponse);

        if (Validator.isNull(editMeasurementURL)) {
            editMeasurementURL = PortalUtil.getLayoutFullURL(themeDisplay);
        }

        String namespace = actionResponse.getNamespace();
        String windowState = actionResponse.getWindowState().toString();

        editMeasurementURL = HttpUtil.setParameter(editMeasurementURL, "p_p_id", PortletKeys.DATA_MANAGER);
        editMeasurementURL = HttpUtil.setParameter(editMeasurementURL, "p_p_state", windowState);
        editMeasurementURL = HttpUtil.setParameter(editMeasurementURL, namespace + "mvcPath",
                templatePath + "edit_task_record.jsp");
        editMeasurementURL = HttpUtil.setParameter(editMeasurementURL, namespace + "redirect",
                getRedirect(actionRequest, actionResponse));
        editMeasurementURL = HttpUtil.setParameter(editMeasurementURL, namespace + "backURL",
                ParamUtil.getString(actionRequest, "backURL"));
        editMeasurementURL = HttpUtil.setParameter(editMeasurementURL, namespace + "measurementId",
                measurement.getMeasurementId());

        return editMeasurementURL;
    }

    protected void deleteMeasurements(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long measurementId = ParamUtil.getLong(actionRequest, "measurementId");

        long[] measurementIds = ParamUtil.getLongValues(actionRequest, "deleteMeasurementIds");

        if (measurementId > 0) {
            measurementIds = new long[] { measurementId };
        }

        for (long id : measurementIds) {
            _measurementService.deleteMeasurement(id);
        }

    }
    
    protected void getMeasurement(PortletRequest portletRequest) throws Exception {

        long measurementId = ParamUtil.getLong(portletRequest, "measurementId");

        if (measurementId <= 0) {
            return;
        }

        Measurement measurement = _measurementService.getMeasurement(measurementId);

        portletRequest.setAttribute(DataManagerWebKeys.MEASUREMENT, measurement);
    }

    @Reference(unbind = "-")
    protected void setMeasurementService(MeasurementService measurementService) {
        this._measurementService = measurementService;
    }

    protected void updateMeasurement(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long measurementId = ParamUtil.getLong(actionRequest, "measurementId");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Measurement.class.getName(), actionRequest);

        String data = ParamUtil.getString(actionRequest, "data");

        Measurement measurement = null;

        if (measurementId <= 0) {

            // Add measurement

            measurement = _measurementService.addMeasurement(data, serviceContext);

        } else {

            // Update measurement

            measurement = _measurementService.updateMeasurement(measurementId, data, serviceContext);
        }

        String redirect = getEditMeasurementURL(actionRequest, actionResponse, measurement);

        actionRequest.setAttribute(WebKeys.REDIRECT, redirect);

        actionRequest.setAttribute(DataManagerWebKeys.MEASUREMENT, measurement);
    }

    private MeasurementService _measurementService;

    private static final Log _log = LogFactoryUtil.getLog(DataManagerPortlet.class.getName());

}
    