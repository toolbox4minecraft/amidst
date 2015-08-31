package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCString extends ClassChecker {
	private String checkData;

	public CCString(String name, String checkData) {
		super(name);
		this.checkData = checkData;
	}

	@Override
	public boolean isMatching(ByteClass byteClass) {
		return byteClass.searchForString(checkData);
	}

	@Override
	public void execute(Minecraft mc, ByteClass byteClass) {
		mc.registerClass(getName(), byteClass);
	}
}
