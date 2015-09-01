package amidst.byteclass.finder.detect;

import amidst.byteclass.ByteClass;
import amidst.byteclass.ByteClass.Field;

public class FieldFlagsBCD extends ByteClassDetector {
	private int flags;
	private int[] fieldIndices;

	public FieldFlagsBCD(int flags, int... fieldIndices) {
		this.flags = flags;
		this.fieldIndices = fieldIndices;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		for (int fieldIndex : fieldIndices) {
			Field field = byteClass.getField(fieldIndex);
			if (!field.hasFlags(flags)) {
				return false;
			}
		}
		return true;
	}
}
