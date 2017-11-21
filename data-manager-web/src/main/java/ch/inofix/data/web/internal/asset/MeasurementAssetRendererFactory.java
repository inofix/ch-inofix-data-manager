package ch.inofix.data.web.internal.asset;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.constants.MeasurementActionKeys;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementLocalService;
import ch.inofix.data.service.permission.MeasurementPermission;
import ch.inofix.data.service.permission.DataManagerPortletPermission;

/**
 *
 * @author Christian Berndt
 * @created 2017-11-21 12:10
 * @modified 2017-11-21 12:10
 * @version 1.0.0
 *
 */
@Component(
    immediate = true, 
    property = {"javax.portlet.name=" + PortletKeys.DATA_MANAGER }, 
    service = AssetRendererFactory.class
)
public class MeasurementAssetRendererFactory extends BaseAssetRendererFactory<Measurement> {

    public static final String TYPE = "measurement";

    public MeasurementAssetRendererFactory() {

        setCategorizable(true);
        setClassName(Measurement.class.getName());
        setLinkable(false);
        setPortletId(PortletKeys.DATA_MANAGER);
        setSearchable(true);
        setSelectable(true);

    }

    @Override
    public AssetRenderer<Measurement> getAssetRenderer(long classPK, int type) throws PortalException {

        Measurement measurement = _measurementLocalService.getMeasurement(classPK);

        MeasurementAssetRenderer measurementAssetRenderer = new MeasurementAssetRenderer(measurement);

        measurementAssetRenderer.setAssetRendererType(type);
        measurementAssetRenderer.setServletContext(_servletContext);

        return measurementAssetRenderer;

    }

    @Override
    public String getClassName() {
        return Measurement.class.getName();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse) throws PortalException {

        ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY);

        User user = themeDisplay.getUser();

        Group group = user.getGroup();

        if (group != null) {

            long portletPlid = PortalUtil.getPlidFromPortletId(group.getGroupId(), false, PortletKeys.DATA_MANAGER);

            PortletURL portletURL = PortletURLFactoryUtil.create(liferayPortletRequest, PortletKeys.DATA_MANAGER,
                    portletPlid, PortletRequest.RENDER_PHASE);

            portletURL.setParameter("mvcPath", "/edit_measurement.jsp");

            String redirect = (String) liferayPortletRequest.getAttribute("redirect");

            if (Validator.isNotNull(redirect)) {
                portletURL.setParameter("redirect", redirect);
            }

            return portletURL;

        } else {

            return null;

        }
    }

    @Override
    public boolean hasAddPermission(PermissionChecker permissionChecker, long groupId, long classTypeId)
            throws Exception {

        return DataManagerPortletPermission.contains(permissionChecker, groupId, MeasurementActionKeys.ADD_MEASUREMENT);
    }

    @Override
    public boolean hasPermission(PermissionChecker permissionChecker, long classPK, String actionId) throws Exception {

        Measurement measurement = _measurementLocalService.getMeasurement(classPK);

        return MeasurementPermission.contains(permissionChecker, measurement.getMeasurementId(), actionId);
    }

    @Reference(target = "(osgi.web.symbolicname=ch.inofix.data.web)", unbind = "-")
    public void setServletContext(ServletContext servletContext) {
        _servletContext = servletContext;
    }

    @Reference(unbind = "-")
    protected void setMeasurementLocalService(MeasurementLocalService measurementLocalService) {
        _measurementLocalService = measurementLocalService;
    }

    private MeasurementLocalService _measurementLocalService;
    private ServletContext _servletContext;

}
