package cn.entertech.flowtimezh.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.core.widget.NestedScrollView;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.entertech.flowtimezh.R;


public class ShotShareUtil {
    public static String SHOT_PIC_SAVE_PATH = Environment.getExternalStorageDirectory().getPath()+ File.separator+"截图";
    public static void shotScreen(Context context) {
        String filePath;
        View dView = ((Activity) context).getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        if (bitmap != null) {
            FileOutputStream os = null;
            try {
                File rootDir = new File(SHOT_PIC_SAVE_PATH);
                if (!rootDir.exists()){
                    rootDir.mkdirs();
                }
                // 图片文件路径
                filePath = SHOT_PIC_SAVE_PATH + File.separator + System.currentTimeMillis()+".png";
                File file = new File(filePath);
                os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                Logger.d("ShotShareUtil", "截屏完成");
                View qrCodeView = View.inflate(context, R.layout.layout_product_share_view,null);
                Bitmap qrBitmap = convertViewToBitmap(qrCodeView,ScreenUtil.getScreenWidth(context),(int)ScreenUtil.dip2px(context,84));
                shareImage(context,concatFootBitmap(context,filePath,qrBitmap));
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("ShotShareUtil", "截屏失败：" + e.toString());
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static Bitmap shotScreenView(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap shotScrollView(Context context, NestedScrollView view, View headView, View footView){
        int height = 0;
        String filePath;
        //理论上scrollView只会有一个子View啦
        for (int i = 0; i < view.getChildCount(); i++) {
            height += view.getChildAt(i).getHeight();
        }
        //创建保存缓存的bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), height, Bitmap.Config.ARGB_8888);
        //可以简单的把Canvas理解为一个画板 而bitmap就是块画布
        Canvas canvas = new Canvas(bitmap);
        //获取ScrollView的背景颜色
        Drawable background = view.getBackground();
        //画出ScrollView的背景色 这里只用了color一种 有需要也可以自己扩展 也可以自己直接指定一种背景色
//        if (background instanceof ColorDrawable) {
//            ColorDrawable colorDrawable = (ColorDrawable) background;
//            int color = colorDrawable.getColor();
//            canvas.drawColor(color);
//        }
        canvas.drawColor(Color.parseColor("#f8f8f8"));
        //把view的内容都画到指定的画板Canvas上
        view.draw(canvas);
        if (bitmap != null) {
            FileOutputStream os = null;
            try {
                File rootDir = new File(SHOT_PIC_SAVE_PATH);
                if (!rootDir.exists()){
                    rootDir.mkdirs();
                }
                // 图片文件路径
                filePath = SHOT_PIC_SAVE_PATH + File.separator + System.currentTimeMillis()+".png";
                File file = new File(filePath);
                os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                Logger.d("ShotShareUtil", "截屏完成");
                String imagePath = null;
                if (headView != null){
                    Bitmap headBitmap = convertViewToBitmap(headView,ScreenUtil.getScreenWidth(context),(int)ScreenUtil.dip2px(context,191));
                    imagePath = concatHeadBitmap(context,filePath,headBitmap);
                    if (footView != null){
                        Bitmap footBitmap = convertViewToBitmap(footView,ScreenUtil.getScreenWidth(context),(int)ScreenUtil.dip2px(context,84));
                        imagePath = concatFootBitmap(context,imagePath,footBitmap);
                    }
                }else if (footView != null){
                    Bitmap footBitmap = convertViewToBitmap(footView,ScreenUtil.getScreenWidth(context),(int)ScreenUtil.dip2px(context,84));
                    imagePath = concatFootBitmap(context,filePath,footBitmap);
                }
                shareImage(context,imagePath);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("ShotShareUtil", "截屏失败：" + e.toString());
            } finally {
                if (os != null) {
                    try {
                        os.close();
                        bitmap.recycle();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bitmap;
    }

    public static String concatFootBitmap(Context context, String filePath, Bitmap footViewBitmap) {
        if (footViewBitmap == null) {
            return null;
        }
        int navHeight = getHeightWithNav(context) - getHeightWithoutNav(context);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(filePath, options);
        int width = options.outWidth;
//        int height = options.outHeight - navHeight;
        int height = options.outHeight;
        int max = 1024 * 1024 *2 ;
        int sampleSize = 1;
        while (width / sampleSize * height / sampleSize > max) {
            sampleSize *= 2;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;

        Bitmap srcBmp = BitmapFactory.decodeFile(filePath, options);
        //先计算bitmap的宽高，因为bitmap的宽度和屏幕宽度是不一样的，需要按比例拉伸
        double ratio = 1.0 * footViewBitmap.getWidth() / srcBmp.getWidth();
        int additionalHeight = (int) (footViewBitmap.getHeight() / ratio);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(footViewBitmap, srcBmp.getWidth(), additionalHeight, false);
        //到这里图片拉伸完毕

        //这里开始拼接，画到Canvas上
//        Bitmap result = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight() - navHeight / sampleSize + additionalHeight, Bitmap.Config.RGB_565);
        Bitmap result = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight() + additionalHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas();
        canvas.setBitmap(result);
        canvas.drawBitmap(srcBmp, 0, 0, null);
        //这里需要做个判断，因为一些系统是有导航栏的，所以截图时有导航栏，这里需要把导航栏遮住
        //计算出导航栏高度，然后draw时往上偏移一段距离

        double navRatio = 1.0 * context.getResources().getDisplayMetrics().widthPixels / srcBmp.getWidth();
//        canvas.drawBitmap(scaledBmp, 0, srcBmp.getHeight() - (int) (navHeight / navRatio), null);
        canvas.drawBitmap(scaledBmp, 0, srcBmp.getHeight() , null);

        if (result != null) {
            FileOutputStream os = null;
            try {
                // 图片文件路径
                filePath = SHOT_PIC_SAVE_PATH + File.separator + System.currentTimeMillis()+".png";
                File file = new File(filePath);
                os = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                Logger.d("ShotShareUtil", "截屏完成");
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("ShotShareUtil", "截屏失败：" + e.toString());
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        footViewBitmap.recycle();
        result.recycle();
        return filePath;
    }


    public static String concatHeadBitmap(Context context, String filePath, Bitmap headViewBitmap) {
        if (headViewBitmap == null) {
            return null;
        }
        int navHeight = getHeightWithNav(context) - getHeightWithoutNav(context);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(filePath, options);
        int width = options.outWidth;
//        int height = options.outHeight - navHeight;
        int height = options.outHeight;
        int max = 1024 * 1024 *2 ;
        int sampleSize = 1;
        while (width / sampleSize * height / sampleSize > max) {
            sampleSize *= 2;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;

        Bitmap srcBmp = BitmapFactory.decodeFile(filePath, options);
        //先计算bitmap的宽高，因为bitmap的宽度和屏幕宽度是不一样的，需要按比例拉伸
        double ratio = 1.0 * headViewBitmap.getWidth() / srcBmp.getWidth();
        int additionalHeight = (int) (headViewBitmap.getHeight() / ratio);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(headViewBitmap, srcBmp.getWidth(), additionalHeight, false);
        //到这里图片拉伸完毕

        //这里开始拼接，画到Canvas上
//        Bitmap result = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight() - navHeight / sampleSize + additionalHeight, Bitmap.Config.RGB_565);
        Bitmap result = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight() + additionalHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas();
        canvas.setBitmap(result);
        canvas.drawBitmap(srcBmp, 0, additionalHeight, null);
        //这里需要做个判断，因为一些系统是有导航栏的，所以截图时有导航栏，这里需要把导航栏遮住
        //计算出导航栏高度，然后draw时往上偏移一段距离

        double navRatio = 1.0 * context.getResources().getDisplayMetrics().widthPixels / srcBmp.getWidth();
//        canvas.drawBitmap(scaledBmp, 0, srcBmp.getHeight() - (int) (navHeight / navRatio), null);
        canvas.drawBitmap(scaledBmp, 0,0, null);

        if (result != null) {
            FileOutputStream os = null;
            try {
                // 图片文件路径
                filePath = SHOT_PIC_SAVE_PATH + File.separator + System.currentTimeMillis()+".png";
                File file = new File(filePath);
                os = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                Logger.d("ShotShareUtil", "截屏完成");
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("ShotShareUtil", "截屏失败：" + e.toString());
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        headViewBitmap.recycle();
        result.recycle();
        return filePath;
    }

    public static Bitmap convertViewToBitmap(View v, int width, int height) {
        //测量使得view指定大小
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        v.measure(measuredWidth, measuredHeight);
        //调用layout方法布局后，可以得到view的尺寸大小
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }

    /**分享**/
    private static void shareImage(Context context, String imagePath){
        if (imagePath != null){
            Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
            File file = new File(imagePath);
//            Uri uri = FileProvider.getUriForFile(context,
//                    context.getApplicationContext().getPackageName() +
//                            ".my.package.name.provider", file);
            Uri uri = getImageContentUri(context,file);
            intent.putExtra(Intent.EXTRA_STREAM, uri);// 分享的内容
            intent.setType("image/*");// 分享发送的数据类型
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent chooser = Intent.createChooser(intent, "Share");
            if(intent.resolveActivity(context.getPackageManager()) != null){
                context.startActivity(chooser);
            }
        } else {
            Logger.e("ShotShareUtil", "分享失败：");
        }
    }
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }

    /**
     * 获取屏幕高度，不包括navigation
     */
    public static int getHeightWithNav(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        } else {
            try {
                Method method = d.getClass().getDeclaredMethod("getRealMetrics");
                method.setAccessible(true);
                method.invoke(d, realDisplayMetrics);
            } catch (NoSuchMethodException e) {

            } catch (InvocationTargetException e) {

            } catch (IllegalAccessException e) {

            }
        }
        return realDisplayMetrics.heightPixels;
    }

    /**
     * 获取屏幕高度，包括navigation
     *
     * @return
     */
    public static int getHeightWithoutNav(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
