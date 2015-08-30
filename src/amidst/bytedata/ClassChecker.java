package amidst.bytedata;

import amidst.bytedata.builder.ClassCheckerBuilder;
import amidst.minecraft.Minecraft;

public abstract class ClassChecker {
	public static ClassCheckerBuilder builder() {
		return new ClassCheckerBuilder();
	}

	private String name;
	private boolean isComplete = false;
	private int passes = 10;

	public ClassChecker(String name) {
		this.name = name;
	}

	public int getPasses() {
		return passes;
	}

	public void decreasePasses() {
		passes--;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public String getName() {
		return name;
	}

	protected void complete() {
		isComplete = true;
	}

	public abstract void check(Minecraft mc, ByteClass bClass);
}
