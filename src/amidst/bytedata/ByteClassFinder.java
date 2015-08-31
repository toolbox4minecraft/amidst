package amidst.bytedata;

import amidst.bytedata.detect.ByteClassDetector;
import amidst.bytedata.prepare.ByteClassPreparer;
import amidst.minecraft.Minecraft;

public class ByteClassFinder {
	private String name;
	private ByteClassDetector detector;
	private ByteClassPreparer preparer;

	public ByteClassFinder(String name, ByteClassDetector detector,
			ByteClassPreparer preparer) {
		this.name = name;
		this.detector = detector;
		this.preparer = preparer;
	}

	public boolean find(Minecraft mc, ByteClass byteClass) {
		if (detector.detect(byteClass)) {
			preparer.prepare(byteClass);
			mc.registerClass(name, byteClass);
			return true;
		} else {
			return false;
		}
	}

	public String getName() {
		return name;
	}
}
