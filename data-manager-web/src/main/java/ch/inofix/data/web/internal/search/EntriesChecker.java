package ch.inofix.data.web.internal.search;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementLocalServiceUtil;
import ch.inofix.data.service.permission.MeasurementPermission;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-27 11:46
 * @modified 2017-09-27 11:46
 * @version 1.0.0
 *
 */
public class EntriesChecker extends EmptyOnClickRowChecker {

    public EntriesChecker(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse) {

        super(liferayPortletResponse);

        _liferayPortletResponse = liferayPortletResponse;

        ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY);

        _permissionChecker = themeDisplay.getPermissionChecker();
    }

    @Override
    public String getAllRowsCheckBox() {
        return null;
    }

    @Override
    public String getAllRowsCheckBox(HttpServletRequest request) {
        return null;
    }

    @Override
    public String getRowCheckBox(HttpServletRequest request, boolean checked, boolean disabled, String primaryKey) {

        long entryId = GetterUtil.getLong(primaryKey);

        Measurement entry = MeasurementLocalServiceUtil.fetchMeasurement(entryId);

        boolean showInput = false;

        String name = null;

        if (entry != null) {
            name = Measurement.class.getSimpleName();

            try {
                if (MeasurementPermission.contains(_permissionChecker, entry, ActionKeys.DELETE)) {

                    showInput = true;
                }
            } catch (Exception e) {
            }
        }

        if (!showInput) {
            return StringPool.BLANK;
        }

        String checkBoxRowIds = getEntryRowIds();
        String checkBoxAllRowIds = "'#" + getAllRowIds() + "'";

        return getRowCheckBox(request, checked, disabled,
                _liferayPortletResponse.getNamespace() + RowChecker.ROW_IDS + name, primaryKey, checkBoxRowIds,
                checkBoxAllRowIds, StringPool.BLANK);
    }

    protected String getEntryRowIds() {
        StringBundler sb = new StringBundler(13);

        sb.append("['");
        sb.append(_liferayPortletResponse.getNamespace());
        sb.append(RowChecker.ROW_IDS);
        sb.append(Measurement.class.getSimpleName());
        sb.append("']");

        return sb.toString();
    }

    private final LiferayPortletResponse _liferayPortletResponse;
    private final PermissionChecker _permissionChecker;

}
