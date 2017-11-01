package ch.inofix.data.web.internal.portlet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementService;
import ch.inofix.data.web.configuration.DataManagerConfiguration;
import ch.inofix.data.web.internal.constants.DataManagerWebKeys;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * DataManagerPortlet: MVC-Controller of the data-manager.
 *
 * @author Christian Berndt
 * @created 2017-09-10 16:32
 * @modified 2017-10-31 13:37
 * @version 1.0.7
 */
@Component(
    configurationPid = "ch.inofix.data.web.configuration.DataManagerConfiguration",
    immediate = true, 
    property = { 
        "com.liferay.portlet.css-class-wrapper=portlet-data-manager",
        "com.liferay.portlet.display-category=category.inofix", 
        "com.liferay.portlet.header-portlet-css=/css/main.css",
        "com.liferay.portlet.instanceable=false", 
        "com.liferay.portlet.preferences-owned-by-group=true",
        "com.liferay.portlet.preferences-unique-per-layout=false",
        "com.liferay.portlet.scopeable=true",
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
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        renderRequest.setAttribute(DataManagerConfiguration.class.getName(), _dataManagerConfiguration);

        super.doView(renderRequest, renderResponse);
    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {
        
        _log.info("render()");

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

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {

        _dataManagerConfiguration = ConfigurableUtil.createConfigurable(
                DataManagerConfiguration.class, properties);
        
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

    // TODO: move validate method to EditMeasurementMVCActionCommand
    
    protected void validateMeasurement(String measurement) {

        try (InputStream inputStream = getClass().getResourceAsStream("/path/to/your/schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(new JSONObject("{\"hello\" : \"world\"}"));
            // throws a ValidationException if this object is invalid
        } catch (Exception e) {
            _log.error(e);
        }
    }

    private MeasurementService _measurementService;

    private volatile DataManagerConfiguration _dataManagerConfiguration;

    private static final Log _log = LogFactoryUtil.getLog(DataManagerPortlet.class.getName());

}
    