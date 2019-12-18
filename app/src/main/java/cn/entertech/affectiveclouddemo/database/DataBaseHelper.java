package cn.entertech.affectiveclouddemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import cn.entertech.affectiveclouddemo.model.MeditationEntity;
import cn.entertech.affectiveclouddemo.model.UserLessonEntity;


/**
 * Created by EnterTech on 2016/11/7.
 */

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static DataBaseHelper mInstance;

    private Map<String, Dao> daos = new HashMap<String, Dao>();

    public static synchronized DataBaseHelper getHelper(Context context) {
        context = context.getApplicationContext();
        if (null == mInstance) {
            synchronized (DataBaseHelper.class) {
                if (null == mInstance)
                    mInstance = new DataBaseHelper(context);
            }
        }
        return mInstance;
    }


    private DataBaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserLessonEntity.class);
            TableUtils.createTable(connectionSource, MeditationEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 数据库更新规则写在这里
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

//            if (oldVersion < 4 && 4 <= newVersion) {
//                TableUtils.dropTable(connectionSource, Record.class, true);
//                TableUtils.createTable(connectionSource, Record.class);
//            }
        } catch (Exception e) {
//            Logger.d("update database error:"+e.toString());
            e.printStackTrace();
        }
    }

    public synchronized Dao getDao(Class cls) throws SQLException {
        Dao dao = null;
        String className = cls.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (null == dao) {
            dao = super.getDao(cls);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();

        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }

}
