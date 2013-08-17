package amidst.bytedata;

import com.skiphs.AMIDST.minecraft.Minecraft;

public class CCRequireSimple extends ClassChecker {
	private ClassChecker checker;
	public CCRequireSimple(ClassChecker checker) {
		this.checker = checker;
	}
	public boolean canPass(Minecraft mc, ByteClass bClass) {
		
		return true;
	}
	public void check(Minecraft mc, ByteClass bClass) {
		if (canPass(mc, bClass)) {
			checker.check(mc, bClass);
		}
		isComplete = checker.isComplete;
	}
}
