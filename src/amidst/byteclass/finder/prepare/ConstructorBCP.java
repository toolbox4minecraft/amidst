package amidst.byteclass.finder.prepare;

import amidst.byteclass.ByteClass;

public class ConstructorBCP extends ByteClassPreparer {
	private String minecraftConstructorString;
	private String minecraftConstructorName;

	public ConstructorBCP(String minecraftConstructorString,
			String minecraftConstructorName) {
		this.minecraftConstructorString = minecraftConstructorString;
		this.minecraftConstructorName = minecraftConstructorName;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		byteClass.addConstructor(minecraftConstructorString,
				minecraftConstructorName);
	}
}
