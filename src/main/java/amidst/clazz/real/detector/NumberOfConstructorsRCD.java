package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class NumberOfConstructorsRCD extends RealClassDetector {
	private final int count;

	public NumberOfConstructorsRCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.getNumberOfConstructors() == count;
	}
}
