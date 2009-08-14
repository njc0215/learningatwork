
 import java.net.*;
import java.io.*;

// java MulticastSnooper ALL-SYSTEMS.MCAST.NET 4000
public class MulticastReceiver
{
	public static void main( String[] argv )
	{
		try
		{
			System.out.println("Number of argv = "+argv.length);
			if (argv.length<2){
				System.out.println("java -jar rx.jar <address> <port>");
				return;
			}

			// get the InetAddress of the MCAST group
			InetAddress ia = InetAddress.getByName( argv[0] );

			// get the port that we will be listening on
			int port = Integer.parseInt( argv[1]);

			// create a multicast socket on the specified local port number
			MulticastSocket ms = new MulticastSocket( port );

			// create an empty datagram packet
			DatagramPacket dp = new DatagramPacket(new byte[128], 128);

			//Join a multicast group and wait for some action
			ms.joinGroup(ia);
			System.out.println( "waiting for a packet from "+ia+"...");
			ms.receive(dp);

			// print out what we received and quit
			System.out.println( new String(dp.getData() ));

			ms.leaveGroup(ia);
			ms.close();
		}
		catch (IOException e) {

			System.out.println( e.getLocalizedMessage());
		}
	}
}



