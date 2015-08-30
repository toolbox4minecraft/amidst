package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCLong extends ClassChecker {
	private long[] checkData;

	public CCLong(String name, long... checkData) {
		super(name);
		this.checkData = checkData;
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		if (isMatching(bClass)) {
			mc.registerClass(getName(), bClass);
			complete();
		}
	}

	private boolean isMatching(ByteClass bClass) {
		for (long element : checkData) {
			if (!bClass.searchForLong(element)) {
				return false;
			}
		}
		return true;
	}
}
