package com.anhquan.demo.rtsp.core.protocol;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RtpTransmitter {

	private long transmissionRate = 100;// default = 100
	private int checkingRate = 100; 	// default = 100
	private int packetSize = 100;		// default = 100bytes

	private MulticastSocket multicastSocket;
	private InetAddress metadataMulticastGroup;
	
	private volatile boolean flagStopped;
	private volatile boolean isTransmitting;
	
	private static Log _log = LogFactory.getLog(RtpTransmitter.class);
	
	private String multicastAddress;
	private int multicastPort;
	private int ttl;
	
	private byte[][] packets;
	private int nextPacketIndex;
	private int sequenceNumber;

	private class RegularTransmitter extends Thread {
		public void run() {
			long lastSendingTime = System.currentTimeMillis();
			long period;
	
			isTransmitting = true;
			
			while (!flagStopped){
				period = System.currentTimeMillis() - lastSendingTime;
		
				if (period >= transmissionRate) {
					lastSendingTime = System.currentTimeMillis();
					sendOnePacketFromBuffer();
				}
				
				try {
					Thread.sleep(checkingRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	
			clearFlag();
		}
	}
	
	public RtpTransmitter(String multicastAddress, int multicastPort, int ttl){
		
		this.multicastAddress = multicastAddress;
		this.multicastPort = multicastPort;
		this.ttl = ttl;
		this.sequenceNumber = 0;
		
		cleanBuffer();
		clearFlag();
	}
	
	public void setPacketSize (int packetSize){
		this.packetSize = packetSize;
	}
	
	public void setTransmissionRate(long transmissionRate){
		this.transmissionRate = transmissionRate;
	}
	
	public void setCheckingRate(int checkingRate){
		this.checkingRate = checkingRate;
	}
	
	/**
	 * Create a multicast socket and join the multicast group
	 * Buffer is also cleaned up
	 */
	public void connect(){
		try {
			metadataMulticastGroup = InetAddress.getByName(multicastAddress);

			multicastSocket = new MulticastSocket(multicastPort);
			multicastSocket.joinGroup(metadataMulticastGroup);

			multicastSocket.setTimeToLive(ttl);
			
			cleanBuffer();

			_log.debug("MulticastSocket is ready");

		} catch (UnknownHostException e) {
			_log.warn(e.getMessage());
		} catch (SocketException e) {
			_log.warn(e.getMessage());
		} catch (IOException e) {
			_log.warn(e.getMessage());
		}
	}
	
	/**
	 * Stop the transmission, leave the multicast group and close the socket.
	 * Buffer is also cleaned up
	 */
	public void disconnect(){
		try {
			
			stopTransmission();
			
			if (multicastSocket != null) {
				multicastSocket.leaveGroup(metadataMulticastGroup);
				multicastSocket.close();
				
				cleanBuffer();
				
				_log.debug("MulticastSocket is closed");
			}
		} catch (IOException e) {
			_log.warn(e.getMessage());
		}	
	}
	
	/**
	 * Start sending packets from the buffer.
	 */
	public void startTransmission(){		
		if (isTransmitting){
			_log.debug("RTPTransmitter is already running");
			return;
		}
		
		clearFlag();
		RegularTransmitter transmitter = new RegularTransmitter();
		transmitter.start();
		
		_log.debug("RTPTransmitter is starting to send data");
	}
	
	/**
	 * Stop sending packets. It will wait until the transmitter stops
	 */
	public void stopTransmission(){
		if (!isTransmitting){
			_log.debug("RTPTransmitter is already stopped");
			return;
		}
		_log.debug("Trying to stop transmission ... please wait");
		long lastTime = System.currentTimeMillis();
		flagStopped = true;		
		
		while(isTransmitting){
			//_log.debug("wating for shutdown...");
		}
		
		long duration = System.currentTimeMillis() - lastTime;
		_log.debug("RTPTransmitter is stopped (waited time = "+duration+" milisecs)");
	}

	/**
	 * Load data into the output buffer
	 * @param data
	 */
	public void loadData(byte[] data){
		
		if (data == null){
			synchronized (this){
				nextPacketIndex = 0;
				packets = null;
			}
			return;
		}
		
		long dataSize = data.length;
		int lastPacketSize;
		long packetCount;

		int remainder = (int) (dataSize % packetSize);
		if (remainder == 0) {
			packetCount = dataSize / packetSize;
			lastPacketSize = packetSize;
		} else {
			packetCount = (int) Math.floor(dataSize / (1.0f * packetSize)) + 1;
			lastPacketSize = remainder;
		}
		
		// Create the 2-dimension array to hold the data
		byte[][] tmp_packets = new byte[(int) packetCount][packetSize];
		// convert to array of packets
		for (int i = 0; i < packetCount; i++) {
			int currentPacketSize = packetSize;

			if (i == (packetCount - 1)) {
				currentPacketSize = lastPacketSize;
			}

			for (int j = 0; j < currentPacketSize; j++) {
				tmp_packets[i][j] = data[i * packetSize + j];
			}
		}
		_log.debug("packetcount = " + packetCount);
		
		synchronized (this){
			packets = tmp_packets;
			nextPacketIndex = 0;
		}
		_log.debug("new data is loaded");
	}
	
	private void clearFlag() {
		flagStopped = false;
		isTransmitting = false;
	}
	
	private void cleanBuffer(){
		packets = null;
		nextPacketIndex = 0;
	}
	
	private void sendOnePacketFromBuffer(){
		if ((packets == null) || (packets.length <= 0)) {
			_log.debug("Try to send metadata, but there's nothing to send");
			return;
		}
		
		long packetCount = packets.length;
		byte[] packet = packets[nextPacketIndex];
		RtpPacket rtpPacket = new RtpPacket(packet, packet.length, sequenceNumber++);

		// mark the last packet
		int lastPacketIndex = packets.length - 1;
		boolean isMarked = (nextPacketIndex == lastPacketIndex);
		rtpPacket.setMarker(isMarked);
		_log.debug("Send packet["+nextPacketIndex+"] = "+packets[nextPacketIndex][0]+(isMarked ? " [M]" : " [ ]"));
		byte[] dgData = new byte[rtpPacket.getLength()];

		rtpPacket.getBytes(dgData);

		DatagramPacket dgPacket = new DatagramPacket(dgData, rtpPacket.getLength(), metadataMulticastGroup, multicastPort);

		try {
			// _log.debug("send metadata packet #"+nextPacketIndex);
			multicastSocket.send(dgPacket);
			nextPacketIndex++;
			if (nextPacketIndex == packetCount) {
				// no more data, back to first packet
				nextPacketIndex = 0;
			}
		} catch (IOException e) {
			_log.warn("Error when sending metadata:" + e.getMessage());
		}
	}
}
