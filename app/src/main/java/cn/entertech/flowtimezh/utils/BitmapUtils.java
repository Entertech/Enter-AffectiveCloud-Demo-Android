package cn.entertech.flowtimezh.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import cn.entertech.flowtimezh.app.Application;


public class BitmapUtils {
    public static String bitmap2String(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap string2Bitmap(String base64) {
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public static Bitmap createBitmap(Bitmap source, float imageWidth, float imageHeight, int mCropGravity) {
        imageWidth = ScreenUtil.dip2px(Application.Companion.getInstance(), imageWidth);
        imageHeight = ScreenUtil.dip2px(Application.Companion.getInstance(), imageHeight);
        Bitmap bitmap;
        switch (mCropGravity) {
            case 0:
                if (imageWidth < source.getWidth()) {
                    bitmap = Bitmap.createBitmap(source,
                            (int) (source.getWidth() * 1.0f / 2 - imageWidth / 2), 0,
                            (int) imageWidth, (int) imageHeight);
                } else {
                    bitmap = Bitmap.createBitmap(source,
                            0, 0,
                            source.getWidth(), (int) imageHeight);
                }
                break;
            case 1:
                if (imageWidth < source.getWidth()) {
                    bitmap = Bitmap.createBitmap(source,
                            (int) (source.getWidth() * 1.0f / 2 - imageWidth / 2),
                            (int) (source.getHeight() * 1.0f / 2 - imageHeight / 2),
                            (int) imageWidth, (int) imageHeight);
                } else {
                    bitmap = Bitmap.createBitmap(source,
                            0,
                            (int) (source.getHeight() * 1.0f / 2 - imageHeight / 2),
                            source.getWidth(), (int) imageHeight);
                }

                break;
            case 2:
                bitmap = Bitmap.createBitmap(source, 0,
                        (int) (source.getHeight() - imageHeight),
                        (int) imageWidth, (int) imageHeight);
                break;
            default:
                bitmap = source;
                break;

        }
        return bitmap;
    }

    public static Bitmap blur(Context context, Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap);
        // 构建一个RenderScript对象
        RenderScript rs = RenderScript.create(context);
        // 创建高斯模糊脚本
        ScriptIntrinsicBlur gaussianBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //创建用于输入的脚本类型
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        // 创建用于输出的脚本类型
        Allocation allOut = Allocation.createFromBitmap(rs, output);
        // 设置模糊半径，范围0f<radius<=25f
        gaussianBlur.setRadius(radius);
        // 设置输入脚本类型
        gaussianBlur.setInput(allIn);
        // 执行高斯模糊算法，并将结果填入输出脚本类型中
        gaussianBlur.forEach(allOut);
        // 将输出内存编码为Bitmap，图片大小必须注意
        allOut.copyTo(output);
        // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
        RenderScript.releaseAllContexts();
        return output;
    }
}
