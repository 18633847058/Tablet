package com.nmea.obj;

public class AccNmeaObject extends AbstractNmeaObject {
	
	public AccNmeaObject(){
		this.objType = ACC_PROTOL;
	}
//	1.UTC 时间（小时/分钟/秒/十分之一秒）
	private String utc_Time;
//	2.X轴加速度
	private char x_acc;
//	3.Y轴加速度
	private char y_acc;
//	4.Z轴加速度
	private char z_acc;
	@Override
	public String toString() {
		if(this.msgFields == null || this.msgFields.size()<5){
			return "数据格式有误";
		}
		StringBuffer str = new StringBuffer();
		str.append("ACC消息：");
		
		str.append("定位点的UTC时间：");
		String time = this.msgFields.get(1);
		if(!time.isEmpty()) {
			str.append(time.substring(0, 2) + "时");
			str.append(time.substring(2, 4) + "分");
			str.append(time.substring(4, 6) + "秒");
		}
		String acc_x = this.msgFields.get(2);
		String acc_y = this.msgFields.get(3);
		String acc_z = this.msgFields.get(4);
		if(!acc_x.isEmpty()&&!acc_y.isEmpty()&&!acc_z.isEmpty()){
			str.append("，此时x轴方向上的加速度为");
			str.append(acc_x);
			str.append("，此时y轴方向上的加速度为");
			str.append(acc_y);
			str.append("，此时z轴方向上的加速度为");
			str.append(acc_z);
		}
		return str.toString();
	}
	
}	
