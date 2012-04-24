package uk.me.jrg.simpleprofiler;

import java.lang.instrument.Instrumentation;

public class Agent {

    private static Instrumentation instrumentation;
    private static String callbackClass = TimingLoggingCallback.class.getName();
    private static String[] regexs;

    /**
     * JVM hook to statically load the javaagent at startup.
     *
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called. Then the real application main method will be called.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void premain(String args, Instrumentation inst) throws Exception {
    	processArgs(args);
        instrumentation = inst;
        instrumentation.addTransformer(new ClassInstrumenter(regexs));
    }

    /**
     * JVM hook to dynamically load javaagent at runtime.
     *
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
    	processArgs(args);
    	instrumentation = inst;
        instrumentation.addTransformer(new ClassInstrumenter(regexs));
    }

    /**
     * Parses the argument supplied to the javaagent to work out the class
     * to use for the callback, and the regex for the classes to instrument
     * 
     * The argument is in the format:
     * 	[callback-class;]regex1[,regex2,regex3...]
     * 
     * @param args Arguments supplied to javaagent
     */
    private static void processArgs(String args) {
    	if (args == null || args.length() == 0) {
    		return;
    	}
    	
    	String regex = args;
    	
    	if (args.contains(";")) {
    		int pos = args.indexOf(";");
    		callbackClass = args.substring(0, pos);
    		regex = args.substring(pos + 1);
    	}
    	
    	regexs = regex.split(",");
    }

	public static String getCallbackClass() {
		return callbackClass;
	}
}