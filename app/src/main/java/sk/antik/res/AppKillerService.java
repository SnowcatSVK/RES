package sk.antik.res;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sk.antik.res.restrictors.PINActivity;

public class AppKillerService extends Service {

    private ActivityManager am;
    public String lastFrontAppPkg;
    private ApkInfo apkInfo;
    //public boolean startingForbiddenApp;
    private Timer t;

    public AppKillerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //startingForbiddenApp = false;
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    killApp("com.android.settings");
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 5000, 200);
        lastFrontAppPkg = "nada";
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void killApp(String packagename) throws PackageManager.NameNotFoundException {
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            try {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (!lastFrontAppPkg.equals(appProcess.pkgList[0])) {
                        //Log.e("SettingsTest", appProcess.pkgList[0]);
                        apkInfo = ApkInfo.getInfoFromPackageName(appProcess.pkgList[0], this);
                        if (apkInfo.packageInfo.applicationInfo.flags != ApplicationInfo.FLAG_SYSTEM) {
                            //Log.e("SettingsTest", "Non system app");
                            if (!(apkInfo.packageName.equals("com.android.server.telecom") ||
                                    apkInfo.packageName.equals("com.android.providers.telephony") ||
                                    apkInfo.packageName.equals("com.android.systemui") ||
                                    apkInfo.packageName.equals("android") || apkInfo.packageName.equals("com.cghs.stresstest")
                                    || apkInfo.packageName.equals("com.android.providers.applications") || apkInfo.packageName.equals("com.sec.android.provider.logsprovider")
                                    || apkInfo.packageName.equals("com.samsung.sec.android.application.csc") || apkInfo.packageName.equals("com.android.providers.security")
                                    || apkInfo.packageName.equals("com.sec.android.sCloudRelayData") || apkInfo.packageName.equals("com.google.android.gms")
                                    || apkInfo.packageName.equals("com.sec.pcw") || apkInfo.packageName.equals("com.sec.spp.push")
                                    || apkInfo.packageName.equals("com.google.android.apps.plus") || apkInfo.packageName.equals("com.android.vending")
                                    || apkInfo.packageName.equals("com.google.android.talk") || apkInfo.packageName.equals("com.google.android.googlequicksearchbox")
                                    || apkInfo.packageName.equals("com.sec.android.app.FileShareServer") || apkInfo.packageName.equals("com.sec.android.gallery3d")
                                    || apkInfo.packageName.equals("com.sec.android.inputmethod") || apkInfo.packageName.equals("com.google.android.partnersetup")
                                    || apkInfo.packageName.equals("com.android.contacts") || apkInfo.packageName.equals("com.google.android.syncadapters.contacts")
                                    || apkInfo.packageName.equals("org.simalliance.openmobileapi.service") || apkInfo.packageName.equals("com.wssyncmldm")
                                    || apkInfo.packageName.equals("com.sec.android.providers.downloads") || apkInfo.packageName.equals("com.samsung.groupcast")
                                    || apkInfo.packageName.equals("com.samsung.videohub") || apkInfo.packageName.equals("com.sec.android.app.keyguard")
                                    || apkInfo.packageName.equals("com.sec.android.pagebuddynotisvc"))) {
                                // Log.e("SettingsTest", "LastFrontAppPkg: " + lastFrontAppPkg);
                                Log.e("SettingsTest", "LastFrontAppPkg: " + lastFrontAppPkg + ", foreground package: " + apkInfo.packageName);
                                lastFrontAppPkg = appProcess.pkgList[0];
                                if (apkInfo.packageName.equals(packagename)) {
                                    Intent intent = new Intent(this, PINActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("MODE", "Settings");
                                    startActivity(intent);
                                    t.cancel();
                                    t.purge();
                                    stopSelf();
                                    //Log.e("SettingsTest", "LastFrontAppPkg: " + lastFrontAppPkg + ", foreground package: " + apkInfo.packageName);

                                }
                            }
                        }
                        //kill the app
                        //Here do the pupop with password to launch the lastFrontAppPkg if the pass is correct
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }


}



