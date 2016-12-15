package com.yang.nav.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yang.nav.model.entity.DaoMaster;
import com.yang.nav.model.entity.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by Yang on 2016/12/15.
 */

public class DaoManager {
    private static final String TAG = DaoManager.class.getSimpleName();
    private static final String DB_NAME = "route.db";//数据库名称
    private volatile static DaoManager daoManager;//多线程访问
    private static DaoMaster.DevOpenHelper devOpenHelper;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static SQLiteDatabase database;
    private Context context;

    /**
     * 使用单例模式获得操作数据库的对象
     * @return daoManager
     */
    public static DaoManager getInstance(){
        DaoManager instance = null;
        if(daoManager == null){
            synchronized (DaoManager.class){
                if(instance == null){
                    instance = new DaoManager();
                    daoManager = instance;
                }
            }
        }
        return daoManager;
    }

    /**
     * 初始化Context对象
     * @param context
     */
    public void init(Context context){
        this.context = context;
    }

    /**
     * 判断数据库是否存在，如果不存在则创建
     * @return daoMaster
     */
    public DaoMaster getDaoMaster(){
        if (daoMaster == null) {
            devOpenHelper = new DaoMaster.DevOpenHelper(context,DB_NAME,null);
            daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 创建对增删改查处理的DaoSession对象
     * @return daoSession
     */
    public DaoSession getDaoSession(){
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     * @param flag
     */
    public void setDebug(boolean flag){
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

    /**
     * 关闭数据库
     */
    public void closeDataBase(){
        closeHelper();
        closeDaoSession();
    }

    private void closeDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    private void closeHelper(){
        if (devOpenHelper != null) {
            devOpenHelper.close();
            devOpenHelper = null;
        }
    }
}
