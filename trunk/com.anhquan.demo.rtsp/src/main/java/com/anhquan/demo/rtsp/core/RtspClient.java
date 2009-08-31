package com.anhquan.demo.rtsp.core;

import com.anhquan.demo.rtsp.core.state.RtspState;

public class RtspClient extends RtspStateMachine{

	public void run(){
		while (!done){
			RtspState nextState = currentState.processRequest();
			currentState = nextState;
		}
	}
}
