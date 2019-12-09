package cn.entertech.flowtimezh.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import cn.entertech.flowtimezh.ui.adapter.LabelsAdapter;

public class LabelsEntity {


    /**
     * id : 2
     * mode : [{"id":2,"name_cn":"愉悦度","name_en":"pleasure","desc":"愉悦度模型","gmt_create":"2019-11-27T15:56:00","gmt_modify":"2019-12-05T15:23:21.403974"}]
     * tag : [{"id":2,"dim":[{"id":2,"name_cn":"很不喜欢","name_en":"very dislike","value":"0","desc":"愉悦度评价（1/5）","gmt_create":"2019-12-05T14:48:00","gmt_modify":"2019-12-05T15:01:41.161439"},{"id":3,"name_cn":"不太喜欢","name_en":"dislike","value":"0.25","desc":"愉悦度评价（2/5）","gmt_create":"2019-12-05T14:56:00","gmt_modify":"2019-12-05T15:01:48.368758"},{"id":4,"name_cn":"一般","name_en":"neural","value":"0.5","desc":"愉悦度评价（3/5）","gmt_create":"2019-12-05T14:56:00","gmt_modify":"2019-12-05T15:00:34.512770"},{"id":5,"name_cn":"喜欢","name_en":"like","value":"0.75","desc":"愉悦度评价（4/5）","gmt_create":"2019-12-05T15:00:00","gmt_modify":"2019-12-05T15:01:07.008591"},{"id":6,"name_cn":"很喜欢","name_en":"very like","value":"1","desc":"愉悦度评价（5/5）","gmt_create":"2019-12-05T15:01:00","gmt_modify":"2019-12-05T15:01:24.825365"}],"name_cn":"愉悦度（喜好度）","name_en":"pleasure","desc":"被试者对实验品的喜好程度，反映愉悦程度（李克特5级量表）","gmt_create":"2019-12-05T15:11:00","gmt_modify":"2019-12-05T15:19:36.459615"},{"id":3,"dim":[{"id":7,"name_cn":"1号","name_en":"No.1","value":"1","desc":"香精型号（1/8）","gmt_create":"2019-12-05T15:02:00","gmt_modify":"2019-12-05T15:02:57.763853"},{"id":8,"name_cn":"2号","name_en":"No.2","value":"2","desc":"香精型号（2/8）","gmt_create":"2019-12-05T15:02:00","gmt_modify":"2019-12-05T15:03:15.925112"},{"id":9,"name_cn":"3号","name_en":"No.3","value":"3","desc":"香精型号（3/8）","gmt_create":"2019-12-05T15:03:00","gmt_modify":"2019-12-05T15:03:33.778138"},{"id":10,"name_cn":"4号","name_en":"No.4","value":"4","desc":"香精型号（4/8）","gmt_create":"2019-12-05T15:03:00","gmt_modify":"2019-12-05T15:03:51.873306"},{"id":11,"name_cn":"5号","name_en":"No.5","value":"5","desc":"香精型号（5/8）","gmt_create":"2019-12-05T15:03:00","gmt_modify":"2019-12-05T15:04:13.865944"},{"id":12,"name_cn":"6号","name_en":"No.6","value":"6","desc":"香精型号（6/8）","gmt_create":"2019-12-05T15:04:00","gmt_modify":"2019-12-05T15:04:29.655057"},{"id":13,"name_cn":"7号","name_en":"No.7","value":"7","desc":"香精型号（7/8）","gmt_create":"2019-12-05T15:04:00","gmt_modify":"2019-12-05T15:04:48.215225"},{"id":14,"name_cn":"8号","name_en":"No.8","value":"8","desc":"香精型号（8/8）","gmt_create":"2019-12-05T15:04:00","gmt_modify":"2019-12-05T15:05:12.362785"}],"name_cn":"香精型号","name_en":"aroma_num","desc":"被试者所闻的香精类型编号【脑电波选中国香】","gmt_create":"2019-12-05T15:15:00","gmt_modify":"2019-12-05T15:20:05.490405"}]
     * name_cn : 香精愉悦度测试
     * name_en : aroma_pleasure_test
     * desc : 测试被试者对不同型号香精的喜好程度，采集愉悦度数据【脑电波选中国香】
     * gmt_create : 2019-12-05T15:23:00
     * gmt_modify : 2019-12-05T15:28:04.878493
     * app : 1
     */

    private int id;
    private String name_cn;
    private String name_en;
    private String desc;
    private String gmt_create;
    private String gmt_modify;
    private int app;
    private List<ModeBean> mode;
    private List<TagBean> tag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_cn() {
        return name_cn;
    }

