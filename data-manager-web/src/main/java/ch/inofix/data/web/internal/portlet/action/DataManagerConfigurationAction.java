package ch.inofix.data.web.internal.portlet.action;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.web.configuration.DataManagerConfiguration;

/**
 * @author Christian Berndt
 * @created 2017-09-14 17:28
 * @modified 2017-10-25 22:44
 * @version 1.0.1
 */
@Component(
    configurationPid = "ch.inofix.data.web.configuration.DataManagerConfiguration", 
    configurationPolicy = ConfigurationPolicy.OPTIONAL, 
    immediate = true, 
    property = {"javax.portlet.name=" + PortletKeys.DATA_MANAGER},
    service = ConfigurationAction.class
)
public class DataManagerConfigurationAction extends DefaultConfigurationAction {
	
	@Override
    public String getJspPath(HttpServletRequest httpServletRequest) {
        return "/configuration.jsp";
    }
	@Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {

        String columns = ParamUtil.getString(actionRequest, "columns");
        
        setPreference(actionRequest, "columns", columns.split(","));
        
        super.processAction(portletConfig, actionRequest, actionResponse);
    }

    @Override
    public void include(PortletConfig portletConfig, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws Exception {

        httpServletRequest.setAttribute(DataManagerConfiguration.class.getName(), _dataManagerConfiguration);

        super.include(portletConfig, httpServletRequest, httpServletResponse);
    }

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {

        _dataManagerConfiguration = ConfigurableUtil.createConfigurable(
                DataManagerConfiguration.class, properties);
        
    }

    private volatile DataManagerConfiguration _dataManagerConfiguration;

}
