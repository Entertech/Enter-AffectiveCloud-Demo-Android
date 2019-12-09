package cn.entertech.flowtimezh.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_experiment")
public class ExperimentModel {
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "experiment_id", unique = true)
    private int id;
    @DatabaseField(columnName = "experiment_name_cn")
    private String nameCn;
    @DatabaseField(columnName = "experiment_name_en")
    private String nameEn;
    @DatabaseField(columnName = "experiment_desc")
    private String desc;
    @DatabaseField(columnName = "experiment_create_time")
    private String createTime;
    @DatabaseField(columnName = "experiment_modify_time")
    private String modifyTime;
    @DatabaseField(columnName = "experiment_app")
    private int app;
    @DatabaseField(columnName = "experiment_is_selected")
    private boolean isSelected;

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

    public int getApp() {
        return app;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
