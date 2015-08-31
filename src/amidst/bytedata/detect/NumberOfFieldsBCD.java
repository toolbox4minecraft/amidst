package amidst.bytedata.detect;

import amidst.bytedata.ByteClass;

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
