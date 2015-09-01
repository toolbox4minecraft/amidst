package amidst.clazz.real.finder.detect;

import amidst.clazz.real.ByteClass;

public class NumberOfFieldsBCD extends ByteClassDetector {
	private int count;

	public NumberOfFieldsBCD(int count) {
		this.count = count;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		return byteClass.getFields().length == count;
	}
}
