package ch.inofix.data.service.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

/**
 *
 * @author Christian Berndt
 * @created 2017-11-13 22:09
 * @modified 2017-11-13 22:09
 * @version 1.0.0
 *
 */
public class JSONSchemaUtil {

    public static List<String> getFields(JSONObject jsonSchema) {

        List<String> fields = new ArrayList<String>();

        if (jsonSchema != null) {

            JSONObject itemsObj = jsonSchema.getJSONObject("items");

            if (itemsObj != null) {
                JSONObject propertiesObj = itemsObj.getJSONObject("properties");
                Iterator<String> iterator = propertiesObj.keys();
                while (iterator.hasNext()) {
                    fields.add(iterator.next());
                }
            }
        }

        return fields;

    }

    public static String[] getRequiredFields(JSONObject jsonSchema) {

        String[] requiredFields = null;

        JSONObject itemsObj = jsonSchema.getJSONObject("items");
        JSONArray requiredFieldsArray = null;

        if (itemsObj != null) {

            requiredFieldsArray = itemsObj.getJSONArray("required");

            if (requiredFieldsArray != null) {
                requiredFields = new String[requiredFieldsArray.length()];
                for (int i = 0; i < requiredFieldsArray.length(); i++) {
                    requiredFields[i] = requiredFieldsArray.getString(i);
                }
            }
        }

        return requiredFields;

    }
}
