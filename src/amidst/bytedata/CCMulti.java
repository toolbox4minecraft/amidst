package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCMulti extends ClassChecker {
	private ClassChecker[] checkers;

	public CCMulti(ClassChecker... checkers) {
		super(checkers[0].getName());
		this.checkers = checkers;
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		if (isMatching(mc, bClass)) {
			complete();
		}
	}

	private boolean isMatching(Minecraft mc, ByteClass bClass) {
		for (int i = 0; i < checkers.length; i++) {
			if (!checkers[i].isComplete()) {
				checkers[i].check(mc, bClass);
				if (!checkers[i].isComplete()) {
					return false;
				}
			}
		}
		return true;
	}
}
