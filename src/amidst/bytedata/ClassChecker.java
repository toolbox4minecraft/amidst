package amidst.bytedata;

import amidst.minecraft.Minecraft;

public abstract class ClassChecker {
	protected String publicName;
	private boolean isComplete = false;
	private int passes = 10;

	public ClassChecker() {
		this.publicName = "unknown";
	}

	public ClassChecker(String publicName) {
		this.publicName = publicName;
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
		return publicName;
	}

	protected void complete() {
		isComplete = true;
	}

	public abstract void check(Minecraft m, ByteClass bClass);
}
