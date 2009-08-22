package com.learningatwork.demo.shutdown.hook;

public class ShutdownHook extends Thread{

	IShutdownable owner;
	
	public ShutdownHook (IShutdownable owner){
		this.owner = owner;
	}
	
	public void run(){
		owner.shutdown();
	}
}
