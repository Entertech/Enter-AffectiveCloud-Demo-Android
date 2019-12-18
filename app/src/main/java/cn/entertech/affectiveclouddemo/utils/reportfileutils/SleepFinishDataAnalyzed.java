package cn.entertech.affectiveclouddemo.utils.reportfileutils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class SleepFinishDataAnalyzed implements BrainDataUnit {

    //曲线时间间隔,单位毫秒
    public static final int TIME_INTERVAL_OF_NAP_CURVE = 60000;
    //标量数据容量，单位字节
    public static final int CONTAINER_OF_SCALAR_DATA = 3;
    //数组长度容量，单位字节
    public static final int CONTAINER_OF_ARRAY_LENGTH = 3;
    private short clockPoint;
    private short sleepPoint;
    private short alarmPoint;
    private short napScore;
    private short napClassSober = 80;
    private short napClassBlur = 60;
    private short napClassSleep = 30;
    private short wearQuality = -1;
    private List<Integer> napCurve;
    private List<Double> noiseLvList;
    private List<Integer> snoreList;
    private List<Integer> daydreamList;

    @NotNull
    @Override
    public byte[] toFileBytes() {
        return mergeByteArrays(HexDump.hexSringToBytes(getScalarData()),
                getListByteArray("f1", napCurve), getListByteArray("f3", noiseLvList)
                , getListByteArray("f4", daydreamList), getListByteArray("f5", snoreList));

    }

    public SleepFinishDataAnalyzed() {
    }

//    public SleepFinishDataAnalyzed(SleepFinishResult sleepFinishResult, List<Double> noise, List<Integer> snore, List<Integer> daydream, int clockPoint) {
//        this.sleepPoint = (short) sleepFinishResult.getSleepPoint();
//        this.alarmPoint = (short) sleepFinishResult.getAlarmPoint();
//        this.clockPoint = (short) clockPoint;
//        this.napScore = (short) sleepFinishResult.getScore();
//        this.wearQuality = (short) sleepFinishResult.getDetectQuality();
//        this.napCurve = sleepFinishResult.getSleepCurveCom();
//        this.noiseLvList = noise;
//        this.snoreList = snore;
//        this.daydreamList = daydream;
//    }

    public String getScalarData() {
        return "01" + formatString(napScore, CONTAINER_OF_SCALAR_DATA) + "02" + formatString(sleepPoint, CONTAINER_OF_SCALAR_DATA)
                + "03" + formatString(alarmPoint, CONTAINER_OF_SCALAR_DATA) + "04" + formatString(napClassSober, CONTAINER_OF_SCALAR_DATA)
                + "05" + formatString(napClassBlur, CONTAINER_OF_SCALAR_DATA) + "06" + formatString(napClassSleep, CONTAINER_OF_SCALAR_DATA)
                + "07" + formatString(TIME_INTERVAL_OF_NAP_CURVE, CONTAINER_OF_SCALAR_DATA) + "08" + formatString(wearQuality, CONTAINER_OF_SCALAR_DATA)
                +"09"+formatString(getNapLen(),CONTAINER_OF_SCALAR_DATA)+"10"+formatString(clockPoint,CONTAINER_OF_SCALAR_DATA);
    }

    public int getNapLen(){
        if (sleepPoint == -1 || napCurve== null || napCurve.size() == 0 || napCurve.size() <= sleepPoint){
            return 0;
        }
        return napCurve.size() - sleepPoint;
    }

    public <T> byte[] getByteArray(List<T> data) {
        if (data == null || data.size() == 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) instanceof Double) {
                bytes[i] = ((Double) data.get(i)).byteValue();
            }
            if (data.get(i) instanceof Integer) {
                bytes[i] = ((Integer) data.get(i)).byteValue();
            }
        }
        return bytes;
    }

    private String formatString(int val, int byteCount) {
        Log.d("####", "val is " + val);
        if (val == -1.0 || val == -1) {
            return "ffffff";
        }
        return String.format("%0" + byteCount * 2 + "x", val);
    }

    public byte[] mergeByteArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public <T> byte[] getListByteArray(String key, List<T> list) {
        if (list == null) {
            return mergeByteArrays(HexDump.hexSringToBytes(key + formatString(0,
                    CONTAINER_OF_ARRAY_LENGTH)));
        }
        return mergeByteArrays(HexDump.hexSringToBytes(key + formatString(list.size(),
                CONTAINER_OF_ARRAY_LENGTH)), getByteArray(list));
    }

//    public byte[] getNapCurveByteArray() {
//        return mergeByteArrays(HexDump.hexSringToBytes("f1" + formatString(napCurve.size(),
//                CONTAINER_OF_ARRAY_LENGTH)),getByteArray(napCurve));
//    }
//
//    public byte[] getNapClassByteArray() {
//        return mergeByteArrays(HexDump.hexSringToBytes("f2" + formatString(napClass.size(),
//                CONTAINER_OF_ARRAY_LENGTH)),getByteArray(napClass));
//    }

    public short getSleepPoint() {
        return sleepPoint;
    }

    public void setSleepPoint(short sleepPoint) {
        this.sleepPoint = sleepPoint;
    }

    public short getAlarmPoint() {
        return alarmPoint;
    }

    public void setAlarmPoint(short alarmPoint) {
        this.alarmPoint = alarmPoint;
    }

    public short getNapScore() {
        return napScore;
    }

    public void setNapScore(short napScore) {
        this.napScore = napScore;
    }

    public short getNapClassSober() {
        return napClassSober;
    }

    public void setNapClassSober(short napClassSober) {
        this.napClassSober = napClassSober;
    }

    public short getNapClassBlur() {
        return napClassBlur;
    }

    public void setNapClassBlur(short napClassBlur) {
        this.napClassBlur = napClassBlur;
    }

    public short getNapClassSleep() {
        return napClassSleep;
    }

    public void setNapClassSleep(short napClassSleep) {
        this.napClassSleep = napClassSleep;
    }

    public List<Integer> getNapCurve() {
        return napCurve;
    }

    public void setNapCurve(List<Integer> napCurve) {
        this.napCurve = napCurve;
    }

    public short getWearQuality() {
        return wearQuality;
    }

    public void setWearQuality(short wearQuality) {
        this.wearQuality = wearQuality;
    }


    public List<Double> getNoiseLvList() {
        return noiseLvList;
    }

    public void setNoiseLvList(List<Double> noiseLvList) {
        this.noiseLvList = noiseLvList;
    }

    public List<Integer> getSnoreList() {
        return snoreList;
    }

    public void setSnoreList(List<Integer> snoreList) {
        this.snoreList = snoreList;
    }

    public List<Integer> getDaydreamList() {
        return daydreamList;
    }

    public void setDaydreamList(List<Integer> daydreamList) {
        this.daydreamList = daydreamList;
    }

    @Override
    public String toString() {
        return "SleepFinishDataAnalyzed{" +
                "sleepPoint=" + sleepPoint +
                ", alarmPoint=" + alarmPoint +
                ", napScore=" + napScore +
                ", napClassSober=" + napClassSober +
                ", napClassBlur=" + napClassBlur +
                ", napClassSleep=" + napClassSleep +
                ", wearQuality=" + wearQuality +
                ", napCurve=" + napCurve +
                ", noiseLvList=" + noiseLvList +
                ", snoreList=" + snoreList +
                ", daydreamList=" + daydreamList +
                '}';
    }

    public short getClockPoint() {
        return clockPoint;
    }

    public void setClockPoint(short clockPoint) {
        this.clockPoint = clockPoint;
    }
}
