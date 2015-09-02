package amidst.clazz.real.finder;

import java.util.List;

import amidst.clazz.real.RealClass;
import amidst.clazz.real.finder.detect.RealClassDetector;
import amidst.clazz.real.finder.prepare.RealClassPreparer;

public class RealClassFinder {
	public static RCFBuilder builder() {
		return RCFBuilder.builder();
	}

	private String symbolicClassName;
	private RealClassDetector detector;
	private RealClassPreparer preparer;

	public RealClassFinder(String symbolicClassName,
			RealClassDetector detector, RealClassPreparer preparer) {
		this.symbolicClassName = symbolicClassName;
		this.detector = detector;
		this.preparer = preparer;
	}

	public boolean find(RealClass byteClass) {
		if (detector.detect(byteClass)) {
			preparer.prepare(byteClass);
			return true;
		} else {
			return false;
		}
	}

	public RealClass find(List<RealClass> byteClasses) {
		for (RealClass byteClass : byteClasses) {
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
