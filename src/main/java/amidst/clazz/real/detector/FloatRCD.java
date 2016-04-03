package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class FloatRCD extends RealClassDetector {
	private final float[] floats;

	public FloatRCD(float... floats) {
		this.floats = floats;
	}

	@Override
	public boolean detect(RealClass realClass) {
		for (float element : floats) {
			if (!realClass.searchForFloat(element)) {
				return false;
			}
		}
		return true;
	}
}
