package ch.inofix.data.web.internal.portlet.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.exception.MeasurementIdException;
import ch.inofix.data.exception.MeasurementNameException;
import ch.inofix.data.exception.MeasurementTimestampException;
import ch.inofix.data.exception.NoSuchMeasurementException;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementService;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-01 23:30
 * @modified 2017-11-21 21:39
 * @version 1.0.3
 *
 */
@Component(
    property = {
        "javax.portlet.name=" + PortletKeys.DATA_MANAGER,
        "mvc.command.name=editMeasurement"
    },
    service = MVCActionCommand.class
)
public class EditMeasurementMVCActionCommand extends BaseMVCActionCommand {
    
    protected void deleteGroupMeasurements(ActionRequest actionRequest) throws Exception {

        _log.info("deleteGroupMeasurements()");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Measurement.class.getName(), actionRequest);

        _measurementService.deleteGroupMeasurements(serviceContext.getScopeGroupId());

    }

    protected void deleteMeasurements(ActionRequest actionRequest) throws Exception {

        long measurementId = ParamUtil.getLong(actionRequest, "measurementId");

        long[] measurementIds = ParamUtil.getLongValues(actionRequest, "deleteMeasurementIds");

        if (measurementId > 0) {
            measurementIds = new long[] { measurementId };
        }

        for (long id : measurementIds) {
            _measurementService.deleteMeasurement(id);
        }

    }

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        String redirect = ParamUtil.getString(actionRequest, "redirect");

        Measurement measurement = null;

        try {
            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                measurement = updateMeasurement(actionRequest);
            } else if (cmd.equals(Constants.DELETE)) {
                deleteMeasurements(actionRequest);
            } else if (cmd.equals("deleteGroupMeasurements")) {
                deleteGroupMeasurements(actionRequest);
            }

            if (Validator.isNotNull(cmd)) {
                if (measurement != null) {

                    redirect = getSaveAndContinueRedirect(actionRequest, measurement, themeDisplay.getLayout(),
                            redirect);

                    sendRedirect(actionRequest, actionResponse, redirect);
                }
            }
        } catch (MeasurementIdException | MeasurementNameException | MeasurementTimestampException e) {

            SessionErrors.add(actionRequest, e.getClass());

        } catch (NoSuchMeasurementException | PrincipalException e) {

            SessionErrors.add(actionRequest, e.getClass());

            actionResponse.setRenderParameter("mvcPath", "/error.jsp");

        } catch (Exception e) {

            SessionErrors.add(actionRequest, e.getClass());

            actionResponse.setRenderParameter("mvcPath", "/error.jsp");
        }
    }
    
    protected String getSaveAndContinueRedirect(
            ActionRequest actionRequest, Measurement measurement, Layout layout, String redirect)
        throws Exception {

        PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
            JavaConstants.JAVAX_PORTLET_CONFIG);
        
        LiferayPortletURL portletURL = PortletURLFactoryUtil.create(actionRequest, portletConfig.getPortletName(), layout, PortletRequest.RENDER_PHASE);

        portletURL.setParameter("mvcRenderCommandName", "editMeasurement");

        portletURL.setParameter(Constants.CMD, Constants.UPDATE, false);
        portletURL.setParameter("redirect", redirect, false);
        portletURL.setParameter(
            "groupId", String.valueOf(measurement.getGroupId()), false);
        portletURL.setParameter(
            "measurementId", String.valueOf(measurement.getMeasurementId()), false);
        portletURL.setWindowState(actionRequest.getWindowState());

        return portletURL.toString();
    }

    @Reference(unbind = "-")
    protected void setMeasurementService(MeasurementService measurementService) {
        this._measurementService = measurementService;
    }

    protected Measurement updateMeasurement(ActionRequest actionRequest) throws Exception {

        long measurementId = ParamUtil.getLong(actionRequest, "measurementId");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Measurement.class.getName(), actionRequest);

        PortletPreferences portletPreferences = actionRequest.getPreferences();

        String jsonSchema = portletPreferences.getValue("jsonSchema", "{}");

        com.liferay.portal.kernel.json.JSONObject jsonSchemaObj = JSONFactoryUtil.createJSONObject(jsonSchema);

        Iterator<String> keys = null;

        if (jsonSchemaObj != null) {
            com.liferay.portal.kernel.json.JSONObject itemsObj = jsonSchemaObj.getJSONObject("items");
            if (itemsObj != null) {
                com.liferay.portal.kernel.json.JSONObject propertiesObj = itemsObj.getJSONObject("properties");
                keys = propertiesObj.keys();
            }
        }

        String data = null;
        String id = null;
        String name = null;
        Date timestamp = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");

        if (keys != null) {

            com.liferay.portal.kernel.json.JSONObject dataObj = JSONFactoryUtil.createJSONObject();

            while (keys.hasNext()) {

                String key = keys.next();

                String value = actionRequest.getParameter(key);

                dataObj.put(key, value);

            }

            data = dataObj.toJSONString();

        } else {

            data = ParamUtil.getString(actionRequest, "data");
            id = ParamUtil.getString(actionRequest, "id");
            name = ParamUtil.getString(actionRequest, "name");
            timestamp = ParamUtil.getDate(actionRequest, "timestamp", dateFormat);

        }

        // TODO: validate data against configured JSON-schema

        Measurement measurement = null;

        if (measurementId <= 0) {

            // Add measurement

            measurement = _measurementService.addMeasurement(data, id, name, timestamp, serviceContext);

        } else {

            // Update measurement

            measurement = _measurementService.updateMeasurement(measurementId, data, id, name, timestamp,
                    serviceContext);
        }

        return measurement;

    }

    private MeasurementService _measurementService;
    
    private static Log _log = LogFactoryUtil.getLog(EditMeasurementMVCActionCommand.class.getName()); 

}
