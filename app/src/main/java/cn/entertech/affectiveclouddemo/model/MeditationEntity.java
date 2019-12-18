package cn.entertech.affectiveclouddemo.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_meditation")
public class MeditationEntity {

    /**
     * id : 5
     * created_at : 2019-06-17T02:49:02.980879Z
     * updated_at : 2019-06-17T02:49:02.980920Z
     * start_time : 2019-06-17T10:18:00Z
     * finish_time : 2019-06-17T10:23:00Z
     * heart_rate_avg : 80
     * heart_rate_max : 80
     * heart_rate_min : 80
     * heart_rate_variability_avg : 80
     * attention_avg : 80
     * attention_max : 80
     * attention_min : 80
     * relaxation_avg : 80
     * relaxation_max : 80
     * relaxation_min : 80
     * meditation_file : null
     * user : 1
     */

    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "meditation_id", unique = true)
    private long id;
    @DatabaseField(columnName = "created_at")
    @SerializedName("created_at")
    private String createdAt;
    @DatabaseField(columnName = "updated_at")
    @SerializedName("updated_at")
    private String updatedAt;
    @DatabaseField(columnName = "start_time")
    @SerializedName("start_time")
    private String startTime;
    @DatabaseField(columnName = "finish_time")
    @SerializedName("finish_time")
    private String finishTime;
    @DatabaseField(columnName = "heart_rate_avg")
    @SerializedName("heart_rate_avg")
    private float heartRateAvg;
    @DatabaseField(columnName = "heart_rate_max")
    @SerializedName("heart_rate_max")
    private float heartRateMax;
    @DatabaseField(columnName = "heart_rate_min")
    @SerializedName("heart_rate_min")
    private float heartRateMin;
    @DatabaseField(columnName = "heart_rate_variability_avg")
    @SerializedName("heart_rate_variability_avg")
    private float heartRateVariabilityAvg;
    @DatabaseField(columnName = "attention_avg")
    @SerializedName("attention_avg")
    private float attentionAvg;
    @DatabaseField(columnName = "attention_max")
    @SerializedName("attention_max")
    private float attentionMax;
    @DatabaseField(columnName = "attention_min")
    @SerializedName("attention_min")
    private float attentionMin;
    @DatabaseField(columnName = "relaxation_avg")
    @SerializedName("relaxation_avg")
    private float relaxationAvg;
    @DatabaseField(columnName = "relaxation_max")
    @SerializedName("relaxation_max")
    private float relaxationMax;
    @DatabaseField(columnName = "relaxation_min")
    @SerializedName("relaxation_min")
    private float relaxationMin;
    @DatabaseField(columnName = "meditation_file")
    @SerializedName("meditation_file")
    private String meditationFile;
    @DatabaseField(columnName = "user")
    private int user;
    @DatabaseField(columnName = "is_file_upload")
    private boolean isFileUpload;
    @DatabaseField(columnName = "is_file_get")
    private boolean isFileGet;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public float getHeartRateAvg() {
        return heartRateAvg;
    }

    public void setHeartRateAvg(float heartRateAvg) {
        this.heartRateAvg = heartRateAvg;
    }

    public float getHeartRateMax() {
        return heartRateMax;
    }

    public void setHeartRateMax(float heartRateMax) {
        this.heartRateMax = heartRateMax;
    }

    public float getHeartRateMin() {
        return heartRateMin;
    }

    public void setHeartRateMin(float heartRateMin) {
        this.heartRateMin = heartRateMin;
    }

    public float getHeartRateVariabilityAvg() {
        return heartRateVariabilityAvg;
    }

    public void setHeartRateVariabilityAvg(float heartRateVariabilityAvg) {
        this.heartRateVariabilityAvg = heartRateVariabilityAvg;
    }

    public float getAttentionAvg() {
        return attentionAvg;
    }

    public void setAttentionAvg(float attentionAvg) {
        this.attentionAvg = attentionAvg;
    }

    public float getAttentionMax() {
        return attentionMax;
    }

    public void setAttentionMax(float attentionMax) {
        this.attentionMax = attentionMax;
    }

    public float getAttentionMin() {
        return attentionMin;
    }

    public void setAttentionMin(float attentionMin) {
        this.attentionMin = attentionMin;
    }

    public float getRelaxationAvg() {
        return relaxationAvg;
    }

    public void setRelaxationAvg(float relaxationAvg) {
        this.relaxationAvg = relaxationAvg;
    }

    public float getRelaxationMax() {
        return relaxationMax;
    }

    public void setRelaxationMax(float relaxationMax) {
        this.relaxationMax = relaxationMax;
    }

    public float getRelaxationMin() {
        return relaxationMin;
    }

    public void setRelaxationMin(float relaxationMin) {
        this.relaxationMin = relaxationMin;
    }

    public String getMeditationFile() {
        return meditationFile;
    }

    public void setMeditationFile(String meditationFile) {
        this.meditationFile = meditationFile;
    }

    @Override
    public String toString() {
        return "MeditationEntity{" +
                "mId=" + mId +
                ", id=" + id +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", startTime='" + startTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", heartRateAvg=" + heartRateAvg +
                ", heartRateMax=" + heartRateMax +
                ", heartRateMin=" + heartRateMin +
                ", heartRateVariabilityAvg=" + heartRateVariabilityAvg +
                ", attentionAvg=" + attentionAvg +
                ", attentionMax=" + attentionMax +
                ", attentionMin=" + attentionMin +
                ", relaxationAvg=" + relaxationAvg +
                ", relaxationMax=" + relaxationMax +
                ", relaxationMin=" + relaxationMin +
                ", meditationFile='" + meditationFile + '\'' +
                ", user=" + user +
                '}';
    }

    public boolean isFileUpload() {
        return isFileUpload;
    }

    public void setFileUpload(boolean fileUpload) {
        isFileUpload = fileUpload;
    }

    public boolean isFileGet() {
        return isFileGet;
    }

    public void setFileGet(boolean fileGet) {
        isFileGet = fileGet;
    }
}
