package amidst.byteclass.finder;

import amidst.byteclass.ByteClass;
import amidst.byteclass.finder.detect.ByteClassDetector;
import amidst.byteclass.finder.prepare.ByteClassPreparer;
import amidst.minecraft.local.Minecraft;

public class ByteClassFinder {
	public static BCFBuilder builder() {
		return BCFBuilder.builder();
	}

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
