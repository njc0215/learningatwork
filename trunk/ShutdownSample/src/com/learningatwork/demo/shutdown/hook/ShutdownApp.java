package com.learningatwork.demo.shutdown.hook;

/**
 * This project demonstrates how to shutdown a Java program.
 * In this approach, it does not work properly with "kill pid" on Linux.
 * It does not either work with terminating a Thread on Windows Task Manager
 * @author nguyen.anhquan@gmail.com
 *  
 */
public class ShutdownApp implements IShutdownable {

	public void shutdown(){
		System.out.println("Cleaning up ...");
		System.out.println("Shut down!");
	}
	
	public void startup() {
		System.out.println("Starting Demo ...");
		ShutdownHook shutdownHook = new ShutdownHook(this);
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		
		Thread myThread = new MyThread();
		myThread.run();
		
		System.out.println("Ctrl+C to quit");
	}
	
	private class MyThread extends Thread{
		public void run (){
			while(true){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(".");
			}
		}
	}

	public static void main(String[] args){
		
		ShutdownApp app = new ShutdownApp ();
		app.startup();
		

	}
}
