package cn.entertech.flowtimezh.utils;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cn.entertech.flowtimezh.app.SettingManager;

public class FileStoreHelper {

    public static FileStoreHelper mInstance = null;

    private Handler mHandler;
    private HandlerThread handlerThread;

    private FileStoreHelper() {
        handlerThread = new HandlerThread("store_file_thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    public static FileStoreHelper getInstance() {
        if (mInstance == null) {
            synchronized (FileStoreHelper.class) {
                if (mInstance == null) {
                    mInstance = new FileStoreHelper();
                }
            }
        }
        return mInstance;
    }

    private PrintWriter pw;
    private boolean isFirstWrite = true;

    public void setPath(String filePath, String fileName) {
        if (!SettingManager.getInstance().isSaveData()){
            return;
        }
        try {
            File file = createFile(filePath, fileName);
            pw = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File createFile(String filePath, String fileName) {
        if (!SettingManager.getInstance().isSaveData()){
            return null;
        }
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void writeData(String data) {
        if (!SettingManager.getInstance().isSaveData()){
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isFirstWrite) {
                    pw.print(data);
                    isFirstWrite = false;
                } else {
                    pw.append(data);
                }
                pw.flush();
            }
        });
    }

}
