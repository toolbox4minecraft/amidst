package amidst.clazz.real.finder;

import java.util.List;

import amidst.clazz.real.ByteClass;
import amidst.clazz.real.finder.detect.ByteClassDetector;
import amidst.clazz.real.finder.prepare.ByteClassPreparer;

public class ByteClassFinder {
	public static BCFBuilder builder() {
		return BCFBuilder.builder();
	}

	private String symbolicClassName;
	private ByteClassDetector detector;
	private ByteClassPreparer preparer;

	public ByteClassFinder(String symbolicClassName,
			ByteClassDetector detector, ByteClassPreparer preparer) {
		this.symbolicClassName = symbolicClassName;
		this.detector = detector;
		this.preparer = preparer;
	}

	public boolean find(ByteClass byteClass) {
		if (detector.detect(byteClass)) {
			preparer.prepare(byteClass);
			return true;
		} else {
			return false;
		}
	}

	public ByteClass find(List<ByteClass> byteClasses) {
		for (ByteClass byteClass : byteClasses) {
			if (find(byteClass)) {
				return byteClass;
			}
		}
		return null;
	}

	public String getSymbolicClassName() {
		return symbolicClassName;
	}
}
