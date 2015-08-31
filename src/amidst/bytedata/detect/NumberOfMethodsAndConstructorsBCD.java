package amidst.bytedata.detect;

import amidst.bytedata.ByteClass;

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
