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
    private String id;
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
    @DatabaseField(columnName = "is_selected")
    private boolean isSelected;
    @DatabaseField(columnName = "dim_value")
    private String value;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
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

    @Override
    public int getItemType() {
        return LabelsAdapter.TYPE_LEVEL_1;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
