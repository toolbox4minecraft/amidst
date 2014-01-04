package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCConstructorPreset extends ClassChecker {
	private String[] constructor;
	private boolean multiple;
	private int ID;
	private String name;
	public CCConstructorPreset(String name, String... constructor) {
		super(name);
		multiple = true;
		this.constructor = constructor;
	}
	public CCConstructorPreset(String name, int i, String construct) {
		super(name);
		multiple = false;
		ID = i;
		this.name = construct;
	}
	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		ByteClass clazz = mc.getByteClass(publicName);
		if (multiple) {
			for (int i = 0; i < constructor.length; i += 2) {
				clazz.addConstructor(constructor[i], constructor[i+1]);
			}
		} else {
			String args = clazz.getArguementsForConstructor(ID);
			clazz.addConstructor(args, name);
		}
		isComplete = true;
	}
}
