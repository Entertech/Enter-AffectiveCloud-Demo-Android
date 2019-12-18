package cn.entertech.affectiveclouddemo.utils.reportfileutils;

import android.content.Context;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

import cn.entertech.affectiveclouddemo.R;

/**
 * Created by EnterTech on 2017/3/20.
 * 硬件读写文件帮助类
 */

public class FileHelper {
    private final static String VERSION = "0100";
    private final static String HEADER_LEN = "20";
    private final static String TYPE_SOUCE = "01";
    private final static String TYPE_ANALYZED = "02";
    private final static String DATA_VERSION = "03010000";


    public static String getRawHeader() {
        return getHeaderByType(TYPE_SOUCE);
    }


    public static String getAnalyzedHeader() {
        return getHeaderByType(TYPE_ANALYZED);
    }


    private static String getHeaderByType(String type) {
        Calendar calendar = Calendar.getInstance();
        long utcTick = calendar.getTimeInMillis();
        // TODO: 2017/3/21 校验和
        return VERSION + HEADER_LEN + type + DATA_VERSION + "0000000000000000"
                + Long.toHexString(utcTick / 1000) + "000000000000000000000000";
    }

    /**
     * 更新文件数据长度
     *
     * @param file
     * @param str
     */
    public static void updateDataLength(File file, String str) {
        try {
            RandomAccessFile rf = new RandomAccessFile(file, "rw");
            rf.seek(8);
            byte[] cur = new byte[6];
            rf.read(cur, 0, 6);
//            Logger.d("read long = "+HexDump.toHexString(cur));
            Long curLen = Long.valueOf(HexDump.toHexString(cur), 16);
            curLen += (str.length() / 2);
//            Logger.d("read curLen = "+curLen);
            rf.seek(8);
            rf.write(HexDump.hexStringToByteArray(String.format("%012x", curLen)));
            rf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析分析后文件
     */
    //todo 临时只简单的连接各个片段，暂时不考虑中断等情况
    public static FileProtocol<? extends BrainDataUnit> getMeditationReport(Context context, String fileName) {
        String path = FileUtil.getMeditationReportDir() + "/" + fileName + FileUtil.getMeditationReportExtention();
//        Logger.d(path);
        String string = FileUtil.readFile(path);

        if (null == string || string.equals("")) {
            string = FileUtil.readRaw(context, R.raw.sample);
        }

        int protocolVersion = Integer.valueOf(StringUtil.substring(string, 0, 4), 16);
        int headLength = Integer.valueOf(StringUtil.substring(string, 4, 6), 16);
        int fileType = Integer.valueOf(StringUtil.substring(string, 6, 8), 16);
        long dataVersion = Long.valueOf(StringUtil.substring(string, 8, 16), 16);
        long dataLength = Long.valueOf(StringUtil.substring(string, 16, 28), 16);
        int checkSum = Integer.valueOf(StringUtil.substring(string, 28, 32), 16);
        long tick = Long.valueOf(StringUtil.substring(string, 32, 40), 16);

        FileProtocol<BrainDataUnit> fileProtocol = new FileProtocol<>(protocolVersion, headLength,
                fileType, dataVersion, dataLength, checkSum, tick);
        fileProtocol.add(FileParser.parseMeditationReport(string));
        return fileProtocol;
    }


}
