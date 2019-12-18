package cn.entertech.affectiveclouddemo.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_purchase")
public class PurchaseMode {

    /**
     * subscription : android_1month_subscription
     * last_renew : 2019-04-26T08:59:12Z
     * last_renew_ms : 1556269152
     * platform : android
     */

    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "subscription")
    private String subscription;
    @DatabaseField(columnName = "expire_time")
    private String expireTime;
    @DatabaseField(columnName = "expire_time_ms")
    private long expireTimeMs;
    @DatabaseField(columnName = "platform")
    private String platform;
    @DatabaseField(columnName = "auto_renewing")
    private boolean autoRenewing;
    @DatabaseField(columnName = "user_id", unique = true)
    private int userId;

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public long getExpireTimeMs() {
        return expireTimeMs;
    }

    public void setExpireTimeMs(long expireTimeMs) {
        this.expireTimeMs = expireTimeMs;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public boolean isAutoRenewing() {
        return autoRenewing;
    }

    public void setAutoRenewing(boolean autoRenewing) {
        this.autoRenewing = autoRenewing;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
