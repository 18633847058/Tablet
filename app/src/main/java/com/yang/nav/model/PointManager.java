package com.yang.nav.model;

import android.content.Context;
import android.util.Log;

import com.yang.nav.model.entity.Frame;
import com.yang.nav.model.entity.Point;
import com.yang.nav.model.entity.PointDao;
import com.yang.nav.utils.TimeUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yang on 2016/12/15.
 */

public class PointManager extends BaseDao<Point> {

    public static final String SF = "yyyy-MM-dd HH:mm";
    private volatile static PointManager pointManager;//多线程访问

    private PointManager(Context context) {
        super(context);
    }

    public static PointManager getInstance(Context context){
        PointManager instance = null;
        if(pointManager == null){
            synchronized (DaoManager.class){
                if(instance == null){
                    instance = new PointManager(context);
                    pointManager = instance;
                }
            }
        }
        return pointManager;
    }

    public boolean insertFrames(final List<Frame> objects) {
        boolean flag = false;
        if (objects == null || objects.isEmpty()) {
            return false;
        }
        try {
            manager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Frame object : objects) {
                        manager.getDaoSession().insertOrReplace(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return flag;
    }
    /***************************数据库查询*************************/

    /**
     * 通过ID查询对象
     * @param id
     * @return
     */
    public Point loadById(long id){

        return session.getPointDao().load(id);
    }

    /**
     * 获取某个对象的主键ID
     * @param point
     * @return
     */
    public long getID(Point point){

        return session.getPointDao().getKey(point);
    }

    /**
     * 通过名字获取Customer对象
     * @return
     */
    public List<Point> getPointByTime(String key){
        QueryBuilder queryBuilder =  session.getPointDao().queryBuilder();
        queryBuilder.where(PointDao.Properties.Time.eq(key));
        int size = queryBuilder.list().size();
        if (size > 0){
            return queryBuilder.list();
        }else{
            return null;
        }
    }

    /**
     * 通过名字获取Customer对象的id集合
     * @return
     */
    public List<Long> getIdByName(String key){
        List<Point> points = getPointByTime(key);
        List<Long> ids = new ArrayList<Long>();
        int size = points.size();
        if (size > 0){
            for (int i = 0;i < size;i++){
                ids.add(points.get(i).getId());
            }
            return ids;
        }else{
            return null;
        }
    }

    /**
     * 通过时间间隔获取point集合
     * @param s
     * @param e
     * @return
     */
    public List<Point> getPointsByTime(String s, String e){
        Long start = TimeUtils.convertToMil(s, SF);
        Long end = TimeUtils.convertToMil(e, SF);
        QueryBuilder queryBuilder =  session.getPointDao().queryBuilder();
        queryBuilder.where(PointDao.Properties.Time.between(start,end));
        int size = queryBuilder.list().size();
        if (size > 0){
            return queryBuilder.list();
        }else{
            return null;
        }
    }

    public List<Frame> queryAllFrames(Class object) {
        List<Frame> objects = null;
        try {
            objects = (List<Frame>) session.getDao(object).loadAll();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return objects;
    }

    /***************************数据库删除*************************/

    /**
     * 根据ID进行数据库的删除操作
     * @param id
     */
    public void deleteById(long id){
        session.getPointDao().deleteByKey(id);
    }


    /**
     * 根据ID同步删除数据库操作
     * @param ids
     */
    public void deleteByIds(List<Long> ids){
        session.getPointDao().deleteByKeyInTx(ids);
    }
    public boolean deletePointsByTime(String start, String end){
        List<Point> points = getPointsByTime(start,end);
        return points!=null&&!points.isEmpty()&&deleteMultObject(points,Point.class);
    }
}
