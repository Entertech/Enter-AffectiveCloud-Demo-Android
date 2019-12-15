package cn.entertech.flowtimezh.entity;

import java.util.List;

import cn.entertech.affectivecloudsdk.entity.RecData;

public class RecDataRecord {
    private String session_id;
    private List<RecData> rec;

    @Override
    public String toString() {
        return "RecDataRecord{" +
                "session_id='" + session_id + '\'' +
                ", recDatas=" + rec +
                '}';
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public List<RecData> getRecDatas() {
        return rec;
    }

    public void setRecDatas(List<RecData> recDatas) {
        this.rec = recDatas;
    }
}
