package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCString extends ClassChecker {
	private String checkData;

	public CCString(String name, String checkData) {
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
