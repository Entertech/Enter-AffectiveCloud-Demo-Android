package cn.entertech.flowtimezh.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import cn.entertech.flowtimezh.database.model.ExperimentModeModel;
import cn.entertech.flowtimezh.database.model.ExperimentTagModel;


/**
 * Created by EnterTech on 2016/11/7.
 * tb_record仅需要添加、查询、删除接口
 */

public class ExperimentTagDao {
    private Dao<ExperimentTagModel, Integer> mRecordDaoOp;
    private DataBaseHelper mHelper;

    @SuppressWarnings("unchecked")
    public ExperimentTagDao(Context context) {
        try {
            mHelper = DataBaseHelper.getHelper(context);
            mRecordDaoOp = mHelper.getDao(ExperimentTagModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(ExperimentTagModel experimentTagModel) {
        try {
            List<ExperimentTagModel> list = mRecordDaoOp.queryBuilder().where().eq("tag_id", experimentTagModel.getId()).query();
            if (list == null || list.size() == 0) {
                mRecordDaoOp.create(experimentTagModel);
            } else {
                experimentTagModel.setmId(list.get(0).getmId());
                mRecordDaoOp.update(experimentTagModel);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }


    public List<ExperimentTagModel> listAll() {
        try {
            List<ExperimentTagModel> experimentTagModels = mRecordDaoOp.queryBuilder().query();
            return experimentTagModels;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ExperimentTagModel> findTagByExperimentId(int experimentId) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<ExperimentTagModel> tags = mRecordDaoOp.queryBuilder().where().eq("experiment_id", experimentId).query();
            return tags;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExperimentTagModel findTagById(int experimentId, int id) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<ExperimentTagModel> modes = mRecordDaoOp.queryBuilder().where().eq("experiment_id", experimentId).eq("tag_id", id).query();
            if (!modes.isEmpty()) {
                return modes.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
