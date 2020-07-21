package cn.entertech.flowtimezh.mvp.model;

public class LessonListEntity {


    /**
     * id : 1
     * course : 2
     * order_in_course : 1
     * name : lesson 1
     * duration : 900
     * file : https://flowtime-test.s3.amazonaws.com/courses/Improve%20productivity/lessons/1/ambient_music_1.mp3?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&amp;Signature=Ca%2B3Xktd4RItsTgPr27zBvRLd0M%3D&amp;Expires=1554886729
     * is_free : false
     */

    private int id;
    private int course;
    private int order_in_course;
    private String name;
    private int duration;
    private String file;
    private boolean is_free;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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
}