    public void setName_cn(String name_cn) {
        this.name_cn = name_cn;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(String gmt_create) {
        this.gmt_create = gmt_create;
    }

    public String getGmt_modify() {
        return gmt_modify;
    }

    public void setGmt_modify(String gmt_modify) {
        this.gmt_modify = gmt_modify;
    }

    public int getApp() {
        return app;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public List<ModeBean> getMode() {
        return mode;
    }

    public void setMode(List<ModeBean> mode) {
        this.mode = mode;
    }

    public List<TagBean> getTag() {
        return tag;
    }

    public void setTag(List<TagBean> tag) {
        this.tag = tag;
    }

    public static class ModeBean {
        /**
         * id : 2
         * name_cn : 愉悦度
         * name_en : pleasure
         * desc : 愉悦度模型
         * gmt_create : 2019-11-27T15:56:00
         * gmt_modify : 2019-12-05T15:23:21.403974
         */

        private int id;
        private String name_cn;
        private String name_en;
        private String desc;
        private String gmt_create;
        private String gmt_modify;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName_cn() {
            return name_cn;
        }

        public void setName_cn(String name_cn) {
            this.name_cn = name_cn;
        }

        public String getName_en() {
            return name_en;
        }

        public void setName_en(String name_en) {
            this.name_en = name_en;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getGmt_create() {
            return gmt_create;
        }

        public void setGmt_create(String gmt_create) {
            this.gmt_create = gmt_create;
        }

        public String getGmt_modify() {
            return gmt_modify;
        }

        public void setGmt_modify(String gmt_modify) {
            this.gmt_modify = gmt_modify;
        }
    }

    public static class TagBean{
        /**
         * id : 2
         * dim : [{"id":2,"name_cn":"很不喜欢","name_en":"very dislike","value":"0","desc":"愉悦度评价（1/5）","gmt_create":"2019-12-05T14:48:00","gmt_modify":"2019-12-05T15:01:41.161439"},{"id":3,"name_cn":"不太喜欢","name_en":"dislike","value":"0.25","desc":"愉悦度评价（2/5）","gmt_create":"2019-12-05T14:56:00","gmt_modify":"2019-12-05T15:01:48.368758"},{"id":4,"name_cn":"一般","name_en":"neural","value":"0.5","desc":"愉悦度评价（3/5）","gmt_create":"2019-12-05T14:56:00","gmt_modify":"2019-12-05T15:00:34.512770"},{"id":5,"name_cn":"喜欢","name_en":"like","value":"0.75","desc":"愉悦度评价（4/5）","gmt_create":"2019-12-05T15:00:00","gmt_modify":"2019-12-05T15:01:07.008591"},{"id":6,"name_cn":"很喜欢","name_en":"very like","value":"1","desc":"愉悦度评价（5/5）","gmt_create":"2019-12-05T15:01:00","gmt_modify":"2019-12-05T15:01:24.825365"}]
         * name_cn : 愉悦度（喜好度）
         * name_en : pleasure
         * desc : 被试者对实验品的喜好程度，反映愉悦程度（李克特5级量表）
         * gmt_create : 2019-12-05T15:11:00
         * gmt_modify : 2019-12-05T15:19:36.459615
         */

        private int id;
        private String name_cn;
        private String name_en;
        private String desc;
        private String gmt_create;
        private String gmt_modify;
        private List<DimBean> dim;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName_cn() {
            return name_cn;
        }

        public void setName_cn(String name_cn) {
            this.name_cn = name_cn;
        }

        public String getName_en() {
            return name_en;
        }

        public void setName_en(String name_en) {
            this.name_en = name_en;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getGmt_create() {
            return gmt_create;
        }

        public void setGmt_create(String gmt_create) {
            this.gmt_create = gmt_create;
        }

        public String getGmt_modify() {
            return gmt_modify;
        }

        public void setGmt_modify(String gmt_modify) {
            this.gmt_modify = gmt_modify;
        }

        public List<DimBean> getDim() {
            return dim;
        }

        public void setDim(List<DimBean> dim) {
            this.dim = dim;
        }


        public static class DimBean {
            /**
             * id : 2
             * name_cn : 很不喜欢
             * name_en : very dislike
             * value : 0
             * desc : 愉悦度评价（1/5）
             * gmt_create : 2019-12-05T14:48:00
             * gmt_modify : 2019-12-05T15:01:41.161439
             */

            private int id;
            private String name_cn;
            private String name_en;
            private String value;
            private String desc;
            private String gmt_create;
            private String gmt_modify;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName_cn() {
                return name_cn;
            }

            public void setName_cn(String name_cn) {
                this.name_cn = name_cn;
            }

            public String getName_en() {
                return name_en;
            }

            public void setName_en(String name_en) {
                this.name_en = name_en;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getGmt_create() {
                return gmt_create;
            }

            public void setGmt_create(String gmt_create) {
                this.gmt_create = gmt_create;
            }

            public String getGmt_modify() {
                return gmt_modify;
            }

            public void setGmt_modify(String gmt_modify) {
                this.gmt_modify = gmt_modify;
            }

        }
    }
}
