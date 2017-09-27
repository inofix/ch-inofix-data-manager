package ch.inofix.data.service.util;

import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import ch.inofix.data.model.Measurement;
import ch.inofix.data.service.MeasurementLocalServiceUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-27 12:19
 * @modified 2017-09-27 12:19
 * @version 1.0.0
 *
 */
public class MeasurementUtil {

    public static List<Measurement> getMeasurements(Hits hits) {

        List<Document> documents = ListUtil.toList(hits.getDocs());

        List<Measurement> measurements = new ArrayList<Measurement>();

        for (Document document : documents) {
            try {
                long measurementId = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

                Measurement measurement = MeasurementLocalServiceUtil.getMeasurement(measurementId);
                measurements.add(measurement);

            } catch (Exception e) {

                if (_log.isErrorEnabled()) {
                    _log.error(e.getMessage());
                }
            }
        }

        return measurements;
    }

    private static final Log _log = LogFactoryUtil.getLog(MeasurementUtil.class.getName());

}
