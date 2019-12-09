package cn.entertech.flowtimezh.database;

import android.content.Context;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

import cn.entertech.flowtimezh.entity.UserLessonEntity;

/**
 * Created by EnterTech on 2016/11/7.
 * tb_record仅需要添加、查询、删除接口
 */

public class UserLessonRecordDao {
    private Dao<UserLessonEntity, Integer> mRecordDaoOp;
    private DataBaseHelper mHelper;

    @SuppressWarnings("unchecked")
    public UserLessonRecordDao(Context context) {
        try {
            mHelper = DataBaseHelper.getHelper(context);
            mRecordDaoOp = mHelper.getDao(UserLessonEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(UserLessonEntity record) {
        try {
            List<UserLessonEntity> list = mRecordDaoOp.queryBuilder().where().eq("record_id", record.getId()).query();
            if (list == null || list.size() == 0) {
                mRecordDaoOp.create(record);
            } else {
                record.setmId(list.get(0).getmId());
                mRecordDaoOp.update(record);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }


    public List<UserLessonEntity> listAll(int userId) {
        try {
            List<UserLessonEntity> records = mRecordDaoOp.queryBuilder().orderBy("start_time",false).where().eq("is_sample", false).and().eq("user",userId).query();
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<UserLessonEntity> listAllSampleData() {
        try {
            List<UserLessonEntity> records = mRecordDaoOp.queryBuilder().where().eq("is_sample", true).query();
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public UserLessonEntity findRecordById(int userId,long recordId) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<UserLessonEntity> course = mRecordDaoOp.queryBuilder().where().eq("record_id", recordId).and().eq("user",userId).query();
            if (!course.isEmpty()) {
                return course.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public UserLessonEntity findRecordByMeditationId(int userId,long meditationId) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<UserLessonEntity> records = mRecordDaoOp.queryBuilder().where().eq("meditation_id", meditationId).and().eq("user",userId).query();
            if (!records.isEmpty()) {
                return records.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateMeditationId(long recordId,long meditationId) {
        try {
            UpdateBuilder<UserLessonEntity, Integer> updateBuilder = mRecordDaoOp.updateBuilder();
            updateBuilder.updateColumnValue("meditation_id", meditationId).where().eq("record_id", recordId);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateRecordId(int userId, String startTime, long recordId) {
        try {
            UpdateBuilder<UserLessonEntity, Integer> updateBuilder = mRecordDaoOp.updateBuilder();
            updateBuilder.updateColumnValue("record_id", recordId).where().eq("start_time", startTime).and().eq("user",userId);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
