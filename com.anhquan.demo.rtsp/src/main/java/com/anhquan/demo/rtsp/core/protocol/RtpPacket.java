package com.anhquan.demo.rtsp.core.protocol;

public class RtpPacket {

	/** RTP header without extension **/
	static int HEADER_SIZE = 12;	

	public int Version;
	public int Padding;
	public int Extension;
	public int CC;
	public int Marker;
	public int PayloadType;
	public int SequenceNumber;
	public int TimeStamp;
	public int Ssrc;

	public byte[] header;

	public int payloadSize;
	public byte[] payload;

	public RtpPacket(byte[] data,int dataLength,int sequenceNumber){
		
		this(35,sequenceNumber,0,data,dataLength);
	}
	
	/**
	 * RTPPacket Constructor
	 * @param payloadType
	 * @param sequenceNumber
	 * @param timeStamp
	 * @param data
	 * @param dataLength
	 */
	public RtpPacket(int payloadType, int sequenceNumber, int timeStamp, byte[] data,int dataLength) {
		
		/** RTP Header */
		header = new byte[HEADER_SIZE];
		
		//header[0]
		this.Version = 2;	//2 bits
		this.Padding = 0;	//1 bit
		this.Extension = 0;	//1 bit
		this.CC = 0;			//4 bits
		
		//header[1]
		//Marker 				//1 bit
		this.PayloadType = payloadType;	//7 bits
		
		//header[2], header[3]
		SequenceNumber = sequenceNumber;	//16 bits
		
		//header[4][5][6][7]
		this.TimeStamp = timeStamp;			//32 bits
		
		//header[8][9][10][11]
		Ssrc = 1111;				//32 bits	- choose randomly

		header[0] = (byte) (((Version << 6) & 0xC0) | ((Padding << 5) & 0x20) | ((Extension << 4) & 0x10) | (CC & 0x0F));
		header[1] = (byte)(((Marker << 7) & 0x80) | (PayloadType & 0x7F));  
		
		header[2] = (byte) (SequenceNumber >> 8);
		header[3] = (byte) (SequenceNumber & 0xFF);
		
		header[4] = (byte) (TimeStamp >> 24);
		header[5] = (byte) (TimeStamp >> 16);
		header[6] = (byte) (TimeStamp >> 8);
		header[7] = (byte) (TimeStamp & 0xFF);
		
		header[8] = (byte) (Ssrc >> 24);
		header[9] = (byte) (Ssrc >> 16);
		header[10] = (byte) (Ssrc >> 8);
		header[11] = (byte) (Ssrc & 0xFF);

		payloadSize = dataLength;
		payload = new byte[dataLength];

		for (int i = 0; i < dataLength; i++) {
			payload[i] = data[i];
		}
	}

	public void setMarker(boolean mark){
		if (mark){
			header[1] = (byte) (header[1] | 0x80);	
		}
		else{
			header[1] = (byte) (header[1] & 0x7F);
		}
	}
	
	/**
	 * Get Payload and store it in array data[]
	 * @param data
	 * @return int payload size
	 */
	public int getPayload(byte[] data) {
		
		for (int i = 0; i < payloadSize; i++){
			data[i] = payload[i];
		}
		return (payloadSize);
	}

	/**
	 * Return the length of payload in byte
	 * @return payload size
	 */
	public int getPayloadSize() {
		return (payloadSize);
	}

	/**
	 * Return the length of RTPPacket (HEADER_SIZE + Payload)
	 * @return length of RTPPacket   
	 */
	public int getLength() {
		return (payloadSize + HEADER_SIZE);
	}

	/**
	 * Fetch the packet into array of bytes
	 * @param packet
	 * @return packet size
	 */
	public int getBytes(byte[] packet) {
		
		//packet = header + payload
		for (int i = 0; i < HEADER_SIZE; i++)
			packet[i] = header[i];
		
		for (int j = 0; j < payloadSize; j++) {
			int k = j + 12;
			packet[k] = payload[j];
		}
		return (payloadSize + HEADER_SIZE);
	}

}