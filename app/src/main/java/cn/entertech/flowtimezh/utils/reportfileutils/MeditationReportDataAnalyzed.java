package cn.entertech.flowtimezh.utils.reportfileutils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager;
import cn.entertech.flowtimezh.app.SettingManager;
import cn.entertech.flowtimezh.entity.meditation.ReportMeditationDataEntity;

import static cn.entertech.flowtimezh.app.Constant.DEVICE_TYPE_CUSHION;
import static cn.entertech.flowtimezh.app.Constant.DEVICE_TYPE_ENTERTECH_VR;
import static cn.entertech.flowtimezh.app.Constant.DEVICE_TYPE_HEADBAND;
import static cn.entertech.flowtimezh.utils.reportfileutils.HexDump.floatArrayToByteArray;
import static cn.entertech.flowtimezh.utils.reportfileutils.HexDump.mergeByteArrays;
import static cn.entertech.flowtimezh.utils.reportfileutils.HexDump.toByteArray;

public class MeditationReportDataAnalyzed implements BrainDataUnit {
    //数据实时间隔 单位毫秒
    public static final int INTERRUPT_TIME_OF_BIODATA = 400;
    public static final int INTERRUPT_TIME_OF_AFFECTIVE = 800;
    /*新算法实时时间间隔默认为600ms*/
    public static final int INTERRUPT_TIME = 600;
    //标量数据容量，单位字节
    public static final int CONTAINER_OF_SCALAR_DATA = 3;
    //数组长度容量，单位字节
    public static final int CONTAINER_OF_ARRAY_LENGTH = 4;
    private String deviceType;
    private float deepDuration;
    private float lightDuration;
    private float soberDuration;
    private float sleepLatency;
    private float sleepPoint;
    private List<Double> sleepCurve;
    private ArrayList<MeditaionInterruptManager.MeditationInterrupt> interruptTimeStamps;
    private long startTime;

    private float attentionAvg;
    private List<Double> attentionRec;
    private float relaxationAvg;
    private List<Double> relaxationRec;
    private float pressureAvg;
    private List<Double> pressureRec;
    private float pleasureAvg;
    private List<Double> pleasureRec;

    private List<Double> alphaCurve;
    private List<Double> betaCurve;
    private List<Double> thetaCurve;
    private List<Double> deltaCurve;
    private List<Double> gammaCurve;
    private float hrAvg;
    private float hrMax;
    private float hrMin;
    private float hrvAvg;
    private List<Double> hrRec;
    private List<Double> hrvRec;

    @NotNull
    @Override
    public byte[] toFileBytes() {
        return mergeByteArrays(getScalarDataBytes()
                , getListByteArray("f1", alphaCurve),
                getListByteArray("f8", attentionRec), getListByteArray("f9", relaxationRec)
                , getListByteArray("fa", pressureRec), getListByteArray("fb", pleasureRec)
                , getListByteArray("f6", hrRec), getListByteArray("f7", hrvRec)
                , getListByteArray("f2", betaCurve)
                , getListByteArray("f3", thetaCurve), getListByteArray("f4", deltaCurve)
                , getListByteArray("f5", gammaCurve)
                , getListByteArray("fe", sleepCurve)
        );
    }

    public MeditationReportDataAnalyzed() {
    }

