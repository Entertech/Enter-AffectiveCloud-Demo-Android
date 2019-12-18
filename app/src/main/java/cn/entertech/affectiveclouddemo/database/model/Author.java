package cn.entertech.affectiveclouddemo.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "tb_author")
public class Author {
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "author_id",unique = true)
    private int id;
    @DatabaseField(columnName = "author_image")
    private String image;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
