package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCStringMatch extends ClassChecker {
	private String checkData;

	public CCStringMatch(String name, String checkData) {
		super(name);
		this.checkData = checkData;
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		if (bClass.searchForString(checkData)) {
			mc.registerClass(getName(), bClass);
			complete();
		}
	}
}
