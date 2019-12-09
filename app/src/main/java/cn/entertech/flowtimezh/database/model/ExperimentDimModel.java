package cn.entertech.flowtimezh.database.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import cn.entertech.flowtimezh.ui.adapter.LabelsAdapter;

@DatabaseTable(tableName = "tb_experiment_dim")
public class ExperimentDimModel implements MultiItemEntity {
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "dim_id", unique = true)
    private int id;
    @DatabaseField(columnName = "dim_name_cn")
    private String nameCn;
    @DatabaseField(columnName = "dim_name_en")
    private String nameEn;
    @DatabaseField(columnName = "dim_desc")
    private String desc;
    @DatabaseField(columnName = "dim_create_time")
    private String createTime;
    @DatabaseField(columnName = "dim_modify_time")
    private String modifyTime;
    @DatabaseField(columnName = "tag_id")
    private int tagId;

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

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    @Override
    public int getItemType() {
        return LabelsAdapter.TYPE_LEVEL_1;
    }
}
