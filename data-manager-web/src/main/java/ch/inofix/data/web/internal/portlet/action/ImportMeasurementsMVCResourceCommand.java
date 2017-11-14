package ch.inofix.data.web.internal.portlet.action;

import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import ch.inofix.data.constants.PortletKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-02 19:19
 * @modified 2017-11-02 19:19
 * @version 1.0.0
 *
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.DATA_MANAGER,
        "mvc.command.name=importMeasurements"
    },
    service = MVCResourceCommand.class
)
public class ImportMeasurementsMVCResourceCommand extends BaseMVCResourceCommand {

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws Exception {
        
//        _log.info("doServeResource()");

        String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

//        _log.info("cmd = " + cmd);

        PortletRequestDispatcher portletRequestDispatcher = null;

        if (cmd.equals(Constants.IMPORT)) {
            portletRequestDispatcher = getPortletRequestDispatcher(resourceRequest, "/import/processes_list/view.jsp");
        } else {
            portletRequestDispatcher = getPortletRequestDispatcher(resourceRequest,
                    "/import/new_import/import_measurements_resources.jsp");
        }

        portletRequestDispatcher.include(resourceRequest, resourceResponse);
    }
    
    private static Log _log = LogFactoryUtil.getLog(ImportMeasurementsMVCResourceCommand.class.getName());

}
