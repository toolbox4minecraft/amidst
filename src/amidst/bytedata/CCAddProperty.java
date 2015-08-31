package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCAddProperty extends NameLessAcceptingClassChecker {
	private String property;
	private String propertyName;

	public CCAddProperty(String property, String propertyName) {
		this.property = property;
		this.propertyName = propertyName;
	}

	@Override
	public void execute(Minecraft mc, ByteClass byteClass) {
		byteClass.addProperty(property, propertyName);
	}
}