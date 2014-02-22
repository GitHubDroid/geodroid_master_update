package eu.hydrologis.geopaparazzi;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.util.Log;
import eu.geopaparazzi.library.GeopaparazziLibraryContextHolder;
import eu.geopaparazzi.library.database.GPLog;
import eu.geopaparazzi.spatialite.database.spatial.SpatialiteContextHolder;

@ReportsCrashes(//
formKey = "", //
mailTo = "promptnetworks@gmail.com", //
customReportContent = {//
/*    */ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, //
        ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, //
        ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT}, //
mode = ReportingInteractionMode.TOAST, //
resToastText = R.string.crash_toast_text, //
logcatArguments = {"-t", "400", "-v", "time", "GPLOG:I", "*:S"})
public class GeodroidApplication extends Application {

    private static GeodroidApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        GeopaparazziLibraryContextHolder.INSTANCE.setContext(instance);
        SpatialiteContextHolder.INSTANCE.setContext(instance);

        ACRA.init(instance);
        Log.i("TRACKOIDAPPLICATION", "ACRA Initialized.");

        if (GPLog.LOG_ANDROID) {
            Log.i(getClass().getSimpleName(), "Geodroid Application singleton created.");
        }
    }

    /**
     * @return the singleton instance.
     */
    public static GeodroidApplication getInstance() {
        return instance;
    }
}
