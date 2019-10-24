package com.laiyifen.capital;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.DrawableRes;
import androidx.core.content.FileProvider;

public class PicturlUtils {

    public static Uri getImageUri(Context context, String name) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/mipmap/" + name);
    }
    public static int sharePic(Context context, String picFilePath) {
        File shareFile = new File(picFilePath);
        if (!shareFile.exists())
            return 100;
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName()+".FileProvider", shareFile);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
        }
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent,"分享");
        if(intent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(chooser);
        }
        return 0;
    }


    public static void saveMyBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        File appDir = new File(Environment.getExternalStorageDirectory(), "mypicture");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "aiwuicon"+ ".jpg";
        File file = new File(appDir, fileName);
        if(file.exists()){
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getResourcesUri(Context context,@DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }

}
