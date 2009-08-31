package com.anhquan.demo.rtsp.core.message;

import java.io.InputStream;

public class RtspRequest extends RtspMessage{

	public void setVersion(String version){
		
	}

	public String getVersion(){
		return "";
	}
	
	/**
	 * Parse RTSP Request from an input stream
	 * @param in
	 * @return
	 */
	public static RtspRequest parseRequest(InputStream in){
		
		return null;
	}
}
