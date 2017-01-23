package com.nmea.core;
import com.nmea.obj.AbstractNmeaObject;

import java.util.Observable;
import java.util.Observer;


public class ObserverPrintMsg implements Observer {


	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
//		System.out.println(arg);
		arg.toString();
		CodecManager.setNmeaObject((AbstractNmeaObject) arg);
	}

}
