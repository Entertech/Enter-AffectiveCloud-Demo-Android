package cn.entertech.flowtimezh.mvp.model;

import java.util.List;
public class CollectionEntity {

    /**
     * count : 4
     * next : https://test.myflowtime.com/collections/?limit=1&amp;offset=2
     * previous : https://test.myflowtime.com/collections/?limit=1
     * results : [{"id":2,"name":"Learn Meditation with Certification","description":"Concentration meditation involves focusing on a single point. This could entail following the breath, repeating a single word or mantra, staring at a candle flame, listening to a repetitive gong, or counting beads on a mala. Since focusing the mind is challenging, a beginner might meditate for only a few minutes and then work up to longer durations.In this form of meditation, you simply refocus your awareness on the chosen object of attention each time you notice your mind wandering.","courses":[3,4,5,6],"image":null,"theme_color":"#637F72,#637F72,#E0FCEE"}]
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
         * id : 2
         * name : Learn Meditation with Certification
         * description : Concentration meditation involves focusing on a single point. This could entail following the breath, repeating a single word or mantra, staring at a candle flame, listening to a repetitive gong, or counting beads on a mala. Since focusing the mind is challenging, a beginner might meditate for only a few minutes and then work up to longer durations.In this form of meditation, you simply refocus your awareness on the chosen object of attention each time you notice your mind wandering.
         * courses : [3,4,5,6]
         * image : null
         * theme_color : #637F72,#637F72,#E0FCEE
         */

        private int id;
        private String name;
        private String description;
        private String image;
        private String theme_color;
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

        public String getTheme_color() {
            return theme_color;
        }

        public void setTheme_color(String theme_color) {
            this.theme_color = theme_color;
        }

        public List<Integer> getCourses() {
            return courses;
        }

        public void setCourses(List<Integer> courses) {
            this.courses = courses;
        }
    }
}
