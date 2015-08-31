package amidst.bytedata.detect;

import amidst.bytedata.ByteClass;

public class LongBCD extends ByteClassDetector {
	private long[] longs;

	public LongBCD(long... longs) {
		this.longs = longs;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		for (long element : longs) {
			if (!byteClass.searchForLong(element)) {
				return false;
			}
		}
		return true;
	}
}
