package ch.inofix.data.web.internal.asset;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.PortalUtil;

import ch.inofix.data.constants.PortletKeys;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.permission.MeasurementPermission;
import ch.inofix.data.web.internal.constants.DataManagerWebKeys;

/**
 *
 * @author Christian Berndt
 * @created 2017-11-21 12:03
 * @modified 2017-11-21 12:03
 * @version 1.0.0
 *
 */
public class MeasurementAssetRenderer extends BaseJSPAssetRenderer<Measurement> implements TrashRenderer {

    public MeasurementAssetRenderer(Measurement measurement) {
        _measurement = measurement;
    }

    @Override
    public Measurement getAssetObject() {
        return _measurement;
    }

    @Override
    public String getClassName() {
        return Measurement.class.getName();
    }

    @Override
    public long getClassPK() {
        return _measurement.getMeasurementId();
    }

    @Override
    public long getGroupId() {
        return _measurement.getGroupId();
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        if (template.equals(TEMPLATE_ABSTRACT) || template.equals(TEMPLATE_FULL_CONTENT)) {
            return "/asset/" + template + ".jsp";
        } else {
            return null;
        }
    }

    @Override
    public String getPortletId() {
        AssetRendererFactory<Measurement> assetRendererFactory = getAssetRendererFactory();

        return assetRendererFactory.getPortletId();
    }

    @Override
    public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
        return null;
    }

    @Override
    public String getTitle(Locale locale) {
        return _measurement.getName();
    }

    @Override
    public String getType() {
        return MeasurementAssetRendererFactory.TYPE;
    }

    @Override
    public PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse) throws Exception {

        PortletURL portletURL = locateMeasurementManager(liferayPortletRequest);

        return portletURL;

    }

    @Override
    public String getURLView(LiferayPortletResponse liferayPortletResponse, WindowState windowState) {

        try {

            long portletPlid = PortalUtil.getPlidFromPortletId(_measurement.getGroupId(), false,
                    PortletKeys.DATA_MANAGER);

            PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(portletPlid, PortletKeys.DATA_MANAGER,
                    PortletRequest.RENDER_PHASE);

            portletURL.setParameter("mvcPath", "/edit_measurement.jsp");

            portletURL.setParameter("measurementId", String.valueOf(_measurement.getMeasurementId()));

            return portletURL.toString();

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getURLViewInContext(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) {

        try {

            PortletURL portletURL = locateMeasurementManager(liferayPortletRequest);

            return portletURL.toString();

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public long getUserId() {
        return _measurement.getUserId();
    }

    @Override
    public String getUserName() {
        return _measurement.getUserName();
    }

    @Override
    public String getUuid() {
        return _measurement.getUuid();
    }

    @Override
    public boolean hasViewPermission(PermissionChecker permissionChecker) {

        return MeasurementPermission.contains(permissionChecker, _measurement, ActionKeys.VIEW);
    }

    @Override
    public boolean include(HttpServletRequest request, HttpServletResponse response, String template) throws Exception {

        request.setAttribute(DataManagerWebKeys.MEASUREMENT, _measurement);

        return super.include(request, response, template);
    }

    private PortletURL locateMeasurementManager(LiferayPortletRequest liferayPortletRequest) throws PortalException {

        long portletPlid = PortalUtil.getPlidFromPortletId(_measurement.getGroupId(), false, PortletKeys.DATA_MANAGER);

        PortletURL portletURL = PortletURLFactoryUtil.create(liferayPortletRequest, PortletKeys.DATA_MANAGER,
                portletPlid, PortletRequest.RENDER_PHASE);

        portletURL.setParameter("mvcPath", "/edit_measurement.jsp");

        portletURL.setParameter("measurementId", String.valueOf(_measurement.getMeasurementId()));

        return portletURL;
    }

    private static final Log _log = LogFactoryUtil.getLog(MeasurementAssetRenderer.class);

    private final Measurement _measurement;

}
