package com.anhquan.demo.rtsp.core;

/**
 * rtsp_URL  =   ( "rtsp:" | "rtspu:" )
 *                  "//" host [ ":" port ] [ abs_path ]
 * host      =   A legal Internet host domain name of IP address
 *               
 * @author anhquan
 *
 */
public class RtspURL {

	String url;
	
	public RtspURL(String url){
		this.url = url;
	}
	
	public boolean isValid(){
		//TODO check if the url is valid
		return true;
	}
}
