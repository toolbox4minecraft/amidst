package amidst.bytedata;

import amidst.bytedata.detect.ByteClassDetector;
import amidst.bytedata.prepare.ByteClassPreparer;
import amidst.minecraft.Minecraft;

public class ByteClassFinder {
	private String minecraftClassName;
	private ByteClassDetector detector;
	private ByteClassPreparer preparer;

	public ByteClassFinder(String minecraftClassName,
			ByteClassDetector detector, ByteClassPreparer preparer) {
		this.minecraftClassName = minecraftClassName;
		this.detector = detector;
		this.preparer = preparer;
	}

	public boolean find(Minecraft minecraft, ByteClass byteClass) {
		if (detector.detect(byteClass)) {
			preparer.prepare(byteClass);
			minecraft.registerClass(minecraftClassName, byteClass);
			return true;
		} else {
			return false;
		}
	}

	public String getMinecraftClassName() {
		return minecraftClassName;
	}
}
