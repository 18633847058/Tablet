package com.nmea.obj;

public class VelNmeaObject extends AbstractNmeaObject {
	
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

	public VelNmeaObject() {
		this.objType = VEL_PROTOL;
	}

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
		if (this.getType().equals("G2")) {
			str.append("，对地水平速度为");
			str.append(hor_s + "米/秒");
			hor_speed = hor_s;

			str.append("，此时相对真北的的实际对地运动方向为");
			str.append(an + "度");
			angel = an;
			
			str.append("，垂直速度为" + ver_s +"米/秒");
			ver_speed = ver_s;
		}else{
			
			String x = this.msgFields.get(5);
			String y = this.msgFields.get(6);
			String z = this.msgFields.get(7);
			if(!x.isEmpty()&&!y.isEmpty()&&!z.isEmpty()){
				ECEF_x = x;
				ECEF_y = y;
				ECEF_z = z;
				str.append("。此时的ECEF坐标为("+ x +","+ y +","+ z +")");
			}
		}
		return str.toString();
	}

	public String getUtc_Time() {
		return utc_Time;
	}

	public void setUtc_Time(String utc_Time) {
		this.utc_Time = utc_Time;
	}

	public String getHor_speed() {
		return hor_speed;
	}

	public void setHor_speed(String hor_speed) {
		this.hor_speed = hor_speed;
	}

	public String getAngel() {
		return angel;
	}

	public void setAngel(String angel) {
		this.angel = angel;
	}

	public String getVer_speed() {
		return ver_speed;
	}

	public void setVer_speed(String ver_speed) {
		this.ver_speed = ver_speed;
	}

	public String getECEF_x() {
		return ECEF_x;
	}

	public void setECEF_x(String ECEF_x) {
		this.ECEF_x = ECEF_x;
	}

	public String getECEF_y() {
		return ECEF_y;
	}

	public void setECEF_y(String ECEF_y) {
		this.ECEF_y = ECEF_y;
	}

	public String getECEF_z() {
		return ECEF_z;
	}

	public void setECEF_z(String ECEF_z) {
		this.ECEF_z = ECEF_z;
	}
}
