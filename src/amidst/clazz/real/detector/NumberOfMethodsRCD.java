package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;

public class NumberOfMethodsRCD extends RealClassDetector {
	private int count;

	public NumberOfMethodsRCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.getNumberOfMethods() == count;
	}
}
