package amidst.bytedata.detect;

import amidst.bytedata.ByteClass;

public class NumberOfConstructorsBCD extends ByteClassDetector {
	private int count;

	public NumberOfConstructorsBCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		return byteClass.getMethodAndConstructorCount() == count;
	}
}
