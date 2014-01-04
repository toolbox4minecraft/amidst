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
		boolean complete = true;
		for (int i = 0; i < checks.length; i++) {
			if (!checks[i].isComplete)
				checks[i].check(mc, bClass);
			complete &= checks[i].isComplete;
		}
		isComplete = complete;
	}

}
