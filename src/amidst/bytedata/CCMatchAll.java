package amidst.bytedata;

import java.util.List;

import amidst.minecraft.Minecraft;

public class CCMatchAll extends ClassChecker {
	private List<ClassChecker> checkers;

	public CCMatchAll(List<ClassChecker> checkers) {
		super(null);
		this.checkers = checkers;
	}

	@Override
	public boolean isMatching(ByteClass byteClass) {
		for (ClassChecker checker : checkers) {
			if (!checker.isMatching(byteClass)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void execute(Minecraft mc, ByteClass byteClass) {
		for (ClassChecker checker : checkers) {
			checker.execute(mc, byteClass);
		}
	}
}
