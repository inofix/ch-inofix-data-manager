/**
 * Copyright (c) 2000-present Inofix GmbH, Luzern. All rights reserved.
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

package ch.inofix.data.model.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import aQute.bnd.annotation.ProviderType;

/**
 * The extended model implementation for the Measurement service. Represents a row in the &quot;inofix_dm_Measurement&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link ch.inofix.data.model.Measurement} interface.
 * </p>
 *
 * @author Christian Berndt
 * @created 2017-08-24 12:19
 * @modified 2017-11-13 17:32
 * @version 1.0.1
 */
@ProviderType
public class MeasurementImpl extends MeasurementBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. All methods that expect a
     * measurement model instance should use the {@link
     * ch.inofix.data.model.Measurement} interface instead.
     */
    public MeasurementImpl() {
    }

    public Map<String, String> getDataMap() {

        JSONObject jsonObject = null;
        Map<String, String> map = new HashMap<String, String>();

        try {
            jsonObject = JSONFactoryUtil.createJSONObject(GetterUtil.getString(getData()));

            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {

                String key = keys.next();

                map.put(key, jsonObject.getString(key));

            }

        } catch (JSONException e) {
            _log.error(e);
        }

        return map;

    }

    private static Log _log = LogFactoryUtil.getLog(MeasurementImpl.class.getName());

}
