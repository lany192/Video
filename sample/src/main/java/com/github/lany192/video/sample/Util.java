package com.github.lany192.video.sample;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Locale;

public class Util {

    public static void writeFile(Context context) {
        try {
            OutputStream os = getLogStream(context);
            os.write(getInformation(context).getBytes("utf-8"));
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(Context context, String str) {
        try {
            OutputStream os = getLogStream(context);
            os.write(str.getBytes("utf-8"));
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OutputStream getLogStream(Context context) throws IOException {
        //crash_log_pkgname.log
        String model = Build.MODEL.replace(" ", "_");
        String fileName = String.format("compress_" + model + ".log", context.getPackageName());
        File file = new File(Environment.getExternalStorageDirectory(), fileName);

        if (!file.exists()) {
            file.createNewFile();
        }

        return new FileOutputStream(file, true);
    }

    public static String getInformation(Context context) {
        long current = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder().append('\n');
        sb.append("BOARD: ").append(Build.BOARD).append('\n');
        sb.append("BOOTLOADER: ").append(Build.BOOTLOADER).append('\n');
        sb.append("BRAND: ").append(Build.BRAND).append('\n');
        sb.append("CPU_ABI: ").append(Build.CPU_ABI).append('\n');
        sb.append("CPU_ABI2: ").append(Build.CPU_ABI2).append('\n');
        sb.append("DEVICE: ").append(Build.DEVICE).append('\n');
        sb.append("DISPLAY: ").append(Build.DISPLAY).append('\n');
        sb.append("FINGERPRINT: ").append(Build.FINGERPRINT).append('\n');
        sb.append("HARDWARE: ").append(Build.HARDWARE).append('\n');
        sb.append("HOST: ").append(Build.HOST).append('\n');
        sb.append("ID: ").append(Build.ID).append('\n');
        sb.append("MANUFACTURER: ").append(Build.MANUFACTURER).append('\n');
        sb.append("MODEL: ").append(Build.MODEL).append('\n');
        sb.append("PRODUCT: ").append(Build.PRODUCT).append('\n');
        sb.append("SERIAL: ").append(Build.SERIAL).append('\n');
        sb.append("TAGS: ").append(Build.TAGS).append('\n');
        sb.append("TIME: ").append(Build.TIME).append(' ').append(toDateString(Build.TIME)).append('\n');
        sb.append("TYPE: ").append(Build.TYPE).append('\n');
        sb.append("USER: ").append(Build.USER).append('\n');
        sb.append("VERSION.CODENAME: ").append(Build.VERSION.CODENAME).append('\n');
        sb.append("VERSION.INCREMENTAL: ").append(Build.VERSION.INCREMENTAL).append('\n');
        sb.append("VERSION.RELEASE: ").append(Build.VERSION.RELEASE).append('\n');
        sb.append("VERSION.SDK_INT: ").append(Build.VERSION.SDK_INT).append('\n');
        sb.append("LANG: ").append(context.getResources().getConfiguration().locale.getLanguage()).append('\n');
        sb.append("APP.VERSION.NAME: ").append(getVersionName(context)).append('\n');
        sb.append("APP.VERSION.CODE: ").append(getVersionCode(context)).append('\n');
        sb.append("CURRENT: ").append(current).append(' ').append(toDateString(current)).append('\n');

        return sb.toString();
    }

    public static String toDateString(long timeMilli) {
        Calendar calc = Calendar.getInstance();
        calc.setTimeInMillis(timeMilli);
        return String.format(Locale.CHINESE, "%04d.%02d.%02d %02d:%02d:%02d:%03d",
                calc.get(Calendar.YEAR), calc.get(Calendar.MONTH) + 1, calc.get(Calendar.DAY_OF_MONTH),
                calc.get(Calendar.HOUR_OF_DAY), calc.get(Calendar.MINUTE), calc.get(Calendar.SECOND), calc.get(Calendar.MILLISECOND));
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        String version = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        int version = 0;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }
}
