package cn.entertech.flowtimezh.mvp.model;

import java.util.List;

public class LessonEntity {

    /**
     * id : 3
     * order_in_course : 2
     * name : From Discomfort to Ease
     * duration : 00:12:38
     * file : https://flowtime-test.s3.amazonaws.com/lessons/From%2520Discomfort%2520to%2520Ease/moving-Between-Ease-Discomfort.ogg/db2591dc-8ceb-11e9-9c54-0242ac120005?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&Signature=ANqdvQ8HAhS8bZgyCimDSOwwbY0%3D&Expires=1560850101
     * is_free : false
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
