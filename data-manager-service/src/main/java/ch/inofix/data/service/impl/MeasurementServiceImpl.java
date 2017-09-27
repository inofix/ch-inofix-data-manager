/**
 * Copyright (c) 2000-present Inofix GmbH. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package ch.inofix.data.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;

import ch.inofix.data.service.base.MeasurementServiceBaseImpl;
import ch.inofix.data.constants.MeasurementActionKeys;
import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.permission.MeasurementPermission;
import ch.inofix.data.service.permission.DataManagerPortletPermission;

/**
 * The implementation of the measurement remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.data.service.MeasurementService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have
 * security checks based on the propagated JAAS credentials because this service
 * can be accessed remotely.
 * </p>
 *
 * @author Christian Berndt
 * @created 2017-09-27 00:32
 * @modified 2017-09-27 12:22
 * @version 1.0.2
 * @see MeasurementServiceBaseImpl
 * @see ch.inofix.data.service.MeasurementServiceUtil
 */
public class MeasurementServiceImpl extends MeasurementServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.data.service.MeasurementServiceUtil} to access the measurement
     * remote service.
     */
    @Override
    public Measurement addMeasurement(String data, ServiceContext serviceContext) throws PortalException {

        DataManagerPortletPermission.check(getPermissionChecker(), serviceContext.getScopeGroupId(),
                MeasurementActionKeys.ADD_MEASUREMENT);

        return measurementLocalService.addMeasurement(getUserId(), data, serviceContext);

    }

    @Override
    public Measurement createMeasurement() throws PortalException {

        // Create an empty measurement - no permission check required
        return measurementLocalService.createMeasurement(0);
    }
    
    @Override
    public Measurement getMeasurement(long measurementId) throws PortalException {

        MeasurementPermission.check(getPermissionChecker(), measurementId, MeasurementActionKeys.VIEW);
        return measurementLocalService.getMeasurement(measurementId);
    }
    
    @Override
    public Hits search(long userId, long groupId, String keywords, int start, int end, Sort sort)
            throws PortalException {

        return measurementLocalService.search(userId, groupId, keywords, start, end, sort);
    }

    @Override
    public Measurement deleteMeasurement(long measurementId) throws PortalException {

        MeasurementPermission.check(getPermissionChecker(), measurementId, MeasurementActionKeys.DELETE);

        return measurementLocalService.deleteMeasurement(measurementId);

    }

    @Override
    public Measurement updateMeasurement(long measurementId, String data, ServiceContext serviceContext)
            throws PortalException {

        MeasurementPermission.check(getPermissionChecker(), measurementId, MeasurementActionKeys.UPDATE);

        return measurementLocalService.updateMeasurement(measurementId, getUserId(), data, serviceContext);

    }
}
