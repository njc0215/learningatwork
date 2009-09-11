package de.anhquan.demo.statemachine;

import java.util.Set;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.env.AbstractStateMachine;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

public class RtspStateMachine extends AbstractStateMachine implements SCXMLListener {

    public static final String EVENT_SETUP_RESPONSE_RECEIVED = "setup.response.received";
    public static final String EVENT_TEARDOWN_RESPONSE_RECEIVED = "teardown.response.received";
    public static final String EVENT_RECORD_RESPONSE_RECEIVED = "record.response.received";
    public static final String EVENT_PLAY_RESPONSE_RECEIVED = "play.response.received";
    public static final String EVENT_PAUSE_RESPONSE_RECEIVED = "pause.response.received";
    public static final String EVENT_REQUEST_TIMEOUT = "request.timeout";

    public RtspStateMachine() {
	super(StopWatch.class.getClassLoader().getResource("rtsp-server.xml"));
    }
    
    public void reset(){
	this.getEngine().addListener(this.getEngine().getStateMachine(), this);
	//executeThreadToListenToTheSocket();
    }
    
    //state
    public void init(){
	System.out.println("state:init");
    }

    //state
    public void ready(){
	System.out.println("state:ready");
    }
    
    //state
    public void playing(){
	System.out.println("state:playing");
    }
    
    //state
    public void recording(){
	System.out.println("state:recording");
    }
    
    public String getCurrentState() {
	Set states = getEngine().getCurrentStatus().getStates();
	return ((org.apache.commons.scxml.model.State) states.iterator().next()).getId();
    }

    //SCXMLListener implementation    
    @Override
    public void onEntry(TransitionTarget target) {
	System.out.println("onEntry:"+target.getId());
    }

    @Override
    public void onExit(TransitionTarget target) {
	System.out.println("onExit:"+target.getId());
    }

    @Override
    public void onTransition(TransitionTarget fromTarget, TransitionTarget toTarget, Transition transition) {
	System.out.println("onTransition: from:"+fromTarget.getId()+" to "+toTarget.getId()+ " transition:"+transition.getCond());
    }
}
