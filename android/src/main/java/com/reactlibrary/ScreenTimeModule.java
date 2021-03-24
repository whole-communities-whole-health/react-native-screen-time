// ScreenTimeModule.java

package com.reactlibrary;

import android.app.AppOpsManager;
import android.app.AppOpsManager.*;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Process;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static androidx.core.content.ContextCompat.startActivity;


public class ScreenTimeModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final Context context;
    public ScreenTimeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.context = getReactApplicationContext();
    }


    @Override
    public String getName() {
        return "ScreenTime";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
//        getPermission();
//        callback.invoke("permission:" + checkForPermission(Objects.requireNonNull(this.getCurrentActivity()).getApplicationContext()));
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private boolean checkForPermission(Context context) {
//        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
//        return mode == MODE_ALLOWED;
//    }

    @ReactMethod
    public void checkForPermission(Promise promise) {
        AppOpsManager appOps = (AppOpsManager) reactContext.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), reactContext.getPackageName());
        promise.resolve(mode == MODE_ALLOWED);
    }

    @ReactMethod
    public void queryUsageStats(int intervalType, double startTime, double endTime, Promise promise) {
        WritableMap result = new WritableNativeMap();
        UsageStatsManager usageStatsManager = (UsageStatsManager)reactContext.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(intervalType, (long) startTime, (long) endTime);
        for (UsageStats us : queryUsageStats) {
            Log.d("UsageStats", us.getPackageName() + " = " + us.getTotalTimeInForeground());
            WritableMap usageStats = new WritableNativeMap();
            usageStats.putString("packageName", us.getPackageName());
            usageStats.putDouble("totalTimeInForeground", us.getTotalTimeInForeground());
            usageStats.putDouble("firstTimeStamp", us.getFirstTimeStamp());
            usageStats.putDouble("lastTimeStamp", us.getLastTimeStamp());
            usageStats.putDouble("lastTimeUsed", us.getLastTimeUsed());
            usageStats.putInt("describeContents", us.describeContents());
            result.putMap(us.getPackageName(), usageStats);
        }
        promise.resolve(result);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = MapBuilder.newHashMap();
        constants.put("INTERVAL_WEEKLY", UsageStatsManager.INTERVAL_WEEKLY);
        constants.put("INTERVAL_MONTHLY", UsageStatsManager.INTERVAL_MONTHLY);
        constants.put("INTERVAL_YEARLY", UsageStatsManager.INTERVAL_YEARLY);
        constants.put("INTERVAL_DAILY", UsageStatsManager.INTERVAL_DAILY);
        constants.put("INTERVAL_BEST", UsageStatsManager.INTERVAL_BEST);


        constants.put("TYPE_WIFI", ConnectivityManager.TYPE_WIFI);
        constants.put("TYPE_MOBILE", ConnectivityManager.TYPE_MOBILE);
        constants.put("TYPE_MOBILE_AND_WIFI", Integer.MAX_VALUE);
        return constants;
    }
}
