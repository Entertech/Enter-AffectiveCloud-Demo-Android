package cn.entertech.affectiveclouddemo.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

import cn.entertech.affectiveclouddemo.model.MeditationEntity;


/**
 * Created by EnterTech on 2016/11/7.
 * tb_record仅需要添加、查询、删除接口
 */

public class MeditationDao {
    private Dao<MeditationEntity, Integer> mRecordDaoOp;
    private DataBaseHelper mHelper;

    @SuppressWarnings("unchecked")
    public MeditationDao(Context context) {
        try {
            mHelper = DataBaseHelper.getHelper(context);
            mRecordDaoOp = mHelper.getDao(MeditationEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(MeditationEntity meditationEntity) {
        try {
            List<MeditationEntity> list = mRecordDaoOp.queryBuilder().where().eq("meditation_id", meditationEntity.getId()).query();
            if (list == null || list.size() == 0) {
                mRecordDaoOp.create(meditationEntity);
            } else {
                meditationEntity.setmId(list.get(0).getmId());
                mRecordDaoOp.update(meditationEntity);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }


    public List<MeditationEntity> listAll() {
        try {
            List<MeditationEntity> meditationEntities = mRecordDaoOp.queryBuilder().query();
            return meditationEntities;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MeditationEntity findMeditationById(long id) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<MeditationEntity> course = mRecordDaoOp.queryBuilder().where().eq("meditation_id", id).query();
            if (!course.isEmpty()) {
                return course.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MeditationEntity findMeditationByStartTime(int user, String startTime) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            List<MeditationEntity> meditationEntities = mRecordDaoOp.queryBuilder().where().eq("user", user).and().eq("start_time",startTime).query();
            if (!meditationEntities.isEmpty()) {
                return meditationEntities.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public MeditationEntity updateMeditationId(int userId, String startTime, long meditationId) {
        try {
            if (!mRecordDaoOp.isTableExists()) {
                return null;
            }
            UpdateBuilder<MeditationEntity, Integer> updateBuilder = mRecordDaoOp.updateBuilder();
            updateBuilder.updateColumnValue("meditation_id", meditationId).where().eq("start_time", startTime).and().eq("user", userId);
            updateBuilder.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void updateMeditationFileUrl(long meditationId, String url) {
        try {
            UpdateBuilder<MeditationEntity, Integer> updateBuilder = mRecordDaoOp.updateBuilder();
            updateBuilder.updateColumnValue("meditation_file", url).where().eq("meditation_id", meditationId);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
