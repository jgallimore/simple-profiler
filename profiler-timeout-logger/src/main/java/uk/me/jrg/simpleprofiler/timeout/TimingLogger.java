package uk.me.jrg.simpleprofiler.timeout;

import org.apache.log4j.Logger;

import uk.me.jrg.simpleprofiler.Callback;

public class TimingLogger implements Callback {
	
	private Logger logger = Logger.getLogger(TimingLogger.class);
	private long startTime;

	public void methodStarted(String className, String method) {
		startTime = System.nanoTime();
	}

	public void methodCompleted(String className, String method) {
		long timeTaken = System.nanoTime() - startTime;
		double ms = (double) timeTaken / 1000000.0;
		
		long targetMs = getTargetMs();

		if (ms > targetMs) {
			logger.error("[AGENT] method " + className + "." + method + " completed in " + ms + " ms");
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
