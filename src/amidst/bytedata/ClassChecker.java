package amidst.bytedata;

import amidst.bytedata.builder.ClassCheckerBuilder;
import amidst.minecraft.Minecraft;

public abstract class ClassChecker {
	public static ClassCheckerBuilder builder() {
		return new ClassCheckerBuilder();
	}

	private String name;

	public ClassChecker(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract boolean isMatching(ByteClass byteClass);

	public abstract void execute(Minecraft mc, ByteClass byteClass);

	@Override
	public String toString() {
		return name + " | " + getClass().getSimpleName();
	}
}
