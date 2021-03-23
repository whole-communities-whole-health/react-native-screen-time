// ScreenTimeModule.java

package com.reactlibrary;

import android.app.AppOpsManager;
import android.app.AppOpsManager.*;
import android.content.Intent;
import android.os.Process;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        getPermission();
//        callback.invoke("permission:" + checkForPermission(Objects.requireNonNull(this.getCurrentActivity()).getApplicationContext()));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    private void getPermission(){
        System.out.println("get permission.");
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(this.context, intent, null);
    }
}
