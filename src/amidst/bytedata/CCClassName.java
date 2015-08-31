package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCClassName extends ClassChecker {
	private ClassChecker checker;

	public CCClassName(String className, ClassChecker checker) {
		super(className);
		this.checker = checker;
	}

	@Override
	public boolean isMatching(ByteClass byteClass) {
		return getName().equals(byteClass.getClassName())
				&& checker.isMatching(byteClass);
	}

	@Override
	public void execute(Minecraft mc, ByteClass byteClass) {
		checker.execute(mc, byteClass);
	}
}
