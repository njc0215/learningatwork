package com.anhquan.demo.rtsp.core;

import com.anhquan.demo.rtsp.core.state.RtspState;

public class RtspStateMachine {

	RtspState currentState;
	boolean done;
	
	public synchronized RtspState getState(){
		return currentState;
	}
	
	public synchronized void setState(RtspState newState){
		currentState = newState;
	}
	
}
