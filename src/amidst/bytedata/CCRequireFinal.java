package amidst.bytedata;

import amidst.minecraft.Minecraft;


public class CCRequireFinal extends CCRequireSimple {
	public CCRequireFinal(ClassChecker checker) {
		super(checker);
	}

	@Override
	public boolean canPass(Minecraft mc, ByteClass bClass) {
		return bClass.isFinal();
	}
}
