package com.nmea.obj;

import com.nmea.util.DegreeUtils;

import org.apache.commons.lang.StringUtils;

public class GgaNmeaObject extends AbstractNmeaObject {

	//1.UTC时间，格式为hhmmss.sss
	private String utc_Time;
	//2.纬度，格式为ddmm.mmmm(第一位是零也将传送)；
	private String latitude;
	//3.纬度半球，N或S(北纬或南纬)
	private char lat_direction;
	//4.经度，格式为dddmm.mmmm(第一位零也将传送)；
	private String longitude;
	//5.经度半球，E或W(东经或西经)
	private char long_direction;
	//6.定位质量指示，0=定位无效，1=定位有效；
	private int gpa_flag;
	//7.使用卫星数量，从00到12(第一个零也将传送)
	private String count;
	//8.水平精确度，0.5到99.9
	private float horizontal;
	//9.天线离海平面的高度，-9999.9到9999.9米
	private float high_1;
	//10.M 米
	//11.大地水准面高度，-9999.9到9999.9米
	private float high_2;
	//12.M 米
	//13.差分GPS数据期限(RTCM SC-104)，最后设立RTCM传送的秒数量
	private String expired;
	//14.差分参考基站标号，从0000到1023(首位0也将传送)
	private String code;

    public GgaNmeaObject() {
        this.objType = GGA_PROTOL;
    }
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

    public int getGpa_flag() {
        return gpa_flag;
    }

    public void setGpa_flag(int gpa_flag) {
        this.gpa_flag = gpa_flag;
    }

	public String getUtc_Time() {
		return utc_Time;
	}

	public void setUtc_Time(String utc_Time) {
		this.utc_Time = utc_Time;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		if(this.msgFields == null || this.msgFields.size()< 15){
			return "数据格式有误";
		}
        if (!this.msgFields.get(6).isEmpty() && "0".equals(this.msgFields.get(6))) {
            return "未定位";
        }
        StringBuffer str = new StringBuffer();
		str.append("GGA消息:");

		str.append("定位点的UTC时间：");
		String time = this.msgFields.get(1);
		StringBuffer utc = new StringBuffer();
		if(!time.isEmpty()) {
			utc.append(time.substring(0, 2) + "时");
			utc.append(time.substring(2, 4) + "分");
			utc.append(time.substring(4, 6) + "秒");

			setUtc_Time(utc.toString());
		}
		str.append(utc + "，");
		StringBuffer lat = new StringBuffer();
		if(!this.msgFields.get(3).isEmpty()&&"N".equals(this.msgFields.get(3))){
			str.append("北纬");
		}
		if(!this.msgFields.get(3).isEmpty()&&"S".equals(this.msgFields.get(3))){
			str.append("南纬");
			lat.append("-");
		}

		String latitude = this.msgFields.get(2);
		if (!latitude.isEmpty()) {
			str.append(latitude.substring(0, 2)+"度");
			str.append(latitude.substring(2)+"分");
			lat.append(DegreeUtils.degreeConvert(latitude));
			setLatitude(lat.toString());
		}

		StringBuffer lon = new StringBuffer();
		if(!this.msgFields.get(5).isEmpty()&&"W".equals(this.msgFields.get(5))){
			str.append("，西经");
			lon.append("-");
		}
		if(!this.msgFields.get(5).isEmpty()&&"E".equals(this.msgFields.get(5))){
			str.append("，东经");
		}

		String longitude = this.msgFields.get(4);
		if (!longitude.isEmpty()) {
			str.append(longitude.substring(0, 3)+"度");
			str.append(longitude.substring(3)+"分");
			lon.append(DegreeUtils.degreeConvert(longitude));
			setLongitude(lon.toString());
		}

		str.append(",GPS定位状态指示:");
		if(!this.msgFields.get(6).isEmpty()&&"0".equals(this.msgFields.get(6))){
            setGpa_flag(Integer.parseInt(this.msgFields.get(6)));
            str.append("未定位");
		}
		if(!this.msgFields.get(6).isEmpty()&&"1".equals(this.msgFields.get(6))){
            setGpa_flag(Integer.parseInt(this.msgFields.get(6)));
            str.append("无差分，SPS模式，定位有效");
		}
		if(!this.msgFields.get(6).isEmpty()&&"2".equals(this.msgFields.get(6))){
            setGpa_flag(Integer.parseInt(this.msgFields.get(6)));
            str.append("带差分，SPS模式，定位有效");
		}
		if(!this.msgFields.get(6).isEmpty()&&"3".equals(this.msgFields.get(6))){
            setGpa_flag(Integer.parseInt(this.msgFields.get(6)));
            str.append("PPS模式，定位有效");
		}
		if(!this.msgFields.get(7).isEmpty()) {
			str.append(",使用卫星数量:");
			str.append(this.msgFields.get(7));
		}
		if(!this.msgFields.get(8).isEmpty()) {
			str.append(",水平精度衰减因子:");
			str.append(this.msgFields.get(8));
		}
		if(!this.msgFields.get(9).isEmpty()) {
			str.append(",海平面高度:");
			str.append(this.msgFields.get(9));
			str.append("米");
		}
		if(!this.msgFields.get(11).isEmpty()) {
			str.append(",大地椭球面相对海平面的高度:");
			str.append(this.msgFields.get(11));
			str.append("米");
		}
		if(!StringUtils.isEmpty(this.msgFields.get(13))){
			str.append(",差分修订时间:");
			str.append(this.msgFields.get(13)+"秒");
		}

		if(!StringUtils.isEmpty(this.msgFields.get(14))){
			str.append(",差分参考基站ID号:");
			str.append(this.msgFields.get(14));
		}
		return str.toString();
	}
}