    public MeditationReportDataAnalyzed(ReportMeditationDataEntity reportMeditationDataEntity, long startTime, ArrayList<MeditaionInterruptManager.MeditationInterrupt> interruptTimeStamp) {
        this.startTime = startTime;
        this.interruptTimeStamps = interruptTimeStamp;
        deviceType = SettingManager.getInstance().getDeviceType();
        if (deviceType.equals(DEVICE_TYPE_CUSHION)){
            this.hrAvg = reportMeditationDataEntity.getReportPEPRDataEntity().getHrAvg().floatValue();
            this.hrMax = reportMeditationDataEntity.getReportPEPRDataEntity().getHrMax().floatValue();
            this.hrMin = reportMeditationDataEntity.getReportPEPRDataEntity().getHrMin().floatValue();
            this.hrvAvg = reportMeditationDataEntity.getReportPEPRDataEntity().getHrvAvg().floatValue();
            this.hrRec = addInterruptData(reportMeditationDataEntity.getReportPEPRDataEntity().getHrRec(), INTERRUPT_TIME);
            this.hrvRec = addInterruptData(reportMeditationDataEntity.getReportPEPRDataEntity().getHrvRec(), INTERRUPT_TIME);
            this.pressureRec = addInterruptData(reportMeditationDataEntity.getReportPressureEnitty().getPressureRec(), INTERRUPT_TIME);
        }else{
            this.hrAvg = reportMeditationDataEntity.getReportHRDataEntity().getHrAvg().floatValue();
            this.hrMax = reportMeditationDataEntity.getReportHRDataEntity().getHrMax().floatValue();
            this.hrMin = reportMeditationDataEntity.getReportHRDataEntity().getHrMin().floatValue();
            this.hrvAvg = reportMeditationDataEntity.getReportHRDataEntity().getHrvAvg().floatValue();
            this.hrRec = addInterruptData(reportMeditationDataEntity.getReportHRDataEntity().getHrRec(), INTERRUPT_TIME);
            this.hrvRec = addInterruptData(reportMeditationDataEntity.getReportHRDataEntity().getHrvRec(), INTERRUPT_TIME);
            this.attentionAvg = reportMeditationDataEntity.getReportAttentionEnitty().getAttentionAvg().floatValue();
            this.relaxationAvg = reportMeditationDataEntity.getReportRelaxationEnitty().getRelaxationAvg().floatValue();
            this.pressureAvg = reportMeditationDataEntity.getReportPressureEnitty().getPressureAvg().floatValue();
            this.pleasureAvg = reportMeditationDataEntity.getReportPleasureEnitty().getPleasureAvg().floatValue();
            this.sleepPoint = reportMeditationDataEntity.getReportSleepEntity().getSleepPoint().floatValue();
            this.sleepLatency = reportMeditationDataEntity.getReportSleepEntity().getSleepLatency().floatValue();
            this.soberDuration = reportMeditationDataEntity.getReportSleepEntity().getSoberDuration().floatValue();
            this.lightDuration = reportMeditationDataEntity.getReportSleepEntity().getLightDuration().floatValue();
            this.deepDuration = reportMeditationDataEntity.getReportSleepEntity().getDeepDuration().floatValue();

            this.attentionRec = addInterruptData(reportMeditationDataEntity.getReportAttentionEnitty().getAttentionRec(), INTERRUPT_TIME);
            this.relaxationRec = addInterruptData(reportMeditationDataEntity.getReportRelaxationEnitty().getRelaxationRec(), INTERRUPT_TIME);
            this.pressureRec = addInterruptData(reportMeditationDataEntity.getReportPressureEnitty().getPressureRec(), INTERRUPT_TIME);
            this.pleasureRec = addInterruptData(reportMeditationDataEntity.getReportPleasureEnitty().getPleasureRec(), INTERRUPT_TIME);
            this.alphaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getAlphaCurve(), INTERRUPT_TIME);
            this.betaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getBetaCurve(), INTERRUPT_TIME);
            this.thetaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getThetaCurve(), INTERRUPT_TIME);
            this.deltaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getDeltaCurve(), INTERRUPT_TIME);
            this.gammaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getGammaCurve(), INTERRUPT_TIME);
            this.sleepCurve = addInterruptData(reportMeditationDataEntity.getReportSleepEntity().getSleepCurve(), INTERRUPT_TIME);
        }
    }

    /**
     * 补齐体验中断数据
     *
     * @param source
     * @param interrupt
     * @return
     */
    public List<Double> addInterruptData(List<Double> source, int interrupt) {
        for (int i = 0; i < interruptTimeStamps.size(); i++) {
            long interruptStartTime = interruptTimeStamps.get(i).getInterruptStartTime();
            long interruptEndTime = interruptTimeStamps.get(i).getInterruptEndTime();
            if (interruptStartTime < startTime){
                continue;
            }
            int insertIndex = (int) (interruptStartTime - startTime) / interrupt;
            int insertCount = (int) (interruptEndTime - interruptStartTime) / interrupt;
            for (int j = 0; j < insertCount; j++) {
                if (insertIndex >= source.size()){
                    break;
                }
                source.add(insertIndex, 0.0);
            }
        }
        return source;
    }


    public byte[] getScalarDataBytes() {
        return mergeByteArrays(
                HexDump.hexSringToBytes("01"), HexDump.float2byte(hrAvg),
                HexDump.hexSringToBytes("02"), HexDump.float2byte(hrMax),
                HexDump.hexSringToBytes("03"), HexDump.float2byte(hrMin),
                HexDump.hexSringToBytes("04"), HexDump.float2byte(hrvAvg),
                HexDump.hexSringToBytes("07"), HexDump.float2byte(attentionAvg),
                HexDump.hexSringToBytes("0a"), HexDump.float2byte(relaxationAvg),
                HexDump.hexSringToBytes("0d"), HexDump.float2byte(pressureAvg),
                HexDump.hexSringToBytes("10"), HexDump.float2byte(pleasureAvg),
                HexDump.hexSringToBytes("1a"), HexDump.float2byte(sleepPoint),
                HexDump.hexSringToBytes("1b"), HexDump.float2byte(sleepLatency),
                HexDump.hexSringToBytes("1c"), HexDump.float2byte(soberDuration),
                HexDump.hexSringToBytes("1d"), HexDump.float2byte(lightDuration),
                HexDump.hexSringToBytes("1e"), HexDump.float2byte(deepDuration),
                HexDump.hexSringToBytes("20"), HexDump.float2byte(getDeviceTypeInFile()));

    }

    public float getDeviceTypeInFile(){
        if (deviceType.equals(DEVICE_TYPE_CUSHION)){
            return 2f;
        }else if (deviceType.equals(DEVICE_TYPE_HEADBAND)){
            return 1f;
        }else if (deviceType.equals(DEVICE_TYPE_ENTERTECH_VR)){
            return 3f;
        }else{
            return 0f;
        }
    }
    public byte[] getByteArray(List<Double> data) {
        if (data == null || data.size() == 0) {
            return new byte[0];
        }
        float[] floatArray = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            floatArray[i] = data.get(i).floatValue();
        }
        return floatArrayToByteArray(floatArray);
    }


    private String formatString(int val, int byteCount) {
//        Log.d("####", "val is " + val);
        if (val == -1.0 || val == -1) {
            return "ffffff";
        }
        return String.format("%0" + byteCount * 2 + "x", val);
    }

    public byte[] getListByteArray(String key, List<Double> list) {
        if (list == null) {
            return mergeByteArrays(HexDump.hexSringToBytes(key), HexDump.float2byte(0));
        }
        return mergeByteArrays(HexDump.hexSringToBytes(key), HexDump.float2byte(list.size()), getByteArray(list));
    }

    public float getAttentionAvg() {
        return attentionAvg;
    }

    public void setAttentionAvg(float attentionAvg) {
        this.attentionAvg = attentionAvg;
    }

    public List<Double> getAttentionRec() {
        return attentionRec;
    }

    public void setAttentionRec(List<Double> attentionRec) {
        this.attentionRec = attentionRec;
    }

    public float getRelaxationAvg() {
        return relaxationAvg;
    }

    public void setRelaxationAvg(float relaxationAvg) {
        this.relaxationAvg = relaxationAvg;
    }

    public List<Double> getRelaxationRec() {
        return relaxationRec;
    }

    public void setRelaxationRec(List<Double> relaxationRec) {
        this.relaxationRec = relaxationRec;
    }

    public float getPressureAvg() {
        return pressureAvg;
    }

    public void setPressureAvg(float pressureAvg) {
        this.pressureAvg = pressureAvg;
    }

    public List<Double> getPressureRec() {
        return pressureRec;
    }

    public void setPressureRec(List<Double> pressureRec) {
        this.pressureRec = pressureRec;
    }

    public float getPleasureAvg() {
        return pleasureAvg;
    }

    public void setPleasureAvg(float pleasureAvg) {
        this.pleasureAvg = pleasureAvg;
    }

    public List<Double> getPleasureRec() {
        return pleasureRec;
    }

    public void setPleasureRec(List<Double> pleasureRec) {
        this.pleasureRec = pleasureRec;
    }

    public List<Double> getAlphaCurve() {
        return alphaCurve;
    }

    public void setAlphaCurve(List<Double> alphaCurve) {
        this.alphaCurve = alphaCurve;
    }

    public List<Double> getBetaCurve() {
        return betaCurve;
    }

    public void setBetaCurve(List<Double> betaCurve) {
        this.betaCurve = betaCurve;
    }

    public List<Double> getThetaCurve() {
        return thetaCurve;
    }

    public void setThetaCurve(List<Double> thetaCurve) {
        this.thetaCurve = thetaCurve;
    }

    public List<Double> getDeltaCurve() {
        return deltaCurve;
    }

    public void setDeltaCurve(List<Double> deltaCurve) {
        this.deltaCurve = deltaCurve;
    }

    public List<Double> getGammaCurve() {
        return gammaCurve;
    }

    public void setGammaCurve(List<Double> gammaCurve) {
        this.gammaCurve = gammaCurve;
    }

    public float getHrAvg() {
        return hrAvg;
    }

    public void setHrAvg(float hrAvg) {
        this.hrAvg = hrAvg;
    }

    public float getHrMax() {
        return hrMax;
    }

    public void setHrMax(float hrMax) {
        this.hrMax = hrMax;
    }

    public float getHrMin() {
        return hrMin;
    }

    public void setHrMin(float hrMin) {
        this.hrMin = hrMin;
    }

    public float getHrvAvg() {
        return hrvAvg;
    }

    public void setHrvAvg(float hrvAvg) {
        this.hrvAvg = hrvAvg;
    }

    public List<Double> getHrRec() {
        return hrRec;
    }

    public void setHrRec(List<Double> hrRec) {
        this.hrRec = hrRec;
    }

    public List<Double> getHrvRec() {
        return hrvRec;
    }

    public void setHrvRec(List<Double> hrvRec) {
        this.hrvRec = hrvRec;
    }

    @Override
    public String toString() {
        return "MeditationReportDataAnalyzed{" +
                "deepDuration=" + deepDuration +
                ", lightDuration=" + lightDuration +
                ", soberDuration=" + soberDuration +
                ", sleepLatency=" + sleepLatency +
                ", sleepPoint=" + sleepPoint +
                ", sleepCurve=" + sleepCurve +
                ", interruptTimeStamps=" + interruptTimeStamps +
                ", startTime=" + startTime +
                ", attentionAvg=" + attentionAvg +
                ", attentionRec=" + attentionRec +
                ", relaxationAvg=" + relaxationAvg +
                ", relaxationRec=" + relaxationRec +
                ", pressureAvg=" + pressureAvg +
                ", pressureRec=" + pressureRec +
                ", pleasureAvg=" + pleasureAvg +
                ", pleasureRec=" + pleasureRec +
                ", alphaCurve=" + alphaCurve +
                ", betaCurve=" + betaCurve +
                ", thetaCurve=" + thetaCurve +
                ", deltaCurve=" + deltaCurve +
                ", gammaCurve=" + gammaCurve +
                ", hrAvg=" + hrAvg +
                ", hrMax=" + hrMax +
                ", hrMin=" + hrMin +
                ", hrvAvg=" + hrvAvg +
                ", hrRec=" + hrRec +
                ", hrvRec=" + hrvRec +
                '}';
    }

    public float getDeepDuration() {
        return deepDuration;
    }

    public void setDeepDuration(float deepDuration) {
        this.deepDuration = deepDuration;
    }

    public float getLightDuration() {
        return lightDuration;
    }

    public void setLightDuration(float lightDuration) {
        this.lightDuration = lightDuration;
    }

    public float getSoberDuration() {
        return soberDuration;
    }

    public void setSoberDuration(float soberDuration) {
        this.soberDuration = soberDuration;
    }

    public float getSleepLatency() {
        return sleepLatency;
    }

    public void setSleepLatency(float sleepLatency) {
        this.sleepLatency = sleepLatency;
    }

    public float getSleepPoint() {
        return sleepPoint;
    }

    public void setSleepPoint(float sleepPoint) {
        this.sleepPoint = sleepPoint;
    }

    public List<Double> getSleepCurve() {
        return sleepCurve;
    }

    public void setSleepCurve(List<Double> sleepCurve) {
        this.sleepCurve = sleepCurve;
    }

    public void setDeviceType(float deviceType){
        if (deviceType == 1f){
            this.deviceType = DEVICE_TYPE_HEADBAND;
        }else if (deviceType == 2f){
            this.deviceType = DEVICE_TYPE_CUSHION;
        }else if (deviceType == 3f){
            this.deviceType = DEVICE_TYPE_ENTERTECH_VR;
        }
    }

    public String getDeviceType(){
        return this.deviceType;
    }
}
