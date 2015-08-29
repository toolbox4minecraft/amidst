package amidst.bytedata;

import amidst.bytedata.ByteClass.AccessFlags;
import amidst.minecraft.Minecraft;

public class CCJustAnother extends ClassChecker {
	public CCJustAnother(String name) {
		super(name);
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		if (bClass.getFields().length != 3) {
			return;
		}
		int privateStatic = AccessFlags.PRIVATE | AccessFlags.STATIC;
		for (int i = 0; i < 3; i++) {
			if ((bClass.getFields()[i].accessFlags & privateStatic) != privateStatic)
				return;
		}

		if ((bClass.getConstructorCount() == 0)
				&& (bClass.getMethodAndConstructorCount() == 6)
				&& (bClass.searchForUtf("isDebugEnabled"))) {
			mc.registerClass(getName(), bClass);
			complete();
		}
	}
}
