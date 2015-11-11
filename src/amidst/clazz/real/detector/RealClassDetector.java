package amidst.clazz.real.detector;

import java.util.ArrayList;
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

	public List<RealClass> allMatching(List<RealClass> realClasses) {
		List<RealClass> result = new ArrayList<RealClass>();
		for (RealClass realClass : realClasses) {
			if (detect(realClass)) {
				result.add(realClass);
			}
		}
		return result;
	}

	public abstract boolean detect(RealClass realClass);
}
