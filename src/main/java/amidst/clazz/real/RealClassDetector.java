package amidst.clazz.real;

import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;

@Immutable
@FunctionalInterface
public interface RealClassDetector {
	public default RealClass firstMatching(List<RealClass> realClasses) {
		for (RealClass realClass : realClasses) {
			if (detect(realClass)) {
				return realClass;
			}
		}
		return null;
	}

	public default List<RealClass> allMatching(List<RealClass> realClasses) {
		List<RealClass> result = new ArrayList<RealClass>();
		for (RealClass realClass : realClasses) {
			if (detect(realClass)) {
				result.add(realClass);
			}
		}
		return result;
	}

	public boolean detect(RealClass realClass);
}
