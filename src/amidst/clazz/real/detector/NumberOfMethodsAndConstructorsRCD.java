package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;

public class NumberOfMethodsAndConstructorsRCD extends RealClassDetector {
	private int count;

	public NumberOfMethodsAndConstructorsRCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.getMethodAndConstructorCount() == count;
	}
}
