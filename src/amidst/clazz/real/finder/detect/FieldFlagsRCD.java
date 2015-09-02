package amidst.clazz.real.finder.detect;

import amidst.clazz.real.RealClass;
import amidst.clazz.real.RealClass.Field;

public class FieldFlagsRCD extends RealClassDetector {
	private int flags;
	private int[] fieldIndices;

	public FieldFlagsRCD(int flags, int... fieldIndices) {
		this.flags = flags;
		this.fieldIndices = fieldIndices;
	}

	@Override
	public boolean detect(RealClass byteClass) {
		for (int fieldIndex : fieldIndices) {
			Field field = byteClass.getField(fieldIndex);
			if (!field.hasFlags(flags)) {
				return false;
			}
		}
		return true;
	}
}
