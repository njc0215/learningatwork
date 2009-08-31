package com.anhquan.demo.rtsp.core.protocol;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.anhquan.demo.rtsp.core.protocol.RtpTransmitter;

public class RTPTransmitterTest {

	private static final String MULTICAST_IP = "239.1.1.1";
	private static final int MULTICAST_PORT = 3000;
	
	RtpTransmitter transmitter;
	byte[] data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
	
	@Before
	public void setUp(){
		transmitter = new RtpTransmitter(MULTICAST_IP, MULTICAST_PORT, 1);
		transmitter.connect();
		transmitter.setPacketSize(1);
	}
	
	@After
	public void tearDown(){
		transmitter.disconnect();
	}
	
	//@Test	
	public void testSimpleTransmission() throws InterruptedException{
		
		transmitter.loadData(data);
		transmitter.startTransmission();
		
		Thread.sleep(1000);
		
		transmitter.stopTransmission();
		
		Thread.sleep(1000);
		
		transmitter.startTransmission();
		
		Thread.sleep(1000);
	}
	
	@Test	
	public void testLoadNewDataDuringTransmission() throws InterruptedException{
		
		transmitter.loadData(data);
		transmitter.startTransmission();
		
		Thread.sleep(1000);
		
		transmitter.loadData("12345".getBytes());
		
		Thread.sleep(3000);
				
	}
}
