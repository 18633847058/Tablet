package com.nmea.obj;

public class GyrNmeaObject extends AbstractNmeaObject {
	
	public GyrNmeaObject(){
		this.objType = GYR_PROTOL;
	}
	
//	1.UTC 时间（小时/分钟/秒/十分之一秒）
	private String utc_Time;
//	2.绕X轴旋转角速度
	private String x_rotation_speed;
//	3.绕Y轴旋转角速度
	private String y_rotation_speed;
//	4.绕Z轴旋转角速度
	private String z_rotation_speed;
	@Override
	public String toString() {
		if(this.msgFields == null || this.msgFields.size()< 5){
			return "数据格式有误";
		}
		StringBuffer str = new StringBuffer();
		str.append("GYR消息：");
		
		String time = this.msgFields.get(1);
		if(!time.isEmpty()) {
			str.append("定位点的UTC时间：");
			str.append(time.substring(0, 2) + "时");
			str.append(time.substring(2, 4) + "分");
			str.append(time.substring(4, 6) + "秒");
		}
		String xrs = this.msgFields.get(2);
		if(!xrs.isEmpty()) {
			str.append("，此时绕x轴旋转角速度为");
			str.append(xrs);
		}
		String yrs = this.msgFields.get(3);
		if(!yrs.isEmpty()) {
			str.append("，绕y轴旋转角速度为");
			str.append(yrs);
		}

		String zrs = this.msgFields.get(4);
		if (!zrs.isEmpty()) {
			str.append("，绕z轴旋转角速度为");
			str.append(zrs);
		}
		return str.toString();
	}
	
	
}
