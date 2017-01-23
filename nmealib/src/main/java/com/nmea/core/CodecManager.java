package com.nmea.core;

import com.nmea.codec.AbstractNmeaSentenceCodec;
import com.nmea.codec.AccNmeaCodec;
import com.nmea.codec.GgaNmeaCodec;
import com.nmea.codec.GllNmeaCodec;
import com.nmea.codec.GsvNmeaCodec;
import com.nmea.codec.GyrNmeaCodec;
import com.nmea.codec.RmcNmeaCodec;
import com.nmea.codec.VdmNmeaCodec;
import com.nmea.codec.VelNmeaCodec;
import com.nmea.obj.AbstractNmeaObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CodecManager {
	private static List<AbstractNmeaObject> nmeaObjectList = new ArrayList<>();
	private static CodecManager codecManager;
	public Buffer buffer;
	private Map<String, AbstractNmeaSentenceCodec> nmeaCodecs = new HashMap<String, AbstractNmeaSentenceCodec>();
	private CodecManager() {
		this.nmeaCodecs.put(AbstractNmeaObject.GGA_PROTOL, new GgaNmeaCodec());
		this.nmeaCodecs.put(AbstractNmeaObject.GLL_PROTOL, new GllNmeaCodec());
		this.nmeaCodecs.put(AbstractNmeaObject.RMC_PROTOL, new RmcNmeaCodec());
		this.nmeaCodecs.put(AbstractNmeaObject.VDM_PROTOL, new VdmNmeaCodec());
		this.nmeaCodecs.put(AbstractNmeaObject.VEL_PROTOL, new VelNmeaCodec());
		this.nmeaCodecs.put(AbstractNmeaObject.ACC_PROTOL, new AccNmeaCodec());
		this.nmeaCodecs.put(AbstractNmeaObject.GYR_PROTOL, new GyrNmeaCodec());
		this.nmeaCodecs.put(AbstractNmeaObject.GSV_PROTOL, new GsvNmeaCodec());
	}

	public static AbstractNmeaObject getNmeaObject(){
		if (!nmeaObjectList.isEmpty()) {
			return nmeaObjectList.remove(0);
		}
		return null;
	}

	public static void setNmeaObject(AbstractNmeaObject object) {
		nmeaObjectList.add(object);
	}

	public static synchronized CodecManager getInstance(){
		if(codecManager == null){
			codecManager = new CodecManager();
		}
		return codecManager;
	}

	public void decode(String content) throws Exception {
		String objType = AbstractNmeaSentenceCodec.getContentType(content);
		if (AbstractNmeaObject.GGA_PROTOL.equals(objType)) {
			this.getNmeaCodec(AbstractNmeaObject.GGA_PROTOL).decode(content);
		}
		if (AbstractNmeaObject.GLL_PROTOL.equals(objType)) {
			this.getNmeaCodec(AbstractNmeaObject.GLL_PROTOL).decode(content);
		}
		if (AbstractNmeaObject.RMC_PROTOL.equals(objType)) {
			this.getNmeaCodec(AbstractNmeaObject.RMC_PROTOL).decode(content);
		}
		if (AbstractNmeaObject.VDM_PROTOL.equals(objType)) {
			this.getNmeaCodec(AbstractNmeaObject.VDM_PROTOL).decode(content);
		}
		if (AbstractNmeaObject.VEL_PROTOL.equals(objType)) {
			this.getNmeaCodec(AbstractNmeaObject.VEL_PROTOL).decode(content);
		}
		if (AbstractNmeaObject.ACC_PROTOL.equals(objType)) {
			this.getNmeaCodec(AbstractNmeaObject.ACC_PROTOL).decode(content);
		}
		if (AbstractNmeaObject.GYR_PROTOL.equals(objType)) {
			this.getNmeaCodec(AbstractNmeaObject.GYR_PROTOL).decode(content);
		}
		if (AbstractNmeaObject.GSV_PROTOL.equals(objType)) {
			this.getNmeaCodec(AbstractNmeaObject.GSV_PROTOL).decode(content);
		}
	}

	public List<String> encode(AbstractNmeaObject obj) throws Exception {
		List<String> content = new ArrayList<String>();
		if (AbstractNmeaObject.GGA_PROTOL.equals(obj.getObjType())) {
			content = this.getNmeaCodec(AbstractNmeaObject.GGA_PROTOL).encode(obj);
		}
		if (AbstractNmeaObject.GLL_PROTOL.equals(obj.getObjType())) {
			content = this.getNmeaCodec(AbstractNmeaObject.GLL_PROTOL).encode(obj);
		}
		if (AbstractNmeaObject.RMC_PROTOL.equals(obj.getObjType())) {
			content =this.getNmeaCodec(AbstractNmeaObject.RMC_PROTOL).encode(obj);
		}
		if (AbstractNmeaObject.VDM_PROTOL.equals(obj.getObjType())) {
			content = this.getNmeaCodec(AbstractNmeaObject.VDM_PROTOL).encode(obj);
		}
		if (AbstractNmeaObject.VEL_PROTOL.equals(obj.getObjType())) {
			content = this.getNmeaCodec(AbstractNmeaObject.VEL_PROTOL).encode(obj);
		}
		if (AbstractNmeaObject.ACC_PROTOL.equals(obj.getObjType())) {
			content = this.getNmeaCodec(AbstractNmeaObject.ACC_PROTOL).encode(obj);
		}
		if (AbstractNmeaObject.GYR_PROTOL.equals(obj.getObjType())) {
			content = this.getNmeaCodec(AbstractNmeaObject.GYR_PROTOL).encode(obj);
		}
		return content;
	}
	
	private AbstractNmeaSentenceCodec getNmeaCodec(String key){
		return nmeaCodecs.get(key);
	}
}
