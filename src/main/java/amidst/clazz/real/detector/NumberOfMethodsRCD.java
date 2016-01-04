package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class NumberOfMethodsRCD extends RealClassDetector {
	private final int count;

	public NumberOfMethodsRCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.getNumberOfMethods() == count;
	}
}
