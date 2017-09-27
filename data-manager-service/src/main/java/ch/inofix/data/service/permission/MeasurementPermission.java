package ch.inofix.data.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementLocalServiceUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-27 10:35
 * @modified 2017-09-27 10:35
 * @version 1.0.0
 *
 */
public class MeasurementPermission {

    public static void check(PermissionChecker permissionChecker, Measurement taskRecord, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, taskRecord, actionId)) {
            throw new PrincipalException();
        }
    }

    public static void check(PermissionChecker permissionChecker, long taskRecordId, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, taskRecordId, actionId)) {
            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, Measurement taskRecord, String actionId) {

        if (permissionChecker.hasOwnerPermission(taskRecord.getCompanyId(), Measurement.class.getName(),
                taskRecord.getMeasurementId(), taskRecord.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(taskRecord.getGroupId(), Measurement.class.getName(),
                String.valueOf(taskRecord.getMeasurementId()), actionId);
    }

    public static boolean contains(PermissionChecker permissionChecker, long taskRecordId, String actionId) {

        Measurement taskRecord;
        try {
            taskRecord = MeasurementLocalServiceUtil.getMeasurement(taskRecordId);
            return contains(permissionChecker, taskRecord, actionId);
        } catch (PortalException e) {
            _log.error(e);
        }

        return false;
    }

    private static final Log _log = LogFactoryUtil.getLog(MeasurementPermission.class.getName());

}
