package sk.antik.res;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Snowcat on 15.01.2016.
 */
public class ApkInfo {

    public String appName;
    public String packageName;
    public String versionName;
    public int versionCode;
    public PackageInfo packageInfo;

    public ApkInfo() {

    }

    public static ApkInfo getInfoFromPackageName(String name, Context context) {
        ApkInfo newInfo = new ApkInfo();
        try {
            PackageInfo p = context.getPackageManager().getPackageInfo(
                    name, PackageManager.GET_PERMISSIONS);

            newInfo.appName = p.applicationInfo.loadLabel(
                    context.getPackageManager()).toString();
            newInfo.packageName = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.packageInfo = (p);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return newInfo;
    }
}
