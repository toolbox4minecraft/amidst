package amidst.clazz.real.finder.detect;

import amidst.clazz.real.RealClass;

public class LongRCD extends RealClassDetector {
	private long[] longs;

	public LongRCD(long... longs) {
		this.longs = longs;
	}

	@Override
	public boolean detect(RealClass byteClass) {
		for (long element : longs) {
			if (!byteClass.searchForLong(element)) {
				return false;
			}
		}
		return true;
	}
}
