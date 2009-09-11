package de.anhquan.demo.statemachine;

import java.util.Set;

import org.apache.commons.scxml.env.AbstractStateMachine;

public class StopWatch extends AbstractStateMachine {
    public static final String EVENT_START = "watch.start", EVENT_STOP = "watch.stop", EVENT_SPLIT = "watch.split", EVENT_UNSPLIT = "watch.unsplit", EVENT_RESET = "watch.reset";

    public StopWatch() {
	super(StopWatch.class.getClassLoader().getResource("stopwatch.xml"));
	System.out.println("[StopWatch.constructor] state:"+this.getCurrentState());
    }

    public void reset() {
	System.out.println("reset");
    }

    public void stopped() {
	System.out.println("stopped");
    }

    public void running() {
	System.out.println("running");
    }

    public void paused() {
	System.out.println("paused");
    }

    public String getCurrentState() {
	Set states = getEngine().getCurrentStatus().getStates();
	return ((org.apache.commons.scxml.model.State) states.iterator().next()).getId();
    }
}