package uk.me.jrg.simpleprofiler;

import java.util.Stack;

public class Recorder {
	private static Recorder instance = new Recorder();
	private static ThreadLocal<Stack<Callback>> context = new ThreadLocal<Stack<Callback>>();
	
	// prevent direct instantiation
	private Recorder() {
		
	}
	
	public static Recorder getInstance() {
		return instance;
	}
	
	public void methodStarted(String className, String method) {
		Callback callback = new TimingLoggingCallback();
		Stack<Callback> stack = context.get();
		if (stack == null) {
			stack = new Stack<Callback>();
		}
		
		stack.push(callback);
		context.set(stack);
		callback.methodStarted(className, method);
	}
	
	public void methodComplete(String className, String method) {
		Stack<Callback> stack = context.get();
		if (stack == null) {
			return;
		}
		
		Callback callback = stack.pop();
		if (callback == null) {
			return;
		}
		
		callback.methodCompleted(className, method);
	}
}
