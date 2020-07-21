package cn.entertech.flowtimezh.mvp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

public class UserCourseEntity {

    /**
     * count : 2
     * next : https://test.myflowtime.com/user/courses/?limit=1&amp;offset=1
     * previous : null
     * results : [{"id":3,"name":"Pain Healing","description":"Meditation brings many benefits: It refreshes us, helps us settle into what\u2019s happening now, makes us wiser and gentler, helps us cope in a world that overloads us with information and communication, and more. But if you\u2019re still looking for a business case to justify spending time meditating, try this one: Meditation makes you more productive.","image":"https://flowtime-test.s3.amazonaws.com/courses/Pain%20Healing/7.jpg?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&amp;Signature=t%2BEa%2FXrC%2BZ%2F63s1GvyPnYCtK0lM%3D&amp;Expires=1555055210","authors":[{"id":3,"name":"Anna Morgan"}],"tags":[],"is_free":true,"lesson_count":5,"lessons":[3,4,5,6,7],"last_learned":"2019-04-11T05:39:50.887417Z","learned_count":3,"learned_lessons":[3,4,5]}]
     */

    private int count;
    private String next;
    private String previous;
    private List<ResultsBean> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    @DatabaseTable(tableName = "tb_user_courses")
    public static class ResultsBean {
        /**
         * id : 3
         * name : Pain Healing
         * description : Meditation brings many benefits: It refreshes us, helps us settle into what’s happening now, makes us wiser and gentler, helps us cope in a world that overloads us with information and communication, and more. But if you’re still looking for a business case to justify spending time meditating, try this one: Meditation makes you more productive.
         * image : https://flowtime-test.s3.amazonaws.com/courses/Pain%20Healing/7.jpg?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&amp;Signature=t%2BEa%2FXrC%2BZ%2F63s1GvyPnYCtK0lM%3D&amp;Expires=1555055210
         * authors : [{"id":3,"name":"Anna Morgan"}]
         * tags : []
         * is_free : true
         * lesson_count : 5
         * lessons : [3,4,5,6,7]
         * last_learned : 2019-04-11T05:39:50.887417Z
         * learned_count : 3
         * learned_lessons : [3,4,5]
         */

        @DatabaseField(generatedId = true)
        private int mId;
        @DatabaseField(columnName = "learned_course_id",unique = true)
        private int id;
        @DatabaseField(columnName = "course_name")
        private String name;
        @DatabaseField(columnName = "course_description")
        private String description;
        @DatabaseField(columnName = "course_image")
        private String image;
        @DatabaseField(columnName = "course_is_free")
        private boolean is_free;
        @DatabaseField(columnName = "course_lesson_count")
        private int lesson_count;
        @DatabaseField(columnName = "course_last_learned")
        private String last_learned;
        @DatabaseField(columnName = "course_learned_count")
        private int learned_count;
        @DatabaseField(columnName = "course_learned_lessons")
        private String learnedLessons;
        @DatabaseField(columnName = "course_author_id")
        private int authorId;
        @DatabaseField(columnName = "user_id")
        private int userId;
        private List<AuthorsBean> authors;
        private List<?> tags;
        private List<Integer> lessons;
        private List<Integer> learned_lessons;

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

        public String getLast_learned() {
            return last_learned;
        }

        public void setLast_learned(String last_learned) {
            this.last_learned = last_learned;
        }

        public int getLearned_count() {
            return learned_count;
        }

        public void setLearned_count(int learned_count) {
            this.learned_count = learned_count;
        }

        public List<AuthorsBean> getAuthors() {
            return authors;
        }

        public void setAuthors(List<AuthorsBean> authors) {
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

        public List<Integer> getLearned_lessons() {
            return learned_lessons;
        }

        public void setLearned_lessons(List<Integer> learned_lessons) {
            this.learned_lessons = learned_lessons;
        }

        public int getmId() {
            return mId;
        }

        public void setmId(int mId) {
            this.mId = mId;
        }

        public String getLearnedLessons() {
            return learnedLessons;
        }

        public void setLearnedLessons(String learnedLessons) {
            this.learnedLessons = learnedLessons;
        }

        public int getAuthorId() {
            return authorId;
        }

        public void setAuthorId(int authorId) {
            this.authorId = authorId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public static class AuthorsBean {
            /**
             * id : 3
             * name : Anna Morgan
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
}
