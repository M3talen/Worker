package com.metalen.worker;

import android.Manifest;
import android.content.Context;
import android.support.multidex.MultiDex;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.percolate.foam.FoamApiKeys;
import com.percolate.foam.FoamApplication;

/**
 * Created by M3talen on 2.1.2016..
 */
@FoamApiKeys(
        flurry = "M4XJ5WSGZRKVCSJ26PH7", // API Key
        googleAnalytics = "UA-71931652-1"
        // papertrail = "logs3.papertrailapp.com:21056" // Server URL
)
public class MainApp extends FoamApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);

        MultiplePermissionsListener dialogMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(this)
                        .withTitle("Storage permission")
                        .withMessage("Storage permission are needed to allow personification")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.drawable.ic_launcher)
                        .build();
        Dexter.checkPermissions(dialogMultiplePermissionsListener, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
