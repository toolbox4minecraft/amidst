package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;

public class NumberOfConstructorsRCD extends RealClassDetector {
	private int count;

	public NumberOfConstructorsRCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.getConstructorCount() == count;
	}
}
