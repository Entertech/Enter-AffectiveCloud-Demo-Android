package cn.entertech.flowtimezh.mvp.model;

import android.graphics.Bitmap;

import java.util.List;

import cn.entertech.flowtimezh.utils.BitmapUtils;

public class CourseEntity {


    /**
     * count : 5
     * next : https://test.myflowtime.com/collections/1/courses/?limit=1&amp;offset=2
     * previous : https://test.myflowtime.com/collections/1/courses/?limit=1
     * results : [{"id":3,"name":"Pain Healing","description":"Meditation brings many benefits: It refreshes us, helps us settle into what\u2019s happening now, makes us wiser and gentler, helps us cope in a world that overloads us with information and communication, and more. But if you\u2019re still looking for a business case to justify spending time meditating, try this one: Meditation makes you more productive.","image":"https://flowtime-test.s3.amazonaws.com/courses/Pain%20Healing/7.jpg?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&amp;Signature=jzmNsN7Jih%2FVgoHppABC%2BLR2cbI%3D&amp;Expires=1554890696","authors":[{"id":3,"name":"Anna Morgan"}],"tags":[],"lesson_count":5,"lessons":[3,4,5,6,7]}]
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

    public static class ResultsBean {
        /**
         * id : 3
         * name : Pain Healing
         * description : Meditation brings many benefits: It refreshes us, helps us settle into what’s happening now, makes us wiser and gentler, helps us cope in a world that overloads us with information and communication, and more. But if you’re still looking for a business case to justify spending time meditating, try this one: Meditation makes you more productive.
         * image : https://flowtime-test.s3.amazonaws.com/courses/Pain%20Healing/7.jpg?AWSAccessKeyId=AKIAIQMK2PQNHTUTZULA&amp;Signature=jzmNsN7Jih%2FVgoHppABC%2BLR2cbI%3D&amp;Expires=1554890696
         * authors : [{"id":3,"name":"Anna Morgan"}]
         * tags : []
         * lesson_count : 5
         * lessons : [3,4,5,6,7]
         */

        private int mId;
        private int id;
        private int collectionId;
        private String name;
        private String description;
        private String image;
        private String imageTop;
        private String imageSmallCenter;
        private String imageLargeCenter;
        private String imageBottom;
        private int lesson_count;
        private int lesson_learned_count;
        private List<AuthorsBean> authors;
        private List<?> tags;
        private List<Integer> lessons;
        private boolean is_free;

        public Bitmap getCourseTopImage() {
            return BitmapUtils.string2Bitmap(imageTop);
        }

        public void setCourseTopImage(Bitmap courseTopImage) {
            this.imageTop = BitmapUtils.bitmap2String(courseTopImage);
        }

        public Bitmap getCourseCenterLargeImage() {
            return BitmapUtils.string2Bitmap(imageLargeCenter);
        }

        public void setCourseCenterLargeImage(Bitmap courseCenterLargeImage) {
            this.imageLargeCenter = BitmapUtils.bitmap2String(courseCenterLargeImage);
        }

        public Bitmap getCourseCenterSmallImage() {
            return BitmapUtils.string2Bitmap(imageSmallCenter);
        }

        public void setCourseCenterSmallImage(Bitmap courseCenterSmallImage) {
            this.imageSmallCenter = BitmapUtils.bitmap2String(courseCenterSmallImage);
        }

        public Bitmap getCourseBottomImage() {
            return BitmapUtils.string2Bitmap(imageBottom);
        }

        public void setCourseBottomImage(Bitmap courseBottomImage) {
            this.imageBottom = BitmapUtils.bitmap2String(courseBottomImage);
        }

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

        public int getLesson_count() {
            return lesson_count;
        }

        public void setLesson_count(int lesson_count) {
            this.lesson_count = lesson_count;
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

        public int getLesson_learned_count() {
            return lesson_learned_count;
        }

        public void setLesson_learned_count(int lesson_learned_count) {
            this.lesson_learned_count = lesson_learned_count;
        }

        public boolean isIs_free() {
            return is_free;
        }

        public void setIs_free(boolean is_free) {
            this.is_free = is_free;
        }

        public int getmId() {
            return mId;
        }

        public void setmId(int mId) {
            this.mId = mId;
        }

        public int getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(int collectionId) {
            this.collectionId = collectionId;
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

        @Override
        public String toString() {
            return "ResultsBean{" +
                    "mId=" + mId +
                    ", id=" + id +
                    ", collectionId=" + collectionId +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", image='" + image + '\'' +
                    ", imageTop='" + imageTop + '\'' +
                    ", imageSmallCenter='" + imageSmallCenter + '\'' +
                    ", imageLargeCenter='" + imageLargeCenter + '\'' +
                    ", imageBottom='" + imageBottom + '\'' +
                    ", lesson_count=" + lesson_count +
                    ", lesson_learned_count=" + lesson_learned_count +
                    ", authors=" + authors +
                    ", tags=" + tags +
                    ", lessons=" + lessons +
                    ", is_free=" + is_free +
                    '}';
        }
    }
}
