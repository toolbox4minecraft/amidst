package amidst.clazz.real.finder.detect;

import amidst.clazz.real.ByteClass;

public class NumberOfMethodsAndConstructorsBCD extends ByteClassDetector {
	private int count;

	public NumberOfMethodsAndConstructorsBCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		return byteClass.getMethodAndConstructorCount() == count;
	}
}
