package amidst.clazz.real.finder.detect;

import amidst.clazz.real.RealClass;

public class NumberOfMethodsAndConstructorsRCD extends RealClassDetector {
	private int count;

	public NumberOfMethodsAndConstructorsRCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(RealClass byteClass) {
		return byteClass.getMethodAndConstructorCount() == count;
	}
}
