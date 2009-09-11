package de.anhquan.demo.statemachine;

public class TestRtspStateMachine {

    private StopWatch stopWatch;

    public TestRtspStateMachine(){
	stopWatch = new StopWatch();
	System.out.println("[TestStopWatch] state:"+stopWatch.getCurrentState());
    }
    
    public void startWatch(){
	stopWatch.fireEvent(StopWatch.EVENT_START);
    }
    
    public static void main(String[] args) {

	RtspStateMachine t = new RtspStateMachine();
	t.reset();
	t.fireEvent(RtspStateMachine.EVENT_PLAY_RESPONSE_RECEIVED);
	t.fireEvent(RtspStateMachine.EVENT_SETUP_RESPONSE_RECEIVED);
    }
}
