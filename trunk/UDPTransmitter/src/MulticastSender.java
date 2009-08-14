//**************************************************************************
// Usage: java MulticastSender ALL-SYSTEMS.MCAST.NET 4000
//**************************************************************************

import java.net.*;
import java.io.*;

public class MulticastSender
{
	int packetSizeMax;
	byte[][] packets;
	int nextPacketIndex;

	public static void main( String[] argv )
	{

		if (argv.length<4){
			System.out.println("java -jar tx.jar <address> <port> <file.xml> <packet_size> <times>");
			return;
		}

		System.out.println("Sending '"+argv[2]+"' to "+argv[0]+ ":"+argv[1] + " "+ argv[4]+" times.");

		MulticastSender sender = new MulticastSender();
		try {
			sender.init(argv[0],argv[1],argv[2],argv[3],argv[4]);
			sender.send();
			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	int recvPort;
	InetAddress address;
	int countDown;
	private int sequenceNumber;
	long packetCount;
	int metaDataPayloadType;
	int timeStamp = 0;
	int transRate = 100;	//100 millisec

	public void init(String strAddress,String strPort, String strFile, String strPacketSize, String strTimes) throws IOException{
		// get the InetAddress of the MCAST group
		try {
			address = InetAddress.getByName( strAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// get the port that the MCAST group members will be listening on
		recvPort = Integer.parseInt( strPort );

		packetSizeMax=Integer.parseInt(strPacketSize);

		countDown=Integer.parseInt(strTimes);

		loadMetaData(strFile);
		//now we have a list of packets

		sequenceNumber = 0;
		metaDataPayloadType = 35;
		timeStamp = 0;
		transRate = 100;
	}

	public void send(){
		try
		{
			// create a multicast socket bound to any local port

			boolean finished=false;
			MulticastSocket ms = new MulticastSocket();
			while(!finished){
				byte[] packet = packets[nextPacketIndex];

				System.out.println(">> sending packet #"+nextPacketIndex+" = "+new String(packet));
				RTPPacket rtpPacket = new RTPPacket(metaDataPayloadType, sequenceNumber++, timeStamp,packet,packet.length);
				rtpPacket.setMarker(nextPacketIndex == 0);
				byte[] dgData = new byte[rtpPacket.getLength()];

				rtpPacket.getBytes(dgData);

				DatagramPacket dp = new DatagramPacket(dgData, rtpPacket.getLength(), address,recvPort);
				ms.setTimeToLive(1);
				ms.send(dp);

				try {
					Thread.sleep(transRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				nextPacketIndex++;
				if (nextPacketIndex==packetCount){
					//no more data, back to first packet
					nextPacketIndex = 0;

					//count down
					if (countDown<=1){
						finished=true;
					}
					countDown--;
				}
			}

			System.out.println("Leave the group ...");
			// tidy up - leave the group and close the socket
			ms.leaveGroup(address);

			System.out.println("Socket closing ...");
			ms.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private byte[] readFromFile(String filePath) throws IOException{

		File file = new File(filePath);

		InputStream is = new FileInputStream(file);

		long dataSize = file.length();
		System.out.println("File size:"+dataSize);

		if (dataSize > Integer.MAX_VALUE) {
			throw new IOException("File too big");
		}

		// Read in the bytes
		int offset = 0;
		int numRead = 0;

		byte[] bytes = new byte[(int) dataSize];	//1-dimension array of meta-data

		while (offset < dataSize
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < dataSize) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}
		is.close();

		return bytes;
	}

	/**
	 * Load meta-data from xml file to buffer.
	 * Make the buffer ready to be streamed.
	 * @throws IOException
	 */
	private void loadMetaData(String filePath) throws IOException {

		byte[] bytes = readFromFile(filePath);
		long dataSize;
		int lastPacketSize;


		// Get the size of the file
		dataSize = bytes.length;

		int remainder = (int) (dataSize % packetSizeMax);
		if (remainder == 0) {
			packetCount = dataSize / packetSizeMax;
			lastPacketSize = packetSizeMax;
		} else {
			packetCount = (int) Math.floor(dataSize / (1f * packetSizeMax)) + 1;
			lastPacketSize = remainder;
		}

		System.out.println("Packet count :"+packetCount);
		System.out.println("Packet Size :"+packetSizeMax);

		// Create the 2-dimension array to hold the data
		packets = new byte[(int) packetCount][packetSizeMax];

		//convert to array of packets
		for(int i=0;i<packetCount;i++){
			int currentPacketSize = packetSizeMax;

			if (i==(packetCount-1)){
				currentPacketSize = lastPacketSize;
			}

			for (int j=0;j<currentPacketSize;j++){
				packets[i][j] = bytes[i*packetSizeMax+j];
				//System.out.print(""+packets[i][j]);
			}

		}
		//System.out.println("\n");
		nextPacketIndex = 0;
	}
}