package com.nmea.obj;

public class VelNmeaObject extends AbstractNmeaObject {
	
	public VelNmeaObject(){
		this.objType = VEL_PROTOL;
	}
	
	//1.UTC时间，格式为hhmmss.sss
	private String utc_Time;
	//2.对地水平速度，米/秒
	private String hor_speed;
	//3.相对于真北的实际对地运动方向（相对地面轨迹），度
	private String angel;
	//4.垂直速度，米/秒，正值表示高度增加（向上），负值表示高度下降（向下）
	private String ver_speed;
	//5.ECEF 坐标系X轴速度
	private String ECEF_x;
	//6.ECEF 坐标系Y轴速度
	private String ECEF_y;
	//7.ECEF 坐标系Z轴速度
	private String ECEF_z;
	@Override
	public String toString() {
		if(this.msgFields == null || this.msgFields.size()< 8){
			return "数据格式有误";
		}
		StringBuffer str = new StringBuffer();
		str.append("VEL消息：");

		String time = this.msgFields.get(1);
		if (!time.isEmpty()) {
			str.append("定位点的UTC时间：");
			str.append(time.substring(0, 2) + "时");
			str.append(time.substring(2, 4) + "分");
			str.append(time.substring(4, 6) + "秒");
		}

		String hor_s = this.msgFields.get(2);
		String an = this.msgFields.get(3);
		String ver_s = this.msgFields.get(4);
		if(!hor_s.isEmpty() && !an.isEmpty() && !ver_s.isEmpty()){
			str.append("，对地水平速度为");
			str.append(hor_s + "米/秒");
		
			str.append("，此时相对真被的的实际对地运动方向为");
			str.append(an + "度");
			
			str.append("，垂直速度为" + ver_s +"米/秒");
		}else{
			
			String x = this.msgFields.get(5);
			String y = this.msgFields.get(6);
			String z = this.msgFields.get(7);
			if(!x.isEmpty()&&!y.isEmpty()&&!z.isEmpty()){
				str.append("。此时的ECEF坐标为("+ x +","+ y +","+ z +")");
			}
		}
		return str.toString();
	}
	

}
