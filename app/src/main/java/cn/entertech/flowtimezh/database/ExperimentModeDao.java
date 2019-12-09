package cn.entertech.flowtimezh.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import cn.entertech.flowtimezh.database.model.ExperimentDimModel;
import cn.entertech.flowtimezh.database.model.ExperimentModeModel;


/**
 * Created by EnterTech on 2016/11/7.
 * tb_record仅需要添加、查询、删除接口
 */

public class ExperimentModeDao {
    private Dao<ExperimentModeModel, Integer> mRecordDaoOp;
    private DataBaseHelper mHelper;

    @SuppressWarnings("unchecked")
    public ExperimentModeDao(Context context) {
        try {
            mHelper = DataBaseHelper.getHelper(context);
            mRecordDaoOp = mHelper.getDao(ExperimentModeModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(ExperimentModeModel experimentModeModel) {
        try {
            List<ExperimentModeModel> list = mRecordDaoOp.queryBuilder().where().eq("mode_id", experimentModeModel.getId()).query();
            if (list == null || list.size() == 0) {
                mRecordDaoOp.create(experimentModeModel);
            } else {
                experimentModeModel.setmId(list.get(0).getmId());
                mRecordDaoOp.update(experimentModeModel);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }


    public List<ExperimentModeModel> listAll() {
        try {
            List<ExperimentModeModel> experimentModeModels = mRecordDaoOp.queryBuilder().query();
            return experimentModeModels;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ExperimentModeModel> findModeByExperimentId(int experimentId) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<ExperimentModeModel> dims = mRecordDaoOp.queryBuilder().where().eq("experiment_id", experimentId).query();
            return dims;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExperimentModeModel findModeById(int experimentId, int id) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<ExperimentModeModel> modes = mRecordDaoOp.queryBuilder().where().eq("experiment_id", experimentId).eq("mode_id", id).query();
            if (!modes.isEmpty()) {
                return modes.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
