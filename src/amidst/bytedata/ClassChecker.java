package amidst.bytedata;

import amidst.minecraft.Minecraft;


public class ClassChecker {
	protected String publicName;
	public boolean isComplete;
	public int passes = 10;
	public ClassChecker() {
		this.publicName = "unknown";
	}
	public ClassChecker(String publicName) {
		this.publicName = publicName;
	}
	public void check(Minecraft m, ByteClass bClass) {
		isComplete = true;
	}
	public String getName() {
		return publicName;
	}
	public ClassChecker passes(int p) {
		passes = p;
		return this;
	}
}
