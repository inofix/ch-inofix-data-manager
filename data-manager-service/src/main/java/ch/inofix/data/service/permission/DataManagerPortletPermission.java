package ch.inofix.data.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.BaseResourcePermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import ch.inofix.data.constants.PortletKeys;

/**
*
* @author Christian Berndt
* @created 2017-09-18 00:08
* @modified 2017-09-18 00:08
* @version 1.0.0
*
*/
public class DataManagerPortletPermission extends BaseResourcePermissionChecker {

    public static final String RESOURCE_NAME = "ch.inofix.data";

    public static void check(PermissionChecker permissionChecker, long groupId, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, groupId, actionId)) {

            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, long groupId, String actionId) {

        return contains(permissionChecker, RESOURCE_NAME, PortletKeys.DATA_MANAGER, groupId, actionId);
    }

    @Override
    public Boolean checkResource(PermissionChecker permissionChecker, long classPK, String actionId) {
        return contains(permissionChecker, classPK, actionId);
    }
}
