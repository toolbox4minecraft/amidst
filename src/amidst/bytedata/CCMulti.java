package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCMulti extends ClassChecker {
	private ClassChecker[] checks;

	public CCMulti(ClassChecker... checks) {
		super(checks[0].getName());
		this.checks = checks;
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		if (isMatching(mc, bClass)) {
			complete();
		}
	}

	private boolean isMatching(Minecraft mc, ByteClass bClass) {
		for (int i = 0; i < checks.length; i++) {
			if (!checks[i].isComplete()) {
				checks[i].check(mc, bClass);
				if (!checks[i].isComplete()) {
					return false;
				}
			}
		}
		return true;
	}
}
