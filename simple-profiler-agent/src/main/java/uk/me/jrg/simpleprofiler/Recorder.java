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
		Callback callback = createNewCallbackInstance();
		
		Stack<Callback> stack = context.get();
		if (stack == null) {
			stack = new Stack<Callback>();
		}
		
		stack.push(callback);
		context.set(stack);
		
		if (callback != null) {
			callback.methodStarted(className, method);	
		}
	}
	
	private Callback createNewCallbackInstance() {
		try {
			Class<?> cls = Class.forName(Agent.getCallbackClass());
			Object instance = cls.newInstance();
			
			return (Callback) instance;
		} catch (Throwable e) {
			return null;
		}
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
