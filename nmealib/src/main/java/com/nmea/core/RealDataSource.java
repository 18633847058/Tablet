package com.nmea.core;

import com.nmea.obj.AbstractNmeaObject;
import com.nmea.obj.GgaNmeaObject;
import com.nmea.obj.GsvNmeaObject;

public class RealDataSource extends AbstractDataSource{
	
	public static void main(String[] args) throws Exception {
		CodecManager codecManager = CodecManager.getInstance();
		String vdm = "!AIVDM,1,1,,B,16:>>s5Oh08dLO8AsMAVqptj0@>p,0*67\r\n";
		String rmc = "$GPRMC,161229.487,A,3723.2475,N,12158.3416,W,0.13,309.62,120598,,*10\r\n";
		String gll = "$GPGLL,3723.2475,N,12158.3416,W,161229.487,A*2C\r\n";
		String gga = "$GPGGA,161229.487,3723.2475,N,12158.3416,W,1,07,1.0,9.0,M, , ,,0000*18\r\n";
		String text = "$GPGGA,161229.487,3723.2475,N,12158.3416,W,1,07,1.0,9.0,M, , ,,0000*18\r\n";
		String gsv = "$GPGSV,3,1,10,20,78,331,45,01,59,235,47,22,41,069,,13,32,252,45*70";

		codecManager.decode(text);
		AbstractNmeaObject nmeaObject = CodecManager.getNmeaObject();
		if (nmeaObject != null) {
			if (nmeaObject.getObjType().equals(AbstractNmeaObject.GSV_PROTOL)) {
				GsvNmeaObject gsvNmeaObject = (GsvNmeaObject) nmeaObject;
				System.out.println(gsvNmeaObject.getContent());
			}
			if (nmeaObject.getObjType().equals(AbstractNmeaObject.GGA_PROTOL)) {
				GgaNmeaObject ggaNmeaObject = (GgaNmeaObject) nmeaObject;
				System.out.println(ggaNmeaObject.getContent());
			}
		}
	}
}
