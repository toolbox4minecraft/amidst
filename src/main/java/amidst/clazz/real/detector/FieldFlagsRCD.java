package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.clazz.real.RealClassField;
import amidst.documentation.Immutable;

@Immutable
public class FieldFlagsRCD extends RealClassDetector {
	private final int flags;
	private final int[] fieldIndices;

	public FieldFlagsRCD(int flags, int... fieldIndices) {
		this.flags = flags;
		this.fieldIndices = fieldIndices;
	}

	@Override
	public boolean detect(RealClass realClass) {
		int fieldCount = realClass.getNumberOfFields();
		
		for (int fieldIndex : fieldIndices) {
			
			if (fieldIndex >= fieldCount) return false;
			
			RealClassField field = realClass.getField(fieldIndex);
			if (!field.hasFlags(flags)) {
				return false;
			}
		}
		return true;
	}
}
