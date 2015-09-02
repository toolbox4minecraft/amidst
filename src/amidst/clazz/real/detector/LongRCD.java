package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;

public class LongRCD extends RealClassDetector {
	private long[] longs;

	public LongRCD(long... longs) {
		this.longs = longs;
	}

	@Override
	public boolean detect(RealClass realClass) {
		for (long element : longs) {
			if (!realClass.searchForLong(element)) {
				return false;
			}
		}
		return true;
	}
}
