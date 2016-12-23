package com.yang.nav;

import android.app.Application;

import com.mapbar.license.License;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.mapdal.WorldManager;
import com.yang.nav.model.PointManager;

/**
 * Created by Yang on 2016/12/14.
 */

public class BaseApplication extends Application {
    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
//    public static final boolean ENCRYPTED = true;
//
//    private DaoSession daoSession;
    private PointManager pointManager;

    @Override
    public void onCreate() {
        super.onCreate();
        pointManager = PointManager.getInstance(getApplicationContext());
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
//        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
//        daoSession = new DaoMaster(db).newSession();
    }

    @Override
    public void onTerminate() {
        pointManager.closeDataBase();
        WorldManager.getInstance().cleanup();
        NativeEnv.cleanup();
        License.cleanup();
        super.onTerminate();
    }
//    public DaoSession getDaoSession() {
//        return daoSession;
//    }


}
