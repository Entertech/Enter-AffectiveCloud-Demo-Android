package cn.entertech.affectiveclouddemo.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "tb_records")
public class UserLessonEntity {

    /**
     * id : 1
     * lesson : {"id":2,"order_in_course":101,"name":"Sensory Elements of Pain","duration":"00:12:30","file":"https://flowtime-test.s3.amazonaws.com/lessons/Sensory%2520Elements%2520of%2520Pain/sensory-Components-Pain.ogg/5cc4249e-8cea-11e9-b11f-0242ac120005?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&Signature=lzqi5%2BwMBGBxmYsMIBKN4CUNVUk%3D&Expires=1560763109","is_free":true,"courses":[{"id":4,"name":"Pain"}]}
     * course : {"id":4,"name":"Pain","description":"The sensation of pain is a mental phenomenon. As a result, there is immense potential for each and every one of us to minimise or even eliminate the suffering typically associated with physical and emotional pain. These guided sessions are particularly useful for practicing this remarkable and powerful skill.","image":"https://flowtime-test.s3.amazonaws.com/courses/Pain/33x.png?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&Signature=NQPvC8xQI9LN81wNSn3cLYIs4bA%3D&Expires=1560763109","authors":[],"tags":[],"is_free":true,"lesson_count":15,"lessons":[2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]}
     * created_at : 2019-06-17T07:53:28.647746Z
     * updated_at : 2019-06-17T07:53:28.647791Z
     * start_time : 2019-06-17T10:18:00Z
     * finish_time : 2019-06-17T10:23:00Z
     * user : 1
     * meditation : 5
     */
    @DatabaseField(generatedId = true)
    private int mId;
    @DatabaseField(columnName = "record_id", unique = true)
    private long id;
    private LessonBean lesson;
    private CourseBean course;
    @DatabaseField(columnName = "created_at")
    @SerializedName("created_at")
    private String createdAt;

    @DatabaseField(columnName = "updated_at")
    @SerializedName("updated_at")
    private String updatedAt;

    @DatabaseField(columnName = "start_time")
    @SerializedName("start_time")
    private String startTime;

    @DatabaseField(columnName = "finish_time")
    @SerializedName("finish_time")
    private String finishTime;
    @DatabaseField(columnName = "user")
    private int user;
    @DatabaseField(columnName = "meditation_id")
    private long meditation;
    @DatabaseField(columnName = "course_id")
    private int courseId;
    @DatabaseField(columnName = "lesson_id")
    private int lessonId;
    @DatabaseField(columnName = "course_name")
    private String courseName;
    @DatabaseField(columnName = "lesson_name")
    private String lessonName;
    @DatabaseField(columnName = "course_image")
    private String courseImage;
    @DatabaseField(columnName = "is_sample")
    private boolean isSampleData;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LessonBean getLesson() {
        return lesson;
    }

    public void setLesson(LessonBean lesson) {
        this.lesson = lesson;
    }

    public CourseBean getCourse() {
        return course;
    }

    public void setCourse(CourseBean course) {
        this.course = course;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public long getMeditation() {
        return meditation;
    }

    public void setMeditation(long meditation) {
        this.meditation = meditation;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getLessonId() {
        return lessonId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public boolean isSampleData() {
        return isSampleData;
    }

    public void setSampleData(boolean sampleData) {
        isSampleData = sampleData;
    }

    public static class LessonBean {
        /**
         * id : 2
         * order_in_course : 101
         * name : Sensory Elements of Pain
         * duration : 00:12:30
         * file : https://flowtime-test.s3.amazonaws.com/lessons/Sensory%2520Elements%2520of%2520Pain/sensory-Components-Pain.ogg/5cc4249e-8cea-11e9-b11f-0242ac120005?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&Signature=lzqi5%2BwMBGBxmYsMIBKN4CUNVUk%3D&Expires=1560763109
         * is_free : true
         * courses : [{"id":4,"name":"Pain"}]
         */

        private int id;
        private int order_in_course;
        private String name;
        private String duration;
        private String file;
        private boolean is_free;
        private List<CoursesBean> courses;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getOrder_in_course() {
            return order_in_course;
        }

        public void setOrder_in_course(int order_in_course) {
            this.order_in_course = order_in_course;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public boolean isIs_free() {
            return is_free;
        }

        public void setIs_free(boolean is_free) {
            this.is_free = is_free;
        }

        public List<CoursesBean> getCourses() {
            return courses;
        }

        public void setCourses(List<CoursesBean> courses) {
            this.courses = courses;
        }

        public static class CoursesBean {
            /**
             * id : 4
             * name : Pain
             */

            private int id;
            private String name;

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
        }
    }

    public static class CourseBean {
        /**
         * id : 4
         * name : Pain
         * description : The sensation of pain is a mental phenomenon. As a result, there is immense potential for each and every one of us to minimise or even eliminate the suffering typically associated with physical and emotional pain. These guided sessions are particularly useful for practicing this remarkable and powerful skill.
         * image : https://flowtime-test.s3.amazonaws.com/courses/Pain/33x.png?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&Signature=NQPvC8xQI9LN81wNSn3cLYIs4bA%3D&Expires=1560763109
         * authors : []
         * tags : []
         * is_free : true
         * lesson_count : 15
         * lessons : [2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
         */

        private int id;
        private String name;
        private String description;
        private String image;
        private boolean is_free;
        private int lesson_count;
        private List<?> authors;
        private List<?> tags;
        private List<Integer> lessons;

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

        public boolean isIs_free() {
            return is_free;
        }

        public void setIs_free(boolean is_free) {
            this.is_free = is_free;
        }

        public int getLesson_count() {
            return lesson_count;
        }

        public void setLesson_count(int lesson_count) {
            this.lesson_count = lesson_count;
        }

        public List<?> getAuthors() {
            return authors;
        }

        public void setAuthors(List<?> authors) {
            this.authors = authors;
        }

        public List<?> getTags() {
            return tags;
        }

        public void setTags(List<?> tags) {
            this.tags = tags;
        }

        public List<Integer> getLessons() {
            return lessons;
        }

        public void setLessons(List<Integer> lessons) {
            this.lessons = lessons;
        }
    }

    @Override
    public String toString() {
        return "UserLessonEntity{" +
                "mId=" + mId +
                ", id=" + id +
                ", lesson=" + lesson +
                ", course=" + course +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", startTime='" + startTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", user=" + user +
                ", meditation=" + meditation +
                ", courseId=" + courseId +
                ", lessonId=" + lessonId +
                ", courseName='" + courseName + '\'' +
                ", lessonName='" + lessonName + '\'' +
                ", courseImage='" + courseImage + '\'' +
                '}';
    }
}
