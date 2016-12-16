package com.yang.nav.model;

import android.content.Context;
import android.util.Log;

import com.yang.nav.model.entity.DaoSession;

import java.util.List;

/**
 * 将所有创建的表格相同的部分封装到BaseDao中
 * Created by Yang on 2016/12/15.
 */

public class BaseDao<T> {
    public static final String TAG = BaseDao.class.getSimpleName();
    //调试模式开启
    public static final boolean DEBUG = true;
    public DaoManager manager;
    public DaoSession session;

    public BaseDao(Context context){
        manager = DaoManager.getInstance();
        manager.init(context);
        session = manager.getDaoSession();
        manager.setDebug(DEBUG);
    }
    /************************数据库插入操作***********************/
    /**
     * 插入单个对象
     * @param object
     * @return
     */
    public boolean insertObject(T object){
        boolean flag = false;
        try {
            flag = manager.getDaoSession().insert(object) != -1;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return flag;
    }
    public boolean insertMultObject(final List<T> objects){
        boolean flag = false;
        if (objects == null || objects.isEmpty()) {
            return false;
        }
        try{
            manager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        manager.getDaoSession().insertOrReplace(object);
                    }
                }
            });
            flag = true;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
        return flag;
    }
    /**************************数据库更新操作***********************/
    /**
     * 以对象形式进行数据修改
     * 其中必须要知道对象的主键ID
     * @param object
     * @return
     */
    public void  updateObject(T object){

        if (null == object){
            return ;
        }
        try {
            manager.getDaoSession().update(object);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 批量更新数据
     * @param objects
     * @return
     */
    public void updateMultObject(final List<T> objects, Class clss){
        if (null == objects || objects.isEmpty()){
            return;
        }
        try {

            session.getDao(clss).updateInTx(new Runnable() {
                @Override
                public void run() {
                    for(T object:objects){
                        session.update(object);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    /**************************数据库删除操作***********************/
    /**
     * 删除某个数据库表
     * @param clss
     * @return
     */
    public boolean deleteAll(Class clss){
        boolean flag = false;
        try {
            manager.getDaoSession().deleteAll(clss);
            flag = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            flag = false;
        }
        return flag;
    }

    /**
     * 删除某个对象
     * @param object
     * @return
     */
    public void deleteObject(T object){
        try {
            session.delete(object);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 异步批量删除数据
     * @param objects
     * @return
     */
    public boolean deleteMultObject(final List<T> objects, Class clss){
        boolean flag = false;
        if (null == objects || objects.isEmpty()){
            return flag;
        }
        try {

            session.getDao(clss).deleteInTx(objects);
            flag = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            flag = false;
        }
        return flag;
    }

    /**************************数据库查询操作***********************/

    /**
     * 获得某个表名
     * @return
     */
    public String getTablename(Class object){
        return session.getDao(object).getTablename();
    }


    /**
     * 根据主键ID来查询
     * @param id
     * @return
     */
    public T QueryById(long id,Class object){
        return (T) session.getDao(object).loadByRowId(id);
    }

    /**
     * 查询某条件下的对象
     * @param object
     * @return
     */
    public List<T> QueryObject(Class object,String where,String...params){
        Object obj = null;
        List<T> objects = null;
        try {
            obj = session.getDao(object);
            if (null == obj){
                return null;
            }
            objects = session.getDao(object).queryRaw(where,params);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return objects;
    }
    /**
     * 查询所有对象
     * @param object
     * @return
     */
    public List<T> QueryAll(Class object){
        List<T> objects = null;
        try {
            objects = (List<T>) session.getDao(object).loadAll();
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
        return objects;
    }

    /***************************关闭数据库*************************/
    /**
     * 关闭数据库一般在Odestory中使用
     */
    public void CloseDataBase(){
        manager.closeDataBase();
    }
}
