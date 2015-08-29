package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCRequireSimple extends ClassChecker {
	private ClassChecker checker;

	public CCRequireSimple(ClassChecker checker) {
		this.checker = checker;
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		checker.check(mc, bClass);
		if (checker.isComplete()) {
			complete();
		}
	}
}
