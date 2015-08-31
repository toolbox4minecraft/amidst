package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCLong extends ClassChecker {
	private long[] checkData;

	public CCLong(String name, long... checkData) {
		super(name);
		this.checkData = checkData;
	}

	@Override
	public boolean isMatching(ByteClass byteClass) {
		for (long element : checkData) {
			if (!byteClass.searchForLong(element)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void execute(Minecraft mc, ByteClass byteClass) {
		mc.registerClass(getName(), byteClass);
	}
}
