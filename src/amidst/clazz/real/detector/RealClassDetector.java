package amidst.clazz.real.detector;

import java.util.List;

import amidst.clazz.real.RealClass;

public abstract class RealClassDetector {
	public RealClass firstMatching(List<RealClass> realClasses) {
		for (RealClass realClass : realClasses) {
			if (detect(realClass)) {
				return realClass;
			}
		}
		return null;
	}

	public abstract boolean detect(RealClass realClass);
}
