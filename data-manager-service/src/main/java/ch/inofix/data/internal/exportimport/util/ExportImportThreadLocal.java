package ch.inofix.data.internal.exportimport.util;

import com.liferay.portal.kernel.util.AutoResetThreadLocal;

/**
 *
 * @author Christian Berndt
 * @created 2017-11-02 22:56
 * @modified 2017-11-02 22:56
 * @version 1.0.0
 *
 *          Based on the model of
 *          com.liferay.exportimport.kernel.lar.ExportImportThreadLocal
 *
 */
public class ExportImportThreadLocal {

    public static boolean isDataDeletionImportInProcess() {
        if (isMeasurementDataDeletionImportInProcess() || isPortletDataDeletionImportInProcess()) {

            return true;
        }

        return false;
    }

    public static boolean isExportInProcess() {
        if (isMeasurementExportInProcess() || isPortletExportInProcess()) {
            return true;
        }

        return false;
    }

    public static boolean isImportInProcess() {
        if (isDataDeletionImportInProcess() || isMeasurementImportInProcess() || isMeasurementValidationInProcess()
                || isPortletImportInProcess() || isPortletValidationInProcess()) {

            return true;
        }

        return false;
    }

    public static boolean isInitialMeasurementStagingInProcess() {
        return _initialMeasurementStagingInProcess.get();
    }

    public static boolean isMeasurementDataDeletionImportInProcess() {
        return _taskRecordDataDeletionImportInProcess.get();
    }

    public static boolean isMeasurementExportInProcess() {
        return _taskRecordExportInProcess.get();
    }

    public static boolean isMeasurementImportInProcess() {
        return _taskRecordImportInProcess.get();
    }

    public static boolean isMeasurementStagingInProcess() {
        return _taskRecordStagingInProcess.get();
    }

    public static boolean isMeasurementValidationInProcess() {
        return _taskRecordValidationInProcess.get();
    }

    public static boolean isPortletDataDeletionImportInProcess() {
        return _portletDataDeletionImportInProcess.get();
    }

    public static boolean isPortletExportInProcess() {
        return _portletExportInProcess.get();
    }

    public static boolean isPortletImportInProcess() {
        return _portletImportInProcess.get();
    }

    public static boolean isPortletStagingInProcess() {
        return _portletStagingInProcess.get();
    }

    public static boolean isPortletValidationInProcess() {
        return _portletValidationInProcess.get();
    }

    public static boolean isStagingInProcess() {
        if (isMeasurementStagingInProcess() || isPortletStagingInProcess()) {
            return true;
        }

        return false;
    }

    public static void setInitialMeasurementStagingInProcess(boolean initialMeasurementStagingInProcess) {

        _initialMeasurementStagingInProcess.set(initialMeasurementStagingInProcess);
    }

    public static void setMeasurementDataDeletionImportInProcess(boolean taskRecordDataDeletionImportInProcess) {

        _taskRecordDataDeletionImportInProcess.set(taskRecordDataDeletionImportInProcess);
    }

    public static void setMeasurementExportInProcess(boolean taskRecordExportInProcess) {
        _taskRecordExportInProcess.set(taskRecordExportInProcess);
    }

    public static void setMeasurementImportInProcess(boolean taskRecordImportInProcess) {
        _taskRecordImportInProcess.set(taskRecordImportInProcess);
    }

    public static void setMeasurementStagingInProcess(boolean taskRecordStagingInProcess) {

        _taskRecordStagingInProcess.set(taskRecordStagingInProcess);
    }

    public static void setMeasurementValidationInProcess(boolean taskRecordValidationInProcess) {

        _taskRecordValidationInProcess.set(taskRecordValidationInProcess);
    }

    public static void setPortletDataDeletionImportInProcess(boolean portletDataDeletionImportInProcess) {

        _portletDataDeletionImportInProcess.set(portletDataDeletionImportInProcess);
    }

    public static void setPortletExportInProcess(boolean portletExportInProcess) {

        _portletExportInProcess.set(portletExportInProcess);
    }

    public static void setPortletImportInProcess(boolean portletImportInProcess) {

        _portletImportInProcess.set(portletImportInProcess);
    }

    public static void setPortletStagingInProcess(boolean portletStagingInProcess) {

        _portletStagingInProcess.set(portletStagingInProcess);
    }

    public static void setPortletValidationInProcess(boolean portletValidationInProcess) {

        _portletValidationInProcess.set(portletValidationInProcess);
    }

    private static final ThreadLocal<Boolean> _initialMeasurementStagingInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._initialMeasurementStagingInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordDataDeletionImportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordDataDeletionImportInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordExportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordExportInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordImportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordImportInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordStagingInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordStagingInProcess", false);
    private static final ThreadLocal<Boolean> _taskRecordValidationInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._taskRecordValidationInProcess", false);
    private static final ThreadLocal<Boolean> _portletDataDeletionImportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletDataDeletionImportInProcess", false);
    private static final ThreadLocal<Boolean> _portletExportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletExportInProcess", false);
    private static final ThreadLocal<Boolean> _portletImportInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletImportInProcess", false);
    private static final ThreadLocal<Boolean> _portletStagingInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletStagingInProcess", false);
    private static final ThreadLocal<Boolean> _portletValidationInProcess = new AutoResetThreadLocal<>(
            ExportImportThreadLocal.class + "._portletValidationInProcess", false);

}
