package cn.entertech.flowtimezh.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_experiment_mode")
public class ExperimentModeModel {
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "mode_id", unique = true)
    private int id;
    @DatabaseField(columnName = "mode_name_cn")
    private String nameCn;
    @DatabaseField(columnName = "mode_name_en")
    private String nameEn;
    @DatabaseField(columnName = "mode_desc")
    private String desc;
    @DatabaseField(columnName = "mode_create_time")
    private String createTime;
    @DatabaseField(columnName = "mode_modify_time")
    private String modifyTime;
    @DatabaseField(columnName = "experiment_id")
    private int expermentId;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getExpermentId() {
        return expermentId;
    }

    public void setExpermentId(int expermentId) {
        this.expermentId = expermentId;
    }

}
