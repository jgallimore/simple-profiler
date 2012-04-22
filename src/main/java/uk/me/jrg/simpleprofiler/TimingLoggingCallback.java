package uk.me.jrg.simpleprofiler;

public class TimingLoggingCallback implements Callback {
	
	public void methodStarted(String className, String method) {
		System.out.println("Method " + className + "." + method + " started");
	}

	public void methodCompleted(String className, String method) {
		System.out.println("Method " + className + "." + method + " completed");
	}

}
