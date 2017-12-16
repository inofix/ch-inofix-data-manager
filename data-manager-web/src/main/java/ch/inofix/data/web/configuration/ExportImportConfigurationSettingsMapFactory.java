package ch.inofix.data.web.configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * 
 * @author Christian Berndt
 * @created 2017-12-16 00:11 
 * @modified 2017-12-16 00:11
 * @version 1.0.0
 *
 */
public class ExportImportConfigurationSettingsMapFactory {

    public static Map<String, Serializable> buildImportMeasurementsSettingsMap(long userId, long targetGroupId,
            Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone) {

        return buildSettingsMap(userId, 0, 0, targetGroupId, 0, StringPool.BLANK, parameterMap, locale, timeZone);
    }

    protected static Map<String, Serializable> buildSettingsMap(long userId, long sourceGroupId, long sourcePlid,
            long targetGroupId, long targetPlid, String portletId, Map<String, String[]> parameterMap, Locale locale,
            TimeZone timeZone) {

        Map<String, Serializable> settingsMap = new HashMap<>();

        if (locale != null) {
            settingsMap.put("locale", locale);
        }

        if (parameterMap != null) {

            HashMap<String, String[]> serializableParameterMap = new HashMap<>(parameterMap);

            settingsMap.put("parameterMap", serializableParameterMap);
        }

        if (Validator.isNotNull(portletId)) {
            settingsMap.put("portletId", portletId);
        }

        if (sourceGroupId > 0) {
            settingsMap.put("sourceGroupId", sourceGroupId);
        }

        if (sourcePlid > 0) {
            settingsMap.put("sourcePlid", sourcePlid);
        }

        if (targetGroupId > 0) {
            settingsMap.put("targetGroupId", targetGroupId);
        }

        if (targetPlid > 0) {
            settingsMap.put("targetPlid", targetPlid);
        }

        if (timeZone != null) {
            settingsMap.put("timezone", timeZone);
        }

        settingsMap.put("userId", userId);

        return settingsMap;
    }

}
