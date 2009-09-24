package rtsp.demo;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoStream {

	FileInputStream fis; // video file
	int frame_nb; // current frame nb

	private static Logger log = LoggerFactory.getLogger(VideoStream.class);
	// -----------------------------------
	// constructor
	// -----------------------------------
	public VideoStream(String filename) throws Exception {

		log.info("load video asset:"+filename);
		
//		// init variables
//		fis = new FileInputStream(filename);
//		frame_nb = 0;
	}

	// -----------------------------------
	// getnextframe
	// returns the next frame as an array of byte and the size of the frame
	// -----------------------------------
	public int getnextframe(byte[] frame) throws Exception {
//		log.info("read next frame");
		int length = 0;
		String length_string;
		byte[] frame_length = new byte[5];

		// read current frame length
//		fis.read(frame_length, 0, 5);

		// transform frame_length to integer
//		length_string = new String(frame_length);
//		length = Integer.parseInt(length_string);

//		length = 10;
//		return (fis.read(frame, 0, length));		
		frame = "helloworld".getBytes();
		return frame.length;
	}
}
