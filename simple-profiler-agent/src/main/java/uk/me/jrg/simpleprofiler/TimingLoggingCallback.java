package uk.me.jrg.simpleprofiler;

public class TimingLoggingCallback implements Callback {
	
	private long startTime;
	
	public void methodStarted(String className, String method) {
		startTime = System.nanoTime();
	}

	public void methodCompleted(String className, String method) {
		long timeTaken = System.nanoTime() - startTime;
		double ms = (double) timeTaken / 1000000.0;
		
		System.out.println("[AGENT] method " + className + "." + method + " completed in " + ms + " ms");
	}

}
