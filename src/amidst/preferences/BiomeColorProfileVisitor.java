package amidst.preferences;

public interface BiomeColorProfileVisitor {
	void enterFolder(String name);

	void visitProfile(BiomeColorProfile profile);

	void leaveFolder();
}
