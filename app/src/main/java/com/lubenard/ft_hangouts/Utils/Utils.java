package com.lubenard.ft_hangouts.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Utils {

    // Very useful https://mkyong.com/java/how-to-write-to-file-in-java-fileoutputstream-example/
    public static void writeFileOnInternalStorage(Context mcoContext, String sFileName, Uri datasUri) {
        try {
            File file = new File(mcoContext.getFilesDir(), sFileName);
            FileOutputStream fop = new FileOutputStream(file);
            InputStream inputStream = mcoContext.getContentResolver().openInputStream(datasUri);
            while (inputStream.available() > 0)
                fop.write(inputStream.read());
            fop.close();
            Log.d("Utils", "wrote file to " + file.getAbsolutePath());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Check the requested permission, and if not already gave, ask for it
     * @param permRequired
     * @return
     */
    public static boolean checkOrRequestPerm(Activity activity, Context context, String permRequired) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permRequired) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else if (activity.shouldShowRequestPermissionRationale(permRequired)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.alertdialog_perm_not_granted_title))
                //   .setMessage(context.getResources().getString(R.string.alertdialog_perm_not_granted_desc)).setPositiveButton(context.getResources().getString(R.));
                return false;
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                activity.requestPermissions(new String[]{permRequired}, 1);
                return true;
            }
        }
        return false;
    }

    public static void copyImageToInternalMemory(){
        return ;
    }

}
