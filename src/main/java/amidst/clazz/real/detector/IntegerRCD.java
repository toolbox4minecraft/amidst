package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class IntegerRCD extends RealClassDetector {
	private final int[] ints;

	public IntegerRCD(int... ints) {
		this.ints = ints;
	}

	@Override
	public boolean detect(RealClass realClass) {
		for (int element : ints) {
			if (!realClass.searchForInteger(element)) {
				return false;
			}
		}
		return true;
	}
}
