package com.lubenard.ft_hangouts.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class Utils {
    /**
     *
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

}
