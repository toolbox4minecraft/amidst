package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCRequire extends ClassChecker {
	private ClassChecker checker;
	private String[] requiredNames;

	public CCRequire(ClassChecker checker, String... requiredNames) {
		super(checker.getName());
		this.checker = checker;
		this.requiredNames = requiredNames;
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		if (hasAllRequiredClasses(mc)) {
			checker.check(mc, bClass);
			if (checker.isComplete()) {
				complete();
			}
		}
	}

	private boolean hasAllRequiredClasses(Minecraft mc) {
		for (String requiredName : requiredNames) {
			if (mc.getByteClass(requiredName) == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "[Require " + requiredNames[0] + " " + checker + "]";
	}
}
