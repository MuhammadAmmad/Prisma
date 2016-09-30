package com.compilesense.liuyi.prisma.util;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.compilesense.liuyi.prisma.PrismaApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by shenjingyuan002 on 16/9/26.
 */

public class Utils {
    static public int calculateInSampleSize(String imagePath, ImageView imageView){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int scrWidth   = options.outWidth;
        int scrHeight  = options.outHeight;
        int viewWidth  = imageView.getMaxWidth();
        int viewHeight = imageView.getMaxHeight();
        int inSampleSize = 1;

        if (scrHeight > viewHeight || scrWidth > viewWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) scrHeight / (float) viewHeight);
            final int widthRatio = Math.round((float) scrWidth / (float) viewWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    static public int calculateInSampleSize(Context context, int imageRes, ImageView imageView){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),imageRes, options);

        int scrWidth   = options.outWidth;
        int scrHeight  = options.outHeight;
        int viewWidth  = imageView.getMaxWidth();
        int viewHeight = imageView.getMaxHeight();
        int inSampleSize = 1;

        if (scrHeight > viewHeight || scrWidth > viewWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) scrHeight / (float) viewHeight);
            final int widthRatio = Math.round((float) scrWidth / (float) viewWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    public static String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "prismaImage");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));

        return file.getPath();
    }

}
