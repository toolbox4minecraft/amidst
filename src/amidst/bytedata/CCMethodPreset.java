package amidst.bytedata;

import amidst.minecraft.Minecraft;


public class CCMethodPreset extends ClassChecker {
	private String[] methods;
	public CCMethodPreset(String name, String... methods) {
		super(name);
		this.methods = methods;
	}
	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		ByteClass clazz = mc.getByteClass(publicName);
		for (int i = 0; i < methods.length; i += 2) {
			clazz.addMethod(methods[i], methods[i+1]);
		}
		isComplete = true;
	}
}
