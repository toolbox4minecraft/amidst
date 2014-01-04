package amidst.bytedata;

import amidst.minecraft.Minecraft;


public class CCPropertyPreset extends ClassChecker {
	private String[] properties;
	public CCPropertyPreset(String name, String... properties) {
		super(name);
		this.properties = properties;
	}
	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		ByteClass clazz = mc.getByteClass(publicName);
		for (int i = 0; i < properties.length; i += 2) {
			clazz.addProperty(properties[i], properties[i+1]);
		}
		isComplete = true;
	}
}