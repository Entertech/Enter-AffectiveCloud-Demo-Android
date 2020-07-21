package cn.entertech.flowtimezh.mvp.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

public class PurchaseEntity {
    public int code;
    public PurchaseData data;

    @DatabaseTable(tableName = "tb_purchase")
    public class PurchaseData{
        /**
         * subscription : android_1month_subscription
         * last_renew : 2019-04-26T08:59:12Z
         * last_renew_ms : 1556269152
         * platform : android
         */
        @DatabaseField(columnName = "user_id")
        private int user;
        @DatabaseField(columnName = "subscription", unique = true)
        private String subscription;
        @DatabaseField(columnName = "expire_time")
        @SerializedName("last_renew")
        private String lastRenew;
        @DatabaseField(columnName = "expire_time_ms")
        @SerializedName("last_renew_ms")
        private long lastRenewMs;
        @DatabaseField(columnName = "platform")
        private String platform;
        @SerializedName("auto_renewing")
        private boolean autoRenewing;

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

        public String getLastRenew() {
            return lastRenew;
        }

        public void setLastRenew(String lastRenew) {
            this.lastRenew = lastRenew;
        }

        public long getLastRenewMs() {
            return lastRenewMs;
        }

        public boolean isInTime() {
            return (lastRenewMs + 8 * 3600) > System.currentTimeMillis();
        }

        public void setLastRenewMs(long lastRenewMs) {
            this.lastRenewMs = lastRenewMs;
        }

        public int getUser() {
            return user;
        }

        public void setUser(int user) {
            this.user = user;
        }

        public boolean isAutoRenewing() {
            return autoRenewing;
        }

        public void setAutoRenewing(boolean autoRenewing) {
            this.autoRenewing = autoRenewing;
        }

        @Override
        public String toString() {
            return "PurchaseData{" +
                    "user=" + user +
                    ", subscription='" + subscription + '\'' +
                    ", lastRenew='" + lastRenew + '\'' +
                    ", lastRenewMs=" + lastRenewMs +
                    ", platform='" + platform + '\'' +
                    ", autoRenewing=" + autoRenewing +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PurchaseEntity{" +
                "code=" + code +
                ", purchaseData=" + data +
                '}';
    }
}
