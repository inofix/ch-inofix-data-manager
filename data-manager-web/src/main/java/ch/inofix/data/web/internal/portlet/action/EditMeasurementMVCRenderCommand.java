package ch.inofix.data.web.internal.portlet.action;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.web.internal.portlet.action.GetMeasurementMVCRenderCommand;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-13 16:43
 * @modified 2017-11-13 16:43
 * @version 1.0.0
 *
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.DATA_MANAGER,
        "mvc.command.name=editMeasurement"
    },
    service = MVCRenderCommand.class
)
public class EditMeasurementMVCRenderCommand extends GetMeasurementMVCRenderCommand {

    @Override
    protected String getPath() {

        return "/edit_measurement.jsp";
    }
}
