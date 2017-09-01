package ch.inofix.data.background.task;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.FileUtil;

import ch.inofix.data.background.task.BaseExportImportBackgroundTaskExecutor;
import ch.inofix.data.background.task.MeasurementImportBackgroundTaskExecutor;
import ch.inofix.data.service.MeasurementLocalServiceUtil;

/**
 * @author Christian Berndt
 * @created 2017-09-01 22:00
 * @modified 2017-09-01 22:00
 * @version 1.0.0
 */
public class MeasurementImportBackgroundTaskExecutor extends BaseExportImportBackgroundTaskExecutor {

    public MeasurementImportBackgroundTaskExecutor() {

        // TODO
        // setBackgroundTaskStatusMessageTranslator(new
        // MeasurementExportImportBackgroundTaskStatusMessageTranslator());

        // Isolation level guarantees this will be serial in a group

        setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_GROUP);
    }

    @Override
    public BackgroundTaskExecutor clone() {
        MeasurementImportBackgroundTaskExecutor taskRecordImportBackgroundTaskExecutor = new MeasurementImportBackgroundTaskExecutor();

        taskRecordImportBackgroundTaskExecutor
                .setBackgroundTaskStatusMessageTranslator(getBackgroundTaskStatusMessageTranslator());
        taskRecordImportBackgroundTaskExecutor.setIsolationLevel(getIsolationLevel());

        return taskRecordImportBackgroundTaskExecutor;
    }

    @Override
    public BackgroundTaskResult execute(BackgroundTask backgroundTask) throws Exception {

        ExportImportConfiguration exportImportConfiguration = getExportImportConfiguration(backgroundTask);

        List<FileEntry> attachmentsFileEntries = backgroundTask.getAttachmentsFileEntries();

        File file = null;

        for (FileEntry attachmentsFileEntry : attachmentsFileEntries) {
            try {
                file = FileUtil.createTempFile("lar");

                FileUtil.write(file, attachmentsFileEntry.getContentStream());

                _log.info(file.getAbsoluteFile());

                TransactionInvokerUtil.invoke(transactionConfig,
                        new MeasurementImportCallable(exportImportConfiguration, file));

            } catch (Throwable t) {
                if (_log.isDebugEnabled()) {
                    _log.debug(t, t);
                } else if (_log.isWarnEnabled()) {
                    _log.warn("Unable to import taskRecords: " + t.getMessage());
                }

                throw new SystemException(t);
            } finally {
                FileUtil.delete(file);
            }
        }

        return BackgroundTaskResult.SUCCESS;
    }

    private static final Log _log = LogFactoryUtil.getLog(MeasurementImportBackgroundTaskExecutor.class);

    private static class MeasurementImportCallable implements Callable<Void> {

        public MeasurementImportCallable(ExportImportConfiguration exportImportConfiguration, File file) {

            _exportImportConfiguration = exportImportConfiguration;
            _file = file;
        }

        @Override
        public Void call() throws PortalException {

            MeasurementLocalServiceUtil.importMeasurements(_exportImportConfiguration, _file);

            return null;
        }

        private final ExportImportConfiguration _exportImportConfiguration;
        private final File _file;

    }

}

