package cn.entertech.affectiveclouddemo.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_courses")
public class CourseModel {
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "course_id", unique = true)
    private int courseId;
    @DatabaseField(columnName = "course_name")
    private String courseName;
    @DatabaseField(columnName = "course_lesson_count")
    private int lessonCount;
    @DatabaseField(columnName = "course_description")
    private String courseDescription;
    @DatabaseField(columnName = "course_is_free")
    private boolean isFree;
    @DatabaseField(columnName = "course_source_image")
    private String courseSourceImage;
//    @DatabaseField(columnName = "course_top_image")
//    private String courseTopImage;
//    @DatabaseField(columnName = "course_center_large_image")
//    private String courseCenterLargeImage;
//    @DatabaseField(columnName = "course_center_small_image")
//    private String courseCenterSmallImage;
//    @DatabaseField(columnName = "course_bottom_image")
//    private String courseBottomImage;
    @DatabaseField(columnName = "course_author_id")
    private int authorId;
    @DatabaseField(columnName = "course_author_source_image")
    private String authorSourceImage;
    @DatabaseField(columnName = "course_detail_color")
    private String courseDetailColor;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseSourceImage() {
        return courseSourceImage;
    }

    public void setCourseSourceImage(String courseSourceImage) {
        this.courseSourceImage = courseSourceImage;
    }

//    public Bitmap getCourseTopImage() {
//        return BitmapUtils.string2Bitmap(courseTopImage);
//    }
//
//    public void setCourseTopImage(Bitmap courseTopImage) {
//        this.courseTopImage = BitmapUtils.bitmap2String(courseTopImage);
//    }
//
//    public Bitmap getCourseCenterLargeImage() {
//        return BitmapUtils.string2Bitmap(courseCenterLargeImage);
//    }
//
//    public void setCourseCenterLargeImage(Bitmap courseCenterLargeImage) {
//        this.courseCenterLargeImage = BitmapUtils.bitmap2String(courseCenterLargeImage);
//    }
//
//    public Bitmap getCourseCenterSmallImage() {
//        return BitmapUtils.string2Bitmap(courseCenterSmallImage);
//    }
//
//    public void setCourseCenterSmallImage(Bitmap courseCenterSmallImage) {
//        this.courseCenterSmallImage = BitmapUtils.bitmap2String(courseCenterSmallImage);
//    }
//
//    public Bitmap getCourseBottomImage() {
//        return BitmapUtils.string2Bitmap(courseBottomImage);
//    }
//
//    public void setCourseBottomImage(Bitmap courseBottomImage) {
//        this.courseBottomImage = BitmapUtils.bitmap2String(courseBottomImage);
//    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorSourceImage() {
        return authorSourceImage;
    }

    public void setAuthorSourceImage(String authorSourceImage) {
        this.authorSourceImage = authorSourceImage;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

//    public int getCollectionId() {
//        return collectionId;
//    }
//
//    public void setCollectionId(int collectionId) {
//        this.collectionId = collectionId;
//    }

    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    @Override
    public String toString() {
        return "CourseModel{" +
                "mId=" + mId +
                ", courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", lessonCount=" + lessonCount +
                ", courseDescription='" + courseDescription + '\'' +
                ", isFree=" + isFree +
                ", courseSourceImage='" + courseSourceImage + '\'' +
                ", authorId=" + authorId +
                ", authorSourceImage='" + authorSourceImage + '\'' +
                '}';
    }

    public String getCourseDetailColor() {
        return courseDetailColor;
    }

    public void setCourseDetailColor(String courseDetailColor) {
        this.courseDetailColor = courseDetailColor;
    }
}
