package cn.entertech.flowtimezh.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_statistics")
public class StatisticsModel {
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "active_days")
    private String activeDays;
    @DatabaseField(columnName = "created_at")
    private String createdAt;
    @DatabaseField(columnName = "current_streak")
    private int currentStreak;
    @DatabaseField(columnName = "id", unique = true)
    private int id;
    @DatabaseField(columnName = "longest_streak")
    private int longestStreak;
    @DatabaseField(columnName = "total_days")
    private int totalDays;
    @DatabaseField(columnName = "total_lessons")
    private int totalLessons;
    @DatabaseField(columnName = "total_time")
    private int totalTime;
    @DatabaseField(columnName = "update_at")
    private String updatedAt;
    @DatabaseField(columnName = "user")
    private int user;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(String activeDays) {
        this.activeDays = activeDays;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
