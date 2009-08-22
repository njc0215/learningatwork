package com.learningatwork.demo.shutdown.handler;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ShutdownTest {
	
	
	public static void main (String[] args){
		
		ShutdownSignalHandler.install("TERM");	//deal with "kill pid"
		ShutdownSignalHandler.install("INT");	//deal with "Ctrl+C"
		ShutdownSignalHandler.install("ABRT");

		System.out.println("Waiting for the TERM");

		while(true){
			
		}
	}
}
