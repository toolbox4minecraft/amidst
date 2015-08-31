package amidst.bytedata;

import amidst.bytedata.ByteClass.AccessFlags;
import amidst.bytedata.ByteClass.Field;
import amidst.minecraft.Minecraft;

public class CCJustAnother extends ClassChecker {
	public CCJustAnother(String name) {
		super(name);
	}

	@Override
	public boolean isMatching(ByteClass byteClass) {
		return matchNumberOfFields(byteClass, 3)
				&& matchFieldFlag(byteClass, AccessFlags.PRIVATE
						| AccessFlags.STATIC, 0, 1, 2)
				&& matchNumberOfConstructors(byteClass, 0)
				&& matchNumberOfMethodsAndConstructors(byteClass, 6)
				&& matchUtf8(byteClass, "isDebugEnabled");
	}

	private boolean matchUtf8(ByteClass byteClass, String string) {
		return byteClass.searchForUtf(string);
	}

	private boolean matchNumberOfMethodsAndConstructors(ByteClass byteClass,
			int count) {
		return byteClass.getMethodAndConstructorCount() == count;
	}

	private boolean matchNumberOfConstructors(ByteClass byteClass, int count) {
		return byteClass.getConstructorCount() == count;
	}

	private boolean matchFieldFlag(ByteClass byteClass, int flags,
			int... fieldIndices) {
		for (int fieldIndex : fieldIndices) {
			Field field = byteClass.getField(fieldIndex);
			if (!field.hasFlags(flags)) {
				return false;
			}
		}
		return true;
	}

	private boolean matchNumberOfFields(ByteClass byteClass, int count) {
		return byteClass.getFields().length == count;
	}

	@Override
	public void execute(Minecraft mc, ByteClass byteClass) {
		mc.registerClass(getName(), byteClass);
	}
}
