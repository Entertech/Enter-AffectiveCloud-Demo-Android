package cn.entertech.flowtimezh.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import cn.entertech.flowtimezh.database.model.MeditationLabelsModel;


/**
 * Created by EnterTech on 2016/11/7.
 * tb_record仅需要添加、查询、删除接口
 */

public class MeditationLabelsDao {
    private Dao<MeditationLabelsModel, Integer> mRecordDaoOp;
    private DataBaseHelper mHelper;

    @SuppressWarnings("unchecked")
    public MeditationLabelsDao(Context context) {
        try {
            mHelper = DataBaseHelper.getHelper(context);
            mRecordDaoOp = mHelper.getDao(MeditationLabelsModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(MeditationLabelsModel meditationLabelsModel) {
        try {
            List<MeditationLabelsModel> list = mRecordDaoOp.queryBuilder().where().eq("label_id", meditationLabelsModel.getId()).query();
            if (list == null || list.size() == 0) {
                mRecordDaoOp.create(meditationLabelsModel);
            } else {
                meditationLabelsModel.setmId(list.get(0).getmId());
                mRecordDaoOp.update(meditationLabelsModel);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }


    public List<MeditationLabelsModel> listAll() {
        try {
            List<MeditationLabelsModel> list = mRecordDaoOp.queryBuilder().query();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MeditationLabelsModel findMeditationLabelById(long id) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<MeditationLabelsModel> experiments = mRecordDaoOp.queryBuilder().where().eq("label_id", id).query();
            if (!experiments.isEmpty()) {
                return experiments.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<MeditationLabelsModel> findByMeditationId(long meditationId) {
        try {
            List<MeditationLabelsModel> list = mRecordDaoOp.queryBuilder().where().eq("meditation_id", meditationId).query();
            if (list != null && list.size() != 0) {
                return list;
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteLabel(MeditationLabelsModel label){
        try {
            mRecordDaoOp.delete(label);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
