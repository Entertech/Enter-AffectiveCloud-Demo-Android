package cn.entertech.flowtimezh.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import cn.entertech.flowtimezh.database.model.ExperimentDimModel;
import cn.entertech.flowtimezh.database.model.ExperimentModel;


/**
 * Created by EnterTech on 2016/11/7.
 * tb_record仅需要添加、查询、删除接口
 */

public class ExperimentDimDao {
    private Dao<ExperimentDimModel, Integer> mRecordDaoOp;
    private DataBaseHelper mHelper;

    @SuppressWarnings("unchecked")
    public ExperimentDimDao(Context context) {
        try {
            mHelper = DataBaseHelper.getHelper(context);
            mRecordDaoOp = mHelper.getDao(ExperimentDimModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(ExperimentDimModel experimentDimModel) {
        try {
            List<ExperimentDimModel> list = mRecordDaoOp.queryBuilder().where().eq("dim_id", experimentDimModel.getId()).query();
            if (list == null || list.size() == 0) {
                mRecordDaoOp.create(experimentDimModel);
            } else {
                experimentDimModel.setmId(list.get(0).getmId());
                mRecordDaoOp.update(experimentDimModel);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }


    public List<ExperimentDimModel> listAll() {
        try {
            List<ExperimentDimModel> experimentDimModels = mRecordDaoOp.queryBuilder().query();
            return experimentDimModels;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ExperimentDimModel> findDimByTagId(int tagId) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<ExperimentDimModel> dims = mRecordDaoOp.queryBuilder().where().eq("tag_id", tagId).query();
            return dims;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExperimentDimModel findDimById(int tagId, int id) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<ExperimentDimModel> experiments = mRecordDaoOp.queryBuilder().where().eq("tag_id", tagId).eq("dim_id", id).query();
            if (!experiments.isEmpty()) {
                return experiments.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
