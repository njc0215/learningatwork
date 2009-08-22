package com.learningatwork.demo.shutdown.handler;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ShutdownSignalHandler implements SignalHandler{

	private SignalHandler oldHandler;

    // Static method to install the signal handler
    public static ShutdownSignalHandler install(String signalName) {
        Signal diagSignal = new Signal(signalName);
        ShutdownSignalHandler diagHandler = new ShutdownSignalHandler();
        diagHandler.oldHandler = Signal.handle(diagSignal,diagHandler);
        return diagHandler;
    }

    // Signal handler method
    public void handle(Signal sig) {
        System.out.println("Diagnostic Signal handler called for signal "+sig);
        try {
            // Output information for each thread
            Thread[] threadArray = new Thread[Thread.activeCount()];
            int numThreads = Thread.enumerate(threadArray);
            System.out.println("Current threads:");
            for (int i=0; i < numThreads; i++) {
                System.out.println("    "+threadArray[i]);
            }
            
            // Chain back to previous handler, if one exists
            if ( oldHandler != SIG_DFL && oldHandler != SIG_IGN ) {
                oldHandler.handle(sig);
            }
            
        } catch (Exception e) {
            System.out.println("Signal handler failed, reason "+e);
        }
    }
}
