package ch.inofix.data.web.internal.portlet.action;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.data.constants.MeasurementActionKeys;
import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.service.permission.DataManagerPortletPermission;

/**
 * @author Christian Berndt
 * @created 2017-11-13 21:01
 * @modified 2017-11-13 21:01
 * @version 1.0.0
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.DATA_MANAGER,
        "mvc.command.name=importMeasurements"
    },
    service = MVCRenderCommand.class
)
public class ImportMeasurementsMVCRenderCommand implements MVCRenderCommand {

    protected String getPath() {

        return "/import/new_import/import_measurements.jsp";
    }

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

        try {

            DataManagerPortletPermission.check(themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(),
                    MeasurementActionKeys.IMPORT_MEASUREMENTS);

        } catch (Exception e) {
            if (e instanceof PrincipalException) {

                SessionErrors.add(renderRequest, e.getClass());

                return "/error.jsp";

            } else {
                throw new PortletException(e);
            }
        }

        return getPath();

    }
}
