package cn.entertech.flowtimezh.mvp.model;

public class AuthSocialEntity {

    /**
     * access_token : VVrGN435nJLIhro76O4U9oyiIXS17MWrO8i5PHTP
     * refresh_token : TV4AHjKFtDX9tIsrlXStqC5mRLCKY3ARaAUO84ZR
     * expires_in : 86400
     */

    private String access_token;
    private String refresh_token;
    private int expires_in;
    private int uid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
