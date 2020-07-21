package cn.entertech.flowtimezh.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import cn.entertech.flowtimezh.app.SettingManager;
import cn.entertech.flowtimezh.database.model.StatisticsModel;
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity;
import cn.entertech.flowtimezh.utils.TimeUtils;


/**
 * Created by EnterTech on 2016/11/7.
 * tb_record仅需要添加、查询、删除接口
 */

public class StatisticsDao {
    private UserLessonRecordDao mRecordDao;
    private Dao<StatisticsModel, Integer> mRecordDaoOp;
    private DataBaseHelper mHelper;

    @SuppressWarnings("unchecked")
    public StatisticsDao(Context context) {
        try {
            mHelper = DataBaseHelper.getHelper(context);
            mRecordDaoOp = mHelper.getDao(StatisticsModel.class);
            mRecordDao = new UserLessonRecordDao(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(StatisticsModel statisticsModel) {
        try {
            List<StatisticsModel> list = mRecordDaoOp.queryBuilder().where().eq("id", statisticsModel.getId()).query();
            if (list == null || list.size() == 0) {
                mRecordDaoOp.create(statisticsModel);
            } else {
                statisticsModel.setmId(list.get(0).getmId());
                mRecordDaoOp.update(statisticsModel);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }


    public List<StatisticsModel> listAll(int userId) {
        try {
            List<StatisticsModel> statisticsModels = mRecordDaoOp.queryBuilder().where().eq("user", userId).query();
            return statisticsModels;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTotalMins() {
        List<UserLessonEntity> totalRecords = mRecordDao.listAll(SettingManager.getInstance().getUserId());
        long minSum = 0;
        for (int i = 0; i < totalRecords.size(); i++) {
            long duration = TimeUtils.getStringToDate(totalRecords.get(i).getFinishTime(),
                    "yyyy-MM-dd HH:mm:ss")
                    - TimeUtils.getStringToDate(totalRecords.get(i).getStartTime(),
                    "yyyy-MM-dd HH:mm:ss");

            minSum = minSum + duration;
        }
        return TimeUtils.timeStampToMin(minSum);
    }

    public int getTotalLessons() {
        List<UserLessonEntity> totalRecords = mRecordDao.listAll(SettingManager.getInstance().getUserId());
        return totalRecords.size();
    }
}
