package amidst.preferences;

public interface BiomeColorProfileVisitor {
	void enterDirectory(String name);

	void visitProfile(BiomeColorProfile profile);

	void leaveDirectory();
}
