package ch.inofix.data.background.task;

import java.io.File;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import ch.inofix.data.background.task.BaseExportImportBackgroundTaskExecutor;
import ch.inofix.data.background.task.MeasurementExportBackgroundTaskExecutor;
import ch.inofix.data.service.MeasurementLocalServiceUtil;

/**
 * @author Christian Berndt
 * @created 2017-09-01 21:57
 * @modified 2017-09-01 21:57
 * @version 1.0.0
 */
public class MeasurementExportBackgroundTaskExecutor extends BaseExportImportBackgroundTaskExecutor {

    public MeasurementExportBackgroundTaskExecutor() {
        // TODO
        // setBackgroundTaskStatusMessageTranslator(new
        // MeasurementExportImportBackgroundTaskStatusMessageTranslator());
    }

    @Override
    public BackgroundTaskExecutor clone() {
        MeasurementExportBackgroundTaskExecutor taskRecordExportBackgroundTaskExecutor = new MeasurementExportBackgroundTaskExecutor();

        taskRecordExportBackgroundTaskExecutor
                .setBackgroundTaskStatusMessageTranslator(getBackgroundTaskStatusMessageTranslator());
        taskRecordExportBackgroundTaskExecutor.setIsolationLevel(getIsolationLevel());

        return taskRecordExportBackgroundTaskExecutor;
    }

    @Override
    public BackgroundTaskResult execute(BackgroundTask backgroundTask) throws PortalException {

        ExportImportConfiguration exportImportConfiguration = getExportImportConfiguration(backgroundTask);

        long userId = backgroundTask.getUserId();

        StringBundler sb = new StringBundler(4);

        sb.append(StringUtil.replace(exportImportConfiguration.getName(), CharPool.SPACE, CharPool.UNDERLINE));
        sb.append(StringPool.DASH);
        sb.append(Time.getTimestamp());
        sb.append(".zip");

        File xmlFile = MeasurementLocalServiceUtil.exportMeasurementsAsFile(exportImportConfiguration);

        BackgroundTaskManagerUtil.addBackgroundTaskAttachment(userId, backgroundTask.getBackgroundTaskId(),
                sb.toString(), xmlFile);

        return BackgroundTaskResult.SUCCESS;
    }
}
