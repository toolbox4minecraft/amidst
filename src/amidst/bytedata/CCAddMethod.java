package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCAddMethod extends NameLessAcceptingClassChecker {
	private String method;
	private String methodName;

	public CCAddMethod(String method, String methodName) {
		this.method = method;
		this.methodName = methodName;
	}

	@Override
	public void execute(Minecraft mc, ByteClass byteClass) {
		byteClass.addMethod(method, methodName);
	}
}
