package ch.inofix.data.web.internal.portlet.action;

import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.exception.NoSuchMeasurementException;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementService;
import ch.inofix.data.web.internal.constants.DataManagerWebKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-01 23:30
 * @modified 2017-11-01 23:30
 * @version 1.0.0
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

        try {
            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                updateMeasurement(actionRequest);
            } else if (cmd.equals(Constants.DELETE)) {
                deleteMeasurements(actionRequest);
            }
        } catch (NoSuchMeasurementException | PrincipalException e) {
            
            SessionErrors.add(actionRequest, e.getClass());

            actionResponse.setRenderParameter("mvcPath", "/error.jsp");

            // TODO: Define set of exceptions reported back to user. For an
            // example, see EditCategoryMVCActionCommand.java.

        } catch (Exception e) {

            SessionErrors.add(actionRequest, e.getClass());
        }
    }

    @Reference(unbind = "-")
    protected void setMeasurementService(MeasurementService measurementService) {
        this._measurementService = measurementService;
    }

    protected void updateMeasurement(ActionRequest actionRequest) throws Exception {

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

        }

        // TODO: validate data against configured JSON-schema

        Measurement measurement = null;

        if (measurementId <= 0) {

            // Add measurement

            measurement = _measurementService.addMeasurement(data, serviceContext);

        } else {

            // Update measurement

            measurement = _measurementService.updateMeasurement(measurementId, data, serviceContext);
        }

        actionRequest.setAttribute(DataManagerWebKeys.MEASUREMENT, measurement);
    }

    private MeasurementService _measurementService;

}
