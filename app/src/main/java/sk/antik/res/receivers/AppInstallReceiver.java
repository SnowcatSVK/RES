package sk.antik.res.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AppInstallReceiver extends BroadcastReceiver {
    public AppInstallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        Log.e("Installations", packageName);
        File file;
        switch (packageName) {
            case "com.mobilityware.spider":
                Log.e("Installations", "installing angrybirds");
                file = new File(context.getExternalFilesDir(null).getPath() + "/com.mobilityware.spider.apk");
                file.delete();
                installPackage("com.rovio.angrybirds.apk", context);
                break;
            case "com.rovio.angrybirds":
                file = new File(context.getExternalFilesDir(null).getPath() + "/com.rovio.angrybirds.apk");
                file.delete();
                installPackage("candycrush.apk", context);
                break;
            case "com.king.candycrushsaga":
                file = new File(context.getExternalFilesDir(null).getPath() + "/candycrush.apk");
                file.delete();
                installPackage("com.twitter.android.apk", context);
                break;
            case "com.twitter.android":
                file = new File(context.getExternalFilesDir(null).getPath() + "/com.twitter.android.apk");
                file.delete();
                installPackage("com.android.chrome.apk", context);
                break;
            case "com.android,chrome" :
                file = new File(context.getExternalFilesDir(null).getPath() + "/com.android.chrome.apk");
                file.delete();
                break;
        }
    }

    public void installPackage(String packageName, Context context) {
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {

            in = assetManager.open(packageName);
            out = new FileOutputStream(context.getExternalFilesDir(null).getPath() + "/" + packageName);

            byte[] buffer = new byte[1024];

            int read;
            Log.e("Installations_instPkg", "Transfer start: " + packageName);
            while ((read = in.read(buffer)) != -1) {

                out.write(buffer, 0, read);

            }
            Log.e("Installations_instPkg", "Transfer end: " + packageName);
            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;

            Intent install = new Intent(Intent.ACTION_VIEW);

            install.setDataAndType(Uri.fromFile(new File(context.getExternalFilesDir(null).getPath() + "/" + packageName)),
                    "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
            Log.e("Installations_instPkg", packageName.substring(0, packageName.length() - 4));
        } catch (Exception e) {
        }

    }
}
