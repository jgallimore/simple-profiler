package uk.me.jrg.simpleprofiler;


public class TimingLogger implements Callback {
	
	private long startTime;

	public void methodStarted(String className, String method) {
		startTime = System.nanoTime();
	}

	public void methodCompleted(String className, String method) {
		long timeTaken = System.nanoTime() - startTime;
		double ms = (double) timeTaken / 1000000.0;
		
		long targetMs = getTargetMs();

		if (ms > targetMs) {
			System.out.println("[AGENT] method " + className + "." + method + " completed in " + ms + " ms");
		}
	}

	private long getTargetMs() {
		long profilerTimeout = 5000;
		String profilerTimeoutStr = System.getProperty("profiler.timeout", "5000");

		try {
			profilerTimeout = Long.parseLong(profilerTimeoutStr);
		} catch (Throwable t) {
		}
		
		return profilerTimeout;
	}

}
