package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCProperty extends ClassChecker {
	private String[] properties;

	public CCProperty(String name, String... properties) {
		super(name);
		this.properties = properties;
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		ByteClass clazz = mc.getByteClass(getName());
		for (int i = 0; i < properties.length; i += 2) {
			clazz.addProperty(properties[i], properties[i + 1]);
		}
		complete();
	}
}