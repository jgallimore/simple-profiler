package uk.me.jrg.simpleprofiler;

public class ClassModificationDemo {

	private int version;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		try {
			Recorder.getInstance().methodStarted("ClassModificationDemo", "toString");
			return "ClassCreationDemo: " + version;
		} finally {
			Recorder.getInstance().methodComplete("ClassModificationDemo", "toString");
		}
	}

	public static void main(String[] args) {
		System.out.println(new ClassModificationDemo());

	}

}
