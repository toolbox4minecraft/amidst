package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;

public class MethodBCP extends ByteClassPreparer {
	private String minecraftMethodString;
	private String minecraftMethodName;

	public MethodBCP(String minecraftMethodString, String minecraftMethodName) {
		this.minecraftMethodString = minecraftMethodString;
		this.minecraftMethodName = minecraftMethodName;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addMethod(minecraftMethodString, minecraftMethodName);
	}
}
