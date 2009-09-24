package rtsp.demo;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtspClient {

	private static Logger log = LoggerFactory.getLogger(RtspClient.class);
	// GUI
	// ----
	JFrame f = new JFrame("Client");
	JButton connectButton = new JButton("Connect");
	JButton disconnectButton = new JButton("Disconnect");
	JButton setupButton = new JButton("Setup");
	JButton playButton = new JButton("Play");
	JButton pauseButton = new JButton("Pause");
	JButton tearButton = new JButton("Teardown");
	
	JPanel mainPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JLabel iconLabel = new JLabel();
	ImageIcon icon;

	Timer timer; // timer used to receive data from the UDP socket
	
	// RTP variables:
	// ----------------
	DatagramPacket rcvdp; 		// UDP packet received from the server
	DatagramSocket RTPsocket; 	// socket to be used to send and receive UDP
								// packets
	static int RTP_RCV_PORT = 25000; // port where the client will receive the
										// RTP packets

	
	
	byte[] buf; // buffer used to store data received from the server

	// RTSP variables
	// ----------------
	// rtsp states
	static RtspState state; // RTSP state == INIT or READY or PLAYING
	
	Socket RTSPsocket; // socket used to send/receive RTSP messages
	// input and output stream filters
	static BufferedReader RTSPBufferedReader;
	static BufferedWriter RTSPBufferedWriter;
	static String VideoFileName; // video file to request to the server
	int RTSPSeqNb = 0; // Sequence number of RTSP messages within the session
	int RTSPid = 0; // ID of the RTSP session (given by the RTSP Server)

	final static String CRLF = "\r\n";

	// Video constants:
	// ------------------
	static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video

	// --------------------------
	// Constructor
	// --------------------------
	public RtspClient() {

		// build GUI
		// --------------------------

		// Frame
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Buttons
		buttonPanel.setLayout(new GridLayout(1, 0));
		buttonPanel.add(connectButton);
		buttonPanel.add(setupButton);
		buttonPanel.add(playButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(tearButton);
		
		connectButton.addActionListener(new connectButtonListener());
		setupButton.addActionListener(new setupButtonListener());
		playButton.addActionListener(new playButtonListener());
		pauseButton.addActionListener(new pauseButtonListener());
		tearButton.addActionListener(new tearButtonListener());

		// Image display label
		iconLabel.setIcon(null);

		// frame layout
		mainPanel.setLayout(null);
		mainPanel.add(iconLabel);
		mainPanel.add(buttonPanel);
		iconLabel.setBounds(0, 0, 380, 280);
		buttonPanel.setBounds(0, 280, 380, 50);

		f.getContentPane().add(mainPanel, BorderLayout.CENTER);
		f.setSize(new Dimension(390, 370));
		f.setVisible(true);

		// init timer
		// --------------------------
		timer = new Timer(20, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		// allocate enough memory for the buffer used to receive data from the
		// server
		buf = new byte[15000];
	}

	// ------------------------------------
	// main
	// ------------------------------------
	public static void main(String argv[]) throws Exception {
		// Create a Client object
		RtspClient theClient = new RtspClient();		

	}

	// ------------------------------------
	// Handler for buttons
	// ------------------------------------

	// .............
	// TO COMPLETE
	// .............

	private void connect() throws IOException {
		// get server RTSP port and IP address from the command line
		// ------------------
		// int RTSP_server_port = Integer.parseInt(argv[1]);
		int RTSP_server_port = 1234;
		String ServerHost = "localhost";
		InetAddress ServerIPAddr = InetAddress.getByName(ServerHost);

		// get video filename to request:
		VideoFileName = "test.mjpeg";

		// Establish a TCP connection with the server to exchange RTSP messages
		// ------------------
		this.RTSPsocket = new Socket(ServerIPAddr, RTSP_server_port);

		// Set input and output stream filters:
		RTSPBufferedReader = new BufferedReader(new InputStreamReader(
				this.RTSPsocket.getInputStream()));
		RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(
				this.RTSPsocket.getOutputStream()));

		// init RTSP state:
		state = RtspState.INIT;
	}

	class connectButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			log.info("Connect Button pressed !");

			try {
				connect();
			} catch (IOException e1) {
				log.info(e1.getMessage());
			}
		}
	}
	
	// Handler for Setup button
	// -----------------------
	class setupButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			log.info("Setup Button pressed !");

			if (state == RtspState.INIT) {
				// Init non-blocking RTPsocket that will be used to receive data
				try {
					// construct a new DatagramSocket to receive RTP packets
					// from the server, on port RTP_RCV_PORT
					// RTPsocket = ...
					RTPsocket = new DatagramSocket(RTP_RCV_PORT);

					// set TimeOut value of the socket to 5msec.
					// ....

					timer.setDelay(5);
				} catch (SocketException se) {
					log.info("Socket exception: " + se);
					System.exit(0);
				}

				// init RTSP sequence number
				RTSPSeqNb = 1;

				// Send SETUP message to the server
				send_RTSP_request(RtspRequest.SETUP);

				// Wait for the response
				if (parse_server_response() != 200)
					log.info("Invalid Server Response");
				else {
					// change RTSP state and print new state
					// state = ....
					state = RtspState.READY;
					log.info("New RTSP state: READY");
				}
			}// else if state != INIT then do nothing
		}
	}

	// Handler for Play button
	// -----------------------
	class playButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			log.info("Play Button pressed !");
			log.info("Current State = "+state);

			if (state == RtspState.READY) {
				// increase RTSP sequence number
				// .....
				RTSPSeqNb++;

				// Send PLAY message to the server
				send_RTSP_request(RtspRequest.PLAY);

				// Wait for the response
				if (parse_server_response() != 200)
					log.info("Invalid Server Response");
				else {
					// change RTSP state and print out new state
					// .....
					state = RtspState.PLAYING;
					log.info("New RTSP state: PLAYING");

					// start the timer
					timer.start();
				}
			}// else if state != READY then do nothing
		}
	}

	// Handler for Pause button
	// -----------------------
	class pauseButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// log.info("Pause Button pressed !");

			if (state == RtspState.PLAYING) {
				// increase RTSP sequence number
				// ........
				RTSPSeqNb++;

				// Send PAUSE message to the server
				send_RTSP_request(RtspRequest.PAUSE);

				// Wait for the response
				if (parse_server_response() != 200)
					log.info("Invalid Server Response");
				else {
					// change RTSP state and print out new state
					// ........
					state = RtspState.READY;
					log.info("New RTSP state: READY");

					// stop the timer
					timer.stop();
				}
			}
			// else if state != PLAYING then do nothing
		}
	}

	// Handler for Teardown button
	// -----------------------
	class tearButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			log.info("Teardown Button pressed !");

			// increase RTSP sequence number
			// ..........
			RTSPSeqNb++;

			// Send TEARDOWN message to the server
			send_RTSP_request(RtspRequest.TEARDOWN);

			// Wait for the response
			if (parse_server_response() != 200)
				log.info("Invalid Server Response");
			else {
				// change RTSP state and print out new state
				// ........
				state = RtspState.INIT;
				log.info("New RTSP state: INIT");

				// stop the timer
				timer.stop();

				// exit
				System.exit(0);
			}
		}
	}

	// ------------------------------------
	// Handler for timer
	// ------------------------------------

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// Construct a DatagramPacket to receive data from the UDP socket
			rcvdp = new DatagramPacket(buf, buf.length);

			try {
				// receive the DP from the socket:
				RTPsocket.receive(rcvdp);

				// create an RTPpacket object from the DP
				RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

				// print important header fields of the RTP packet received:
				log.info("Got RTP packet with SeqNum # "
						+ rtp_packet.getsequencenumber() + " TimeStamp "
						+ rtp_packet.gettimestamp() + " ms, of type "
						+ rtp_packet.getpayloadtype());

				// print header bitstream:
				rtp_packet.printheader();

				// get the payload bitstream from the RTPpacket object
				int payload_length = rtp_packet.getpayload_length();
				byte[] payload = new byte[payload_length];
				rtp_packet.getpayload(payload);

				// get an Image object from the payload bitstream
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Image image = toolkit.createImage(payload, 0, payload_length);

				// display the image as an ImageIcon object
				icon = new ImageIcon(image);
				iconLabel.setIcon(icon);
			} catch (InterruptedIOException iioe) {
				// log.info("Nothing to read");
			} catch (IOException ioe) {
				log.info("Exception caught: " + ioe);
			}
		}
	}

	// ------------------------------------
	// Parse Server Response
	// ------------------------------------
	private int parse_server_response() {
		int reply_code = 0;

		try {
			// parse status line and extract the reply_code:
			String StatusLine = RTSPBufferedReader.readLine();
			// log.info("RTSP Client - Received from Server:");
			log.info(StatusLine);

			StringTokenizer tokens = new StringTokenizer(StatusLine);
			tokens.nextToken(); // skip over the RTSP version
			reply_code = Integer.parseInt(tokens.nextToken());

			// if reply code is OK get and print the 2 other lines
			if (reply_code == 200) {
				String SeqNumLine = RTSPBufferedReader.readLine();
				log.info(SeqNumLine);

				String SessionLine = RTSPBufferedReader.readLine();
				log.info(SessionLine);

				// if state == INIT gets the Session Id from the SessionLine
				tokens = new StringTokenizer(SessionLine);
				tokens.nextToken(); // skip over the Session:
				RTSPid = Integer.parseInt(tokens.nextToken());
			}
		} catch (Exception ex) {
			log.info("Exception caught: " + ex);
			System.exit(0);
		}

		return (reply_code);
	}

	// ------------------------------------
	// Send RTSP Request
	// ------------------------------------

	// .............
	// TO COMPLETE
	// .............

	private void send_RTSP_request(RtspRequest request_type) {
		try {
			// Use the RTSPBufferedWriter to write to the RTSP socket

			// write the request line:
			String requestLine = request_type.toString()+" " + this.VideoFileName + " RTSP/1.0"+CRLF;
			RTSPBufferedWriter.write(requestLine);

			// write the CSeq line:
			String cseqLine = "CSeq: "+this.RTSPSeqNb + CRLF;
			RTSPBufferedWriter.write(cseqLine);

			// check if request_type is equal to "SETUP" and in this case write
			// the Transport: line advertising to the server the port used to
			// receive the RTP packets RTP_RCV_PORT
			// if ....
			// otherwise, write the Session line from the RTSPid field
			// else ....
			if (request_type == RtspRequest.SETUP){
				String transportLine = "Transport: RTP/UDP; client_port= "+this.RTP_RCV_PORT + CRLF;
				RTSPBufferedWriter.write(transportLine);
			}
			else{
				String sessionLine = "Session:"+RTSPid+ CRLF;
				RTSPBufferedWriter.write(sessionLine);
			}

			RTSPBufferedWriter.flush();
		} catch (Exception ex) {
			log.info("Exception caught: " + ex);
			System.exit(0);
		}
	}

}// end of Class Client
