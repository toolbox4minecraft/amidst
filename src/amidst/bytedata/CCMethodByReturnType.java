package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCMethodByReturnType extends ClassChecker {
	private String name, className, returnType, param;
	public CCMethodByReturnType(String className, String returnType, String param, String name) {
		this.name = name;
		this.className = className;
		this.param = param;
		this.returnType = returnType;
	}
	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		ByteClass clazz = mc.getByteClass(className);
		String internalName = mc.getByteClass(returnType).getClassName();
		clazz.addMethod(clazz.searchByReturnType(internalName) + param, name);
		isComplete = true;
	}
}
