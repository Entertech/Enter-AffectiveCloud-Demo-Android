package cn.entertech.flowtimezh.mvp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;
@DatabaseTable(tableName = "tb_author")
public class AuthorEntity {
    /**
     * id : 3
     * name : Anna Morgan
     * description : It's so clear, and very practical, and I and feel this will change my life. But I can't give 5 stars, because though it's so wonderful, and I know will be extremely useful, I cannot ask questions or receive answers of either Ani la or senior students, or respond, so it is not a full and proper course in that respect.
     * image : https://flowtime-test.s3.amazonaws.com/authors/Anna%20Morgan/Rectangle_Copy_11.png?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&amp;Signature=IGJjR%2BL4QVM1ccQqJaT8OIaq5bs%3D&amp;Expires=1554951949
     * courses : [3]
     */

    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "author_id",unique = true)
    private int id;
    private String name;
    private String description;
    @DatabaseField(columnName = "author_image")
    private String image;
    private List<Integer> courses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Integer> getCourses() {
        return courses;
    }

    public void setCourses(List<Integer> courses) {
        this.courses = courses;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }
}
