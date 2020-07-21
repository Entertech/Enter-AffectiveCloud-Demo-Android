package cn.entertech.flowtimezh.wxapi.uikit;

public class MessageEvent {
    private int messageCode;
    private String WXAcessToken;
    private String WXName;
    private String WXImage;
    private String WXUserId;

    public String getWXAcessToken() {
        return WXAcessToken;
    }

    public void setWXAcessToken(String WXAcessToken) {
        this.WXAcessToken = WXAcessToken;
    }

    public String getWXName() {
        return WXName;
    }

    public void setWXName(String WXName) {
        this.WXName = WXName;
    }

    public String getWXImage() {
        return WXImage;
    }

    public void setWXImage(String WXImage) {
        this.WXImage = WXImage;
    }

    public String getWXUserId() {
        return WXUserId;
    }

    public void setWXUserId(String WXUserId) {
        this.WXUserId = WXUserId;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }
}
