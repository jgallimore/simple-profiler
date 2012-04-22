package uk.me.jrg.simpleprofiler;

public interface Callback {
	void methodStarted(String className, String method);
	void methodCompleted(String className, String method);
}
