package com.lubenard.ft_hangouts.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

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

    public static void copyImageToInternalMemory(){
        return ;
    }
}
