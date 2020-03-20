package cn.entertech.flowtimezh.database;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.List;

import cn.entertech.flowtimezh.app.SettingManager;
import cn.entertech.flowtimezh.database.model.ExperimentModel;


/**
 * Created by EnterTech on 2016/11/7.
 * tb_record仅需要添加、查询、删除接口
 */

public class ExperimentDao {
    private Dao<ExperimentModel, Integer> mRecordDaoOp;
    private DataBaseHelper mHelper;

    @SuppressWarnings("unchecked")
    public ExperimentDao(Context context) {
        try {
            mHelper = DataBaseHelper.getHelper(context);
            mRecordDaoOp = mHelper.getDao(ExperimentModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(ExperimentModel experimentModel) {
        try {
            List<ExperimentModel> list = mRecordDaoOp.queryBuilder().where().eq("experiment_id", experimentModel.getId()).query();
            if (list == null || list.size() == 0) {
                mRecordDaoOp.create(experimentModel);
            } else {
                experimentModel.setmId(list.get(0).getmId());
                mRecordDaoOp.update(experimentModel);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }


    public List<ExperimentModel> listAll() {
        try {
            List<ExperimentModel> experimentModels = mRecordDaoOp.queryBuilder().where().eq("server", SettingManager.getInstance().getApiServer()).query();
            return experimentModels;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExperimentModel findExperimentById(int id) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<ExperimentModel> experiments = mRecordDaoOp.queryBuilder().where().eq("experiment_id", id).query();
            if (!experiments.isEmpty()) {
                return experiments.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExperimentModel findExperimentBySelected() {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<ExperimentModel> experiments = mRecordDaoOp.queryBuilder().where().eq("experiment_is_selected", true).query();
            if (!experiments.isEmpty()) {
                return experiments.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
