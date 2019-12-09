package cn.entertech.flowtimezh.database.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import cn.entertech.flowtimezh.entity.LabelsEntity;
import cn.entertech.flowtimezh.ui.adapter.LabelsAdapter;

@DatabaseTable(tableName = "tb_experiment_tag")
public class ExperimentTagModel extends AbstractExpandableItem<ExperimentDimModel> implements MultiItemEntity {
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "tag_id", unique = true)
    private int id;
    @DatabaseField(columnName = "tag_name_cn")
    private String nameCn;
    @DatabaseField(columnName = "tag_name_en")
    private String nameEn;
    @DatabaseField(columnName = "tag_desc")
    private String desc;
    @DatabaseField(columnName = "tag_create_time")
    private String createTime;
    @DatabaseField(columnName = "tag_modify_time")
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

    @Override
    public int getItemType() {
        return LabelsAdapter.TYPE_LEVEL_0;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
