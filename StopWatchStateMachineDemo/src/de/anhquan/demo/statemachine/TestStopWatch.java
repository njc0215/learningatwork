package de.anhquan.demo.statemachine;

public class TestStopWatch {

    private StopWatch stopWatch;

    public TestStopWatch(){
	stopWatch = new StopWatch();
	System.out.println("[TestStopWatch] state:"+stopWatch.getCurrentState());
    }
    
    public void startWatch(){
	stopWatch.fireEvent(StopWatch.EVENT_START);
    }
    
    public static void main(String[] args) {
	TestStopWatch test = new TestStopWatch();
	test.startWatch();

    }
}
