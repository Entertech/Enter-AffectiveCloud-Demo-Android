package cn.entertech.affectiveclouddemo.utils.reportfileutils;

import android.content.Context;
import android.os.Environment;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import cn.entertech.affectiveclouddemo.app.Application;

import static cn.entertech.affectiveclouddemo.utils.reportfileutils.Constants.FIRMWARE_FILE;
import static cn.entertech.affectiveclouddemo.utils.reportfileutils.Constants.MEDITATION_AUDIO_FILE;
import static cn.entertech.affectiveclouddemo.utils.reportfileutils.Constants.MEDITATION_REPORT_FILE;

/**
 * Created by EnterTech on 2017/1/12.
 */

public class FileUtil {

    // 获取分析后脑波文件路径
    public static String getMeditationReportDir() {
        return getFilesDir() + "/" + MEDITATION_REPORT_FILE;
    }

    // 获取分析后脑波文件后缀
    public static String getMeditationReportExtention() {
        return ".report";
    }


    // 获取体验音频路径
    public static String getMeditationAudioDir() {
        return getFilesDir() + "/" + MEDITATION_AUDIO_FILE;
    }

    // 获取体验音频路径后缀
    public static String getMeditationAudioExtention() {
        return ".mp3";
    }


    // 获取体验音频路径
    public static String getFirmwareDir() {
        return getFilesDir() + "/" + FIRMWARE_FILE;
    }

    // 获取体验音频路径后缀
    public static String getFirmwareExtention() {
        return ".zip";
    }


    /**
     * 获取私有文件存储路径，如脑波文件
     *
     * @return 私有文件存储路径
     */
    public static File getFilesDir() {
        return getContext().getFilesDir();
    }

    /**
     * 获取普通文件存储路径，如音乐
     *
     * @return 普通文件存储路径
     */
    private static File getExternalFilesDir() {
        return getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    }

    /**
     * 获取截图路径
     *
     * @return 截图路径
     */
    public static File getScreenShotDir() {
        return getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/Naptime/screenshot/");
    }

    /**
     * 获取缓存文件存储路径，如音乐缓存
     *
     * @return 缓存文件存储路径
     */
    private static File getExternalCacheDir() {
        return getContext().getExternalCacheDir();
    }

    private static Context getContext() {
        return Application.Companion.getInstance();
    }

    /**
     * 读取app内部文件
     *
     * @param
     * @return
     * @throws IOException
     */
    public static String readRaw(Context context, int resId) {
        try {
            InputStream is = context.getResources().openRawResource(resId);
            int length = is.available();
//            Logger.d("len = " + length);
            byte[] buffer = new byte[length];
            is.read(buffer);
            String res = HexDump.toHexString(buffer);
            is.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName) {
        File file = new File(fileName);
        FileInputStream fis = null;
        try {
            if (!file.exists()) return null;
            fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            String res = HexDump.toHexString(buffer);
            fis.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写文件
     *
     * @param fileName
     * @param str
     * @throws IOException
     */
    public static void writeFile(String fileName, String str, byte[] header) {
        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            if (0 == file.length()) {
                fos.write(header);
            }
            // 修改数据内容长度
            FileHelper.updateDataLength(file, str);
            byte[] bytes = HexDump.hexStringToByteArray(str);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//    /**
//     * 下载文件
//     *
//     * @param response
//     * @param destFileName
//     * @throws IOException
//     */
//    public static void saveFile(Response response, String path, String destFileName, OnProgressListener listener, boolean isReset) throws IOException {
//        InputStream is = null;
//        byte[] buf = new byte[2048];
//        int len = 0;
//        FileOutputStream fos = null;
//        try {
//            is = response.body().byteStream();
//            final long total = response.body().contentLength();
//            long sum = 0;
//            File dir = new File(path);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            File file = new File(dir, destFileName);
//            if (file.exists()) {
//                if (!isReset) {
//                    Logger.d("exists " + destFileName);
//                    return;
//                } pic_arousal_pleasure_emotion_else {
//                    Logger.d("delete exists" + destFileName);
//                    file.delete();
//                }
//            }
//            fos = new FileOutputStream(file);
//            while ((len = is.read(buf)) != -1) {
//                sum += len;
//                fos.write(buf, 0, len);
//                if (null != listener) {
////                    Logger.d("total = " + total + "sum = "+sum);
//                    listener.onProgress((int) ((sum * 100) / total));
//                }
//            }
//            fos.flush();
//        } finally {
//            if (null != is) is.close();
//            if (null != fos) fos.close();
//        }
//    }

//    public static void saveReportFile(Response response, String destFileName) throws IOException {
//        saveFile(response, FileUtil.getMeditationReportDir(), destFileName + getMeditationReportExtention(), null, false);
//    }

    public static long getFolderSize(File file) {
        long size = 0;
        if (!file.exists()) {
            return size;
        } else {
            for (int i = 0; i < file.listFiles().length; i++) {
                size = size + file.listFiles()[i].length();
            }
        }
        return size;
    }

    public static void deleteEarlyFile(File folder) {
        if (!folder.exists()) {
            return;
        } else {
            File[] files = folder.listFiles();
            if (files.length <= 0) {
                return;
            }
            File earlyFile = files[0];
            for (int i = 0; i < files.length; i++) {
                if (files[i].lastModified() < earlyFile.lastModified()) {
                    earlyFile = files[i];
                }
            }
            earlyFile.delete();
        }
    }

    public interface OnProgressListener {
        void onProgress(int percent);
    }
}
