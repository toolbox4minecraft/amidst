package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class NumberOfFieldsRCD extends RealClassDetector {
	private final int count;

	public NumberOfFieldsRCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.getNumberOfFields() == count;
	}
}
