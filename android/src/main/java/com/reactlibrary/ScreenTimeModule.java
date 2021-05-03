// ScreenTimeModule.java

package com.reactlibrary;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;


public class ScreenTimeModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final Context context;
    //        constants.put("CATEGORY_UNDEFINED", ApplicationInfo.CATEGORY_UNDEFINED);
//        constants.put("CATEGORY_GAME", ApplicationInfo.CATEGORY_GAME);
//        constants.put("CATEGORY_AUDIO", ApplicationInfo.CATEGORY_AUDIO);
//        constants.put("CATEGORY_VIDEO", ApplicationInfo.CATEGORY_VIDEO);
//        constants.put("CATEGORY_IMAGE", ApplicationInfo.CATEGORY_IMAGE);
//        constants.put("CATEGORY_SOCIAL", ApplicationInfo.CATEGORY_SOCIAL);
//        constants.put("CATEGORY_NEWS", ApplicationInfo.CATEGORY_NEWS);
//        constants.put("CATEGORY_MAPS", ApplicationInfo.CATEGORY_MAPS);
//        constants.put("CATEGORY_PRODUCTIVITY", ApplicationInfo.CATEGORY_PRODUCTIVITY);
    private static HashMap<Integer, String> categoryMap = new HashMap<Integer, String>() {{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            put(ApplicationInfo.CATEGORY_UNDEFINED, "CATEGORY_UNDEFINED");
            put(ApplicationInfo.CATEGORY_AUDIO, "CATEGORY_AUDIO");
            put(ApplicationInfo.CATEGORY_VIDEO, "CATEGORY_VIDEO");
            put(ApplicationInfo.CATEGORY_IMAGE, "CATEGORY_IMAGE");
            put(ApplicationInfo.CATEGORY_SOCIAL, "CATEGORY_SOCIAL");
            put(ApplicationInfo.CATEGORY_NEWS, "CATEGORY_NEWS");
            put(ApplicationInfo.CATEGORY_MAPS, "CATEGORY_MAPS");
            put(ApplicationInfo.CATEGORY_PRODUCTIVITY, "CATEGORY_PRODUCTIVITY");
            put(ApplicationInfo.CATEGORY_GAME, "CATEGORY_GAME");
        }
        put(-2, "UNAVAILABLE");
    }};;
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
//    @ReactMethod
//    public void checkBatteryStatus(Promise promise){
//        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                status == BatteryManager.BATTERY_STATUS_FULL;
//
//    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private boolean checkForPermission(Context context) {
//        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
//        return mode == MODE_ALLOWED;
//    }

    @ReactMethod
    public void checkForPermission(String toastStr,Promise promise) {
        AppOpsManager appOps = (AppOpsManager) reactContext.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), reactContext.getPackageName());
        if(mode == MODE_ALLOWED){
            promise.resolve(true);
        }else{
            Toast.makeText(this.context, toastStr, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @ReactMethod
    public void queryUsageStats(int intervalType, double startTime, double endTime, Promise promise){
        WritableNativeMap result = new WritableNativeMap();
        UsageStatsManager usageStatsManager = (UsageStatsManager)reactContext.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(intervalType, (long) startTime, (long) endTime);
        for (UsageStats us : queryUsageStats) {
            Log.d("UsageStats", us.getPackageName() + " = " + us.getTotalTimeInForeground());
            WritableNativeMap usageStats = new WritableNativeMap();
            usageStats.putString("packageName", us.getPackageName());
            usageStats.putDouble("totalTimeInForeground", us.getTotalTimeInForeground());
            usageStats.putDouble("firstTimeStamp", us.getFirstTimeStamp());
            usageStats.putDouble("lastTimeStamp", us.getLastTimeStamp());
            usageStats.putDouble("lastTimeUsed", us.getLastTimeUsed());
            usageStats.putInt("describeContents", us.describeContents());

            usageStats.putString("packageCategory", getCategory(us.getPackageName()));
            result.putMap(us.getPackageName(), usageStats);
        }
        promise.resolve(result);
    }

    public String getCategory(String packageName){
        try{
            ApplicationInfo applicationInfo = getReactApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
            int category = -2;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                category = applicationInfo.category;
            }
            return categoryMap.get(category);
        }catch (Exception e) {
            e.printStackTrace();
            return categoryMap.get(-2); //UNAVAILABLE
        }
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
