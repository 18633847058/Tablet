package com.yang.nav.map;

import android.graphics.Point;

public class Config {
	
	//是否调试
	public static final boolean DEBUG = true;
	//默认在线或离线
	public static boolean online = false;
	//默认地图中心点
	public static Point centerPoint = new Point(11640152, 3990768);
	//导航
	//测试起点
	public static String NAVI_START_NAME = "鸟巢";
	public static Point NAVI_START_POINT = new Point(11639524, 3999300);  //鸟巢
	//途经点
	public static Point NAVI_WAYPOINT = new Point(11634753, 3992183);
	//测试终点
	public static String NAVI_END_NAME = "天安门";
	public static Point NAVI_END_POINT = new Point(11640152, 3990768);
	
	public static String getOnlineText(boolean isonline){
		if(isonline){
			return "在线";
		}else{
			return "离线";
		}
	}
}
