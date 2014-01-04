package amidst.bytedata;

import amidst.minecraft.Minecraft;


public class CCRequireAccessFlags extends CCRequireSimple {
	private int flags;
	public CCRequireAccessFlags(ClassChecker checker, int flags) {
		super(checker);
		this.flags = flags;
	}
	@Override
	public boolean canPass(Minecraft mc, ByteClass bClass) {
		return bClass.accessFlags == flags;
	}
}
