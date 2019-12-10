package cn.entertech.flowtimezh.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_meditation_labels")
public class MeditationLabelsModel {
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "meditation_id")
    private long meditationId;
    @DatabaseField(columnName = "label_id", unique = true)
    private long id;
    @DatabaseField(columnName = "label_start_time")
    private long startTime;
    @DatabaseField(columnName = "label_start_end")
    private long endTime;
    @DatabaseField(columnName = "label_dim_ids")
    private String dimIds;
    @DatabaseField(columnName = "meditation_start_time")
    private long meditationStartTime;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDimIds() {
        return dimIds;
    }

    public void setDimIds(String dimIds) {
        this.dimIds = dimIds;
    }

    public long getMeditationId() {
        return meditationId;
    }

    public void setMeditationId(long meditationId) {
        this.meditationId = meditationId;
    }

    public long getMeditationStartTime() {
        return meditationStartTime;
    }

    public void setMeditationStartTime(long meditationStartTime) {
        this.meditationStartTime = meditationStartTime;
    }
}
