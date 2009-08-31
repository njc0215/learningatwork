package com.anhquan.demo.rtsp.core.message;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class RtspMessage {

	/**
	 * Get value of the field by name
	 * @param fieldName
	 * @return
	 */
	public String getHeaderField(String fieldName){
		return null;
	}
	
	/**
	 * Get all header fields in this message
	 * @return
	 */
	public java.util.LinkedHashMap getHeaderFields(){
		return null;
	}
	
	/**
	 * Parse the RTSP Message from the input stream
	 * @param in
	 */
	public void parse(InputStream in){
		
	}
	
	/**
	 * Send this message
	 * @param out
	 */
	public void send(OutputStream out){
		
	}
	
	/**
	 * Append the data to the InputStream and send them out
	 * @param out
	 * @param data
	 */
	public void send(OutputStream out, InputStream data){
		
	}

	/**
	 * Set body of the going-to-be-sent message
	 * @param data
	 */
	public void setBody(byte[] data){
		
	}
	
	/**
	 * Set header field by name
	 * @param fieldName
	 * @param fieldValue
	 */
	public void setHeaderField(String fieldName, String fieldValue){
		
	}
	
	/**
	 * Set all header fields
	 * @param fields
	 */
	public void setHeaderFields(Map fields){
		
	}
	
	/**
	 * Convert the RTSP Header to string
	 */
	public String toString(){
		return "";
	}
}
